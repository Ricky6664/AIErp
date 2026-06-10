package com.erp.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.auth.service.AuthPasswordPolicyService;
import com.erp.auth.service.LoginAttemptService;
import com.erp.auth.entity.AuthPasswordPolicy;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysUser;
import com.erp.system.mapper.UserMapper;
import com.erp.system.service.UserService;
import com.erp.system.vo.UserWorkbenchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 用户管理 Service 实现.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImplX<UserMapper, SysUser> implements UserService {

    private final AuthPasswordPolicyService passwordPolicyService;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        baseMapper.deleteUserRoles(userId);

        if (roleIds != null && !roleIds.isEmpty()) {
            baseMapper.insertUserRoles(userId, roleIds);
        }

        try {
            StpUtil.kickout(userId);
        } catch (Exception e) {
            log.debug("角色变更踢出用户失败(用户可能未在线): userId={}", userId);
        }

        log.info("用户角色分配完成: userId={}, roleCount={}", userId,
                roleIds != null ? roleIds.size() : 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        baseMapper.insertPasswordHistory(userId, hashedPassword);

        user.setPasswordHash(hashedPassword);
        user.setPwdResetAt(LocalDateTime.now());
        user.setPasswordExpireDate(calculatePasswordExpireDate());
        updateById(user);

        try {
            StpUtil.kickout(userId);
        } catch (Exception e) {
            log.debug("密码重置踢出用户失败(用户可能未在线): userId={}", userId);
        }

        log.info("用户密码重置完成: userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long userId, String status) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        if (!"normal".equals(status) && !"disabled".equals(status)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "用户状态仅支持 normal/disabled: " + status);
        }

        String oldStatus = user.getStatus();
        user.setStatus(status);
        updateById(user);

        if ("disabled".equals(status)) {
            try {
                StpUtil.kickout(userId);
            } catch (Exception e) {
                log.debug("禁用用户踢出失败(用户可能未在线): userId={}", userId);
            }
        }

        log.info("用户状态更新: userId={}, {} -> {}", userId, oldStatus, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockUser(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        user.setIsLocked(false);
        user.setLockedUntil(null);
        updateById(user);

        loginAttemptService.resetFailCount(user.getUsername());
        log.info("用户已解锁: userId={}, username={}", userId, user.getUsername());
    }

    @Override
    public List<String> getRoleNames(Long userId) {
        List<String> roleNames = baseMapper.selectRoleNamesByUserId(userId);
        return roleNames != null ? roleNames : Collections.emptyList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "用户ID不能为空");
        }
        if (oldPassword == null || oldPassword.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "旧密码不能为空");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "新密码不能为空");
        }
        if (newPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "新密码长度不能少于6位");
        }

        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        if (!BCrypt.checkpw(oldPassword, user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        if (BCrypt.checkpw(newPassword, user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PARAM_DUPLICATE, "新密码不能与旧密码相同");
        }

        List<String> recentPasswords = baseMapper.selectPasswordHistory(userId, 3);
        if (recentPasswords != null) {
            for (String historyHash : recentPasswords) {
                if (BCrypt.checkpw(newPassword, historyHash)) {
                    throw new BusinessException(ErrorCode.PARAM_DUPLICATE, "新密码不能与最近3次使用过的密码相同");
                }
            }
        }

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        baseMapper.insertPasswordHistory(userId, hashedPassword);

        user.setPasswordHash(hashedPassword);
        user.setPwdResetAt(LocalDateTime.now());
        user.setPasswordExpireDate(calculatePasswordExpireDate());
        updateById(user);

        try {
            StpUtil.kickout(userId);
        } catch (Exception e) {
            log.debug("密码修改踢出用户失败(用户可能未在线): userId={}", userId);
        }

        log.info("用户密码修改完成: userId={}", userId);
    }

    @Override
    public boolean isUsernameUnique(String username, Long excludeId) {
        return baseMapper.countByUsername(username, excludeId) == 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String resetPasswordAndReturn(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        String plainPassword = generateRandomPassword();
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        baseMapper.insertPasswordHistory(userId, hashedPassword);

        user.setPasswordHash(hashedPassword);
        user.setPwdResetAt(LocalDateTime.now());
        user.setPasswordExpireDate(calculatePasswordExpireDate());
        updateById(user);

        try {
            StpUtil.kickout(userId);
        } catch (Exception e) {
            log.debug("密码重置踢出用户失败(用户可能未在线): userId={}", userId);
        }

        log.info("用户密码重置完成: userId={}", userId);
        return plainPassword;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserWithCleanup(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        baseMapper.deleteUserRoles(userId);
        baseMapper.deleteUserDepts(userId);

        removeById(userId);

        try {
            StpUtil.kickout(userId);
        } catch (Exception e) {
            log.debug("删除用户踢出失败(用户可能未在线): userId={}", userId);
        }

        log.info("用户已删除(含角色/部门关联清理): userId={}", userId);
    }

    @Override
    public UserWorkbenchVO getWorkbenchData() {
        UserWorkbenchVO stats = baseMapper.selectWorkbenchStats();
        if (stats == null) {
            stats = new UserWorkbenchVO();
        }
        stats.setRoleDistribution(baseMapper.selectRoleDistribution());
        stats.setLoginTrend(baseMapper.selectLoginTrend());
        stats.setRecentLogins(baseMapper.selectRecentLogins(10));
        return stats;
    }

    @Override
    public boolean checkPasswordExpired(Long userId) {
        AuthPasswordPolicy policy = passwordPolicyService.getCurrentPolicy();
        if (policy == null || policy.getExpireDays() == null || policy.getExpireDays() == 0) {
            return false;
        }
        SysUser user = getById(userId);
        if (user == null || user.getPasswordExpireDate() == null) {
            return false;
        }
        return user.getPasswordExpireDate().isBefore(LocalDate.now());
    }

    private LocalDate calculatePasswordExpireDate() {
        AuthPasswordPolicy policy = passwordPolicyService.getCurrentPolicy();
        if (policy != null && policy.getExpireDays() != null && policy.getExpireDays() > 0) {
            return LocalDate.now().plusDays(policy.getExpireDays());
        }
        return null;
    }

    private static final String CHAR_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_DIGIT = "0123456789";
    private static final String CHAR_SPECIAL = "!@#$%&*";
    private static final String CHAR_ALL = CHAR_UPPER + CHAR_LOWER + CHAR_DIGIT + CHAR_SPECIAL;
    private static final SecureRandom RANDOM = new SecureRandom();

    private String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(8);
        sb.append(CHAR_UPPER.charAt(RANDOM.nextInt(CHAR_UPPER.length())));
        sb.append(CHAR_LOWER.charAt(RANDOM.nextInt(CHAR_LOWER.length())));
        sb.append(CHAR_DIGIT.charAt(RANDOM.nextInt(CHAR_DIGIT.length())));
        sb.append(CHAR_SPECIAL.charAt(RANDOM.nextInt(CHAR_SPECIAL.length())));
        for (int i = 4; i < 8; i++) {
            sb.append(CHAR_ALL.charAt(RANDOM.nextInt(CHAR_ALL.length())));
        }
        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }
        return new String(chars);
    }
}
