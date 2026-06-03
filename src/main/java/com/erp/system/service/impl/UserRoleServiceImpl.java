package com.erp.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysUser;
import com.erp.system.mapper.UserMapper;
import com.erp.system.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 用户角色分配 Service 实现.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
public class UserRoleServiceImpl extends ServiceImplX<UserMapper, SysUser> implements UserRoleService {

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
    public void removeUserRole(Long userId, Long roleId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        int count = baseMapper.countUserRole(userId, roleId);
        if (count == 0) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND,
                    "用户角色关联不存在: userId=" + userId + ", roleId=" + roleId);
        }

        baseMapper.deleteUserRole(userId, roleId);

        try {
            StpUtil.kickout(userId);
        } catch (Exception e) {
            log.debug("角色移除踢出用户失败(用户可能未在线): userId={}", userId);
        }

        log.info("用户角色移除完成: userId={}, roleId={}", userId, roleId);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        if (getById(userId) == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        List<Long> roleIds = baseMapper.selectRoleIdsByUserId(userId);
        return roleIds != null ? roleIds : Collections.emptyList();
    }

    @Override
    public List<Long> getUserIdsByRoleId(Long roleId) {
        List<Long> userIds = baseMapper.selectUserIdsByRoleId(roleId);
        return userIds != null ? userIds : Collections.emptyList();
    }

    @Override
    public boolean hasRole(Long userId, Long roleId) {
        return baseMapper.countUserRole(userId, roleId) > 0;
    }
}
