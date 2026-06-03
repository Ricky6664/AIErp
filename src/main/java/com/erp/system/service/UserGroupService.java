package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysUserGroup;

import java.util.List;

/**
 * 用户组 Service 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
public interface UserGroupService extends IServiceX<SysUserGroup> {

    void addMembers(Long groupId, List<Long> userIds);

    void removeMember(Long groupId, Long userId);

    void removeAllMembers(Long groupId);

    List<Long> getMemberUserIds(Long groupId);

    List<Long> getGroupIdsByUserId(Long userId);

    boolean hasMember(Long groupId, Long userId);

    void updateStatus(Long groupId, Boolean isEnabled);
}
