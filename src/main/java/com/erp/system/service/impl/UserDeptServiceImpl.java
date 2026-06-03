package com.erp.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysUser;
import com.erp.system.mapper.UserMapper;
import com.erp.system.service.UserDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 用户部门关联 Service 实现.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
public class UserDeptServiceImpl extends ServiceImplX<UserMapper, SysUser> implements UserDeptService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignDepts(Long userId, List<Long> deptIds) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        baseMapper.deleteUserDepts(userId);

        if (deptIds != null && !deptIds.isEmpty()) {
            baseMapper.insertUserDepts(userId, deptIds);
        }

        try {
            StpUtil.kickout(userId);
        } catch (Exception e) {
            log.debug("部门变更踢出用户失败(用户可能未在线): userId={}", userId);
        }

        log.info("用户部门分配完成: userId={}, deptCount={}", userId,
                deptIds != null ? deptIds.size() : 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserDept(Long userId, Long deptId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        int count = baseMapper.countUserDept(userId, deptId);
        if (count == 0) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND,
                    "用户部门关联不存在: userId=" + userId + ", deptId=" + deptId);
        }

        baseMapper.deleteUserDept(userId, deptId);

        try {
            StpUtil.kickout(userId);
        } catch (Exception e) {
            log.debug("部门移除踢出用户失败(用户可能未在线): userId={}", userId);
        }

        log.info("用户部门移除完成: userId={}, deptId={}", userId, deptId);
    }

    @Override
    public List<Long> getUserDeptIds(Long userId) {
        if (getById(userId) == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        List<Long> deptIds = baseMapper.selectDeptIdsByUserId(userId);
        return deptIds != null ? deptIds : Collections.emptyList();
    }

    @Override
    public List<Long> getUserIdsByDeptId(Long deptId) {
        List<Long> userIds = baseMapper.selectUserIdsByDeptId(deptId);
        return userIds != null ? userIds : Collections.emptyList();
    }

    @Override
    public boolean hasDept(Long userId, Long deptId) {
        return baseMapper.countUserDept(userId, deptId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPrimaryDept(Long userId, Long deptId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + userId);
        }

        int count = baseMapper.countUserDept(userId, deptId);
        if (count == 0) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND,
                    "用户部门关联不存在: userId=" + userId + ", deptId=" + deptId);
        }

        baseMapper.setPrimaryDept(userId, deptId);

        try {
            StpUtil.kickout(userId);
        } catch (Exception e) {
            log.debug("主部门设置踢出用户失败(用户可能未在线): userId={}", userId);
        }

        log.info("用户主部门设置完成: userId={}, deptId={}", userId, deptId);
    }
}
