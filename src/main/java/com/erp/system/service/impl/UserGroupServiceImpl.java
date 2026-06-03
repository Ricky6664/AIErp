package com.erp.system.service.impl;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysUserGroup;
import com.erp.system.mapper.UserGroupMapper;
import com.erp.system.service.UserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 用户组 Service 实现.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
public class UserGroupServiceImpl extends ServiceImplX<UserGroupMapper, SysUserGroup> implements UserGroupService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMembers(Long groupId, List<Long> userIds) {
        SysUserGroup group = getById(groupId);
        if (group == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户组不存在: id=" + groupId);
        }

        if (userIds != null && !userIds.isEmpty()) {
            baseMapper.insertGroupMembers(groupId, userIds);
        }

        log.info("用户组成员添加完成: groupId={}, memberCount={}", groupId,
                userIds != null ? userIds.size() : 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long groupId, Long userId) {
        SysUserGroup group = getById(groupId);
        if (group == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户组不存在: id=" + groupId);
        }

        int count = baseMapper.countGroupMember(groupId, userId);
        if (count == 0) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND,
                    "用户组成员关联不存在: groupId=" + groupId + ", userId=" + userId);
        }

        baseMapper.deleteGroupMember(groupId, userId);

        log.info("用户组成员移除完成: groupId={}, userId={}", groupId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAllMembers(Long groupId) {
        SysUserGroup group = getById(groupId);
        if (group == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户组不存在: id=" + groupId);
        }

        baseMapper.deleteGroupMembers(groupId);

        log.info("用户组全部成员移除完成: groupId={}", groupId);
    }

    @Override
    public List<Long> getMemberUserIds(Long groupId) {
        if (getById(groupId) == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户组不存在: id=" + groupId);
        }

        List<Long> userIds = baseMapper.selectUserIdsByGroupId(groupId);
        return userIds != null ? userIds : Collections.emptyList();
    }

    @Override
    public List<Long> getGroupIdsByUserId(Long userId) {
        List<Long> groupIds = baseMapper.selectGroupIdsByUserId(userId);
        return groupIds != null ? groupIds : Collections.emptyList();
    }

    @Override
    public boolean hasMember(Long groupId, Long userId) {
        return baseMapper.countGroupMember(groupId, userId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long groupId, Boolean isEnabled) {
        SysUserGroup group = getById(groupId);
        if (group == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户组不存在: id=" + groupId);
        }

        group.setIsEnabled(isEnabled);
        updateById(group);

        log.info("用户组状态更新完成: groupId={}, isEnabled={}", groupId, isEnabled);
    }
}
