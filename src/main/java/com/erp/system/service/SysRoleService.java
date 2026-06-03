package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysRole;

/**
 * 角色管理 Service 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
public interface SysRoleService extends IServiceX<SysRole> {

    boolean isRoleCodeUnique(String roleCode, Long excludeId);

    void updateStatus(Long roleId, Boolean enabled);

    void deleteRoleWithCleanup(Long roleId);
}
