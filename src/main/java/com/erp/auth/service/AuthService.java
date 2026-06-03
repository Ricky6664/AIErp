package com.erp.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.auth.entity.AuthOnlineDevice;
import com.erp.auth.entity.SysUser;
import com.erp.auth.exception.CaptchaException;
import com.erp.auth.mapper.AuthOnlineDeviceMapper;
import com.erp.auth.mapper.SysUserMapper;
import com.erp.auth.vo.LoginResponse;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.AuthException;
import com.erp.common.exception.BusinessException;
import com.erp.util.IpAddressUtil;
import com.erp.util.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String LOGIN_FAIL_COUNT_PREFIX = "login:fail:";
    private static final String LOGIN_LOCK_PREFIX = "login:lock:";
    private static final long LOCK_DURATION_MINUTES = 15;
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final SysUserMapper sysUserMapper;
    private final AuthOnlineDeviceMapper authOnlineDeviceMapper;
    private final CaptchaService captchaService;
    private final LoginLogService loginLogService;
    private final StringRedisTemplate redisTemplate;

    /**
     * 执行登录认证.
     */
    public LoginResponse login(String username, String password, String captchaCode, String captchaKey,
                                HttpServletRequest request) {
        // 1. 检查锁定状态
        checkLockStatus(username);

        // 2. 校验验证码
        captchaService.verifyCaptcha(captchaKey, captchaCode);

        // 3. 查询用户
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            incrementLoginFailCount(username);
            throw new AuthException(ErrorCode.PASSWORD_ERROR);
        }

        // 4. 校验用户状态
        if ("disabled".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED, "账号已被禁用");
        }

        // 5. 校验密码
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            incrementLoginFailCount(username);
            recordLoginLog(user.getId(), username, request, "PASSWORD", false, "密码错误");
            throw new AuthException(ErrorCode.PASSWORD_ERROR);
        }

        // 6. 登录成功, 清除失败计数
        clearLoginFailCount(username);

        // 7. Sa-Token 登录
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        // 7.1 生成 refreshToken 并存入 Redis (双Token机制)
        String refreshToken = java.util.UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                "refresh:token:" + refreshToken,
                String.valueOf(user.getId()),
                7, TimeUnit.DAYS);

        // 8. 更新最后登录信息
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(IpAddressUtil.getClientIp(request));
        sysUserMapper.updateById(user);

        // 9. 异步记录登录日志
        recordLoginLog(user.getId(), username, request, "PASSWORD", true, null);

        // 10. 构造响应
        return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname() != null ? user.getNickname() : user.getUsername())
                .avatar(user.getAvatar())
                .menuTree(getMenuTree(user.getId()))
                .permissions(getPermissionList(user.getId()))
                .build();
    }

    /**
     * 执行退出登录.
     */
    public void logout() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            String tokenValue = StpUtil.getTokenValue();

            // 1. 更新在线设备状态为"已下线"
            updateOnlineDeviceStatus(tokenValue);

            // 2. 注销Token, 清除Sa-Token Session
            StpUtil.logout();

            // 3. 清除Redis权限缓存
            redisTemplate.delete("user:permission:" + userId);
            redisTemplate.delete("user:menu:" + userId);

            // 4. 异步写入登出时间
            loginLogService.updateLogoutTime(userId);

            log.info("用户已退出登录: userId={}", userId);
        } catch (cn.dev33.satoken.exception.NotLoginException e) {
            log.info("退出登录时Token已过期或不存在(幂等): {}", e.getMessage());
        }
    }

    /**
     * 校验当前Token有效性, 并自动续期.
     */
    public com.erp.auth.vo.TokenVerifyResponse verifyToken() {
        try {
            StpUtil.checkLogin();
        } catch (cn.dev33.satoken.exception.NotLoginException e) {
            if (cn.dev33.satoken.exception.NotLoginException.TOKEN_TIMEOUT.equals(e.getType())) {
                throw new AuthException(ErrorCode.TOKEN_EXPIRED);
            }
            throw new AuthException(ErrorCode.UNAUTHORIZED);
        }
        long userId = StpUtil.getLoginIdAsLong();
        long expireInSeconds = StpUtil.getTokenTimeout();

        updateDeviceLastActiveTime(StpUtil.getTokenValue());

        log.debug("Token校验成功: userId={}, expireInSeconds={}", userId, expireInSeconds);
        return new com.erp.auth.vo.TokenVerifyResponse(true, userId, expireInSeconds);
    }

    /**
     * 刷新Token: 用refreshToken换取新的accessToken+refreshToken对.
     */
    public com.erp.auth.vo.TokenRefreshResponse refreshToken(String refreshTokenValue) {
        // 1. 校验refreshToken
        String redisKey = "refresh:token:" + refreshTokenValue;
        String userIdStr = redisTemplate.opsForValue().get(redisKey);
        if (userIdStr == null) {
            throw new AuthException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 2. 删除旧refreshToken (一次性使用, 防止重放)
        redisTemplate.delete(redisKey);

        long userId = Long.parseLong(userIdStr);

        // 3. 获取旧accessToken (用于平滑替换)
        String oldToken = StpUtil.getTokenValue();

        // 4. 创建新会话
        StpUtil.login(userId);
        String newToken = StpUtil.getTokenValue();

        // 5. Token平滑替换: 旧Token在宽限期内仍可用
        if (oldToken != null && !oldToken.equals(newToken)) {
            StpUtil.replaced(oldToken, newToken);
        }

        // 6. 生成新refreshToken
        String newRefreshToken = java.util.UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                "refresh:token:" + newRefreshToken,
                String.valueOf(userId),
                7, TimeUnit.DAYS);

        // 7. 更新设备活跃时间
        updateDeviceLastActiveTime(newToken);

        long expiresIn = StpUtil.getTokenTimeout();
        log.info("Token刷新成功: userId={}, newToken={}, expiresIn={}", userId, newToken, expiresIn);
        return com.erp.auth.vo.TokenRefreshResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .expiresIn(expiresIn)
                .build();
    }

    private void updateDeviceLastActiveTime(String tokenValue) {
        try {
            AuthOnlineDevice device = authOnlineDeviceMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AuthOnlineDevice>()
                            .eq(AuthOnlineDevice::getSessionTokenId, tokenValue)
                            .last("LIMIT 1")
            );
            if (device != null) {
                device.setLastActiveTime(LocalDateTime.now());
                authOnlineDeviceMapper.updateById(device);
            }
        } catch (Exception e) {
            log.debug("更新设备活跃时间失败(可能表未初始化): {}", e.getMessage());
        }
    }

    private void updateOnlineDeviceStatus(String tokenValue) {
        try {
            AuthOnlineDevice device = authOnlineDeviceMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AuthOnlineDevice>()
                            .eq(AuthOnlineDevice::getSessionTokenId, tokenValue)
                            .last("LIMIT 1")
            );
            if (device != null) {
                device.setStatus("已下线");
                authOnlineDeviceMapper.updateById(device);
            }
        } catch (Exception e) {
            log.debug("更新在线设备状态失败(可能表未初始化): {}", e.getMessage());
        }
    }

    // ==================== 锁定检查 ====================

    private void checkLockStatus(String username) {
        String lockKey = LOGIN_LOCK_PREFIX + username;
        String locked = redisTemplate.opsForValue().get(lockKey);
        if (locked != null) {
            throw new AuthException(ErrorCode.ACCOUNT_LOCKED);
        }
    }

    // ==================== 失败计数 ====================

    private void incrementLoginFailCount(String username) {
        String failKey = LOGIN_FAIL_COUNT_PREFIX + username;
        Long count = redisTemplate.opsForValue().increment(failKey);
        redisTemplate.expire(failKey, LOCK_DURATION_MINUTES, TimeUnit.MINUTES);
        if (count != null && count >= MAX_LOGIN_ATTEMPTS) {
            String lockKey = LOGIN_LOCK_PREFIX + username;
            redisTemplate.opsForValue().set(lockKey, "1", LOCK_DURATION_MINUTES, TimeUnit.MINUTES);
            log.warn("账号已锁定: username={}, 失败次数={}", username, count);
        }
    }

    private void clearLoginFailCount(String username) {
        String failKey = LOGIN_FAIL_COUNT_PREFIX + username;
        String lockKey = LOGIN_LOCK_PREFIX + username;
        redisTemplate.delete(failKey);
        redisTemplate.delete(lockKey);
    }

    // ==================== 日志 ====================

    private void recordLoginLog(Long userId, String username, HttpServletRequest request,
                                 String loginMethod, boolean success, String failReason) {
        String ip = IpAddressUtil.getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String browser = UserAgentUtil.parseBrowser(userAgent);
        String os = UserAgentUtil.parseOs(userAgent);
        if (success) {
            loginLogService.logSuccess(userId, ip, browser, os, loginMethod);
        } else {
            loginLogService.logFailure(userId, username, ip, browser, os, loginMethod, failReason);
        }
    }

    // ==================== 权限与菜单 ====================

    private List<LoginResponse.MenuTreeNode> getMenuTree(Long userId) {
        // 后续任务实现: 从 sys_role_menu + sys_menu 查询用户菜单权限树
        return Collections.emptyList();
    }

    private List<String> getPermissionList(Long userId) {
        // 后续任务实现: 通过 StpInterfaceImpl 从数据库查询权限标识列表
        try {
            Object permissions = StpUtil.getSession().get("permissionCodeList");
            if (permissions instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> permList = (List<String>) permissions;
                return permList;
            }
        } catch (Exception e) {
            log.debug("获取权限列表失败: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    // ==================== CaptchaException 独立定义 ====================

    // CaptchaException 在 com.erp.auth.exception 包中定义
}
