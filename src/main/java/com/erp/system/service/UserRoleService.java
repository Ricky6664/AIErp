package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysUser;

import java.util.List;

/**
 * 用户角色分配 Service 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
public interface UserRoleService extends IServiceX<SysUser> {

    void assignRoles(Long userId, List<Long> roleIds);

    void removeUserRole(Long userId, Long roleId);

    List<Long> getUserRoleIds(Long userId);

    List<Long> getUserIdsByRoleId(Long roleId);

    boolean hasRole(Long userId, Long roleId);
}
