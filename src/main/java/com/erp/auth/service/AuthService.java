package com.erp.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.auth.entity.SysUser;
import com.erp.auth.exception.CaptchaException;
import com.erp.auth.mapper.SysUserMapper;
import com.erp.auth.vo.LoginResponse;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.AuthException;
import com.erp.common.exception.BusinessException;
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

        // 8. 更新最后登录信息
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(getClientIp(request));
        sysUserMapper.updateById(user);

        // 9. 异步记录登录日志
        recordLoginLog(user.getId(), username, request, "PASSWORD", true, null);

        // 10. 构造响应
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname() != null ? user.getNickname() : user.getUsername())
                .avatar(user.getAvatar())
                .menuTree(getMenuTree(user.getId()))
                .permissions(getPermissionList(user.getId()))
                .build();
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
        String ip = getClientIp(request);
        String browser = request.getHeader("User-Agent");
        String os = getOs(browser);
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

    // ==================== 工具方法 ====================

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String getOs(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "MacOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";
        return "Unknown";
    }

    // ==================== CaptchaException 独立定义 ====================

    // CaptchaException 在 com.erp.auth.exception 包中定义
}
