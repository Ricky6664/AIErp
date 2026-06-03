package com.erp.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysUser;
import com.erp.system.mapper.UserMapper;
import com.erp.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class UserServiceImpl extends ServiceImplX<UserMapper, SysUser> implements UserService {

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

        log.info("用户已解锁: userId={}", userId);
    }

    @Override
    public List<String> getRoleNames(Long userId) {
        List<String> roleNames = baseMapper.selectRoleNamesByUserId(userId);
        return roleNames != null ? roleNames : Collections.emptyList();
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("TODO: implement in P0-004-002-007-001-002");
    }

    @Override
    public boolean isUsernameUnique(String username, Long excludeId) {
        return baseMapper.countByUsername(username, excludeId) == 0;
    }
}
