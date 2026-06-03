package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysRoleFieldPermission;

import java.util.List;

/**
 * 角色字段权限配置 Service 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
public interface SysRoleFieldPermissionService extends IServiceX<SysRoleFieldPermission> {

    List<SysRoleFieldPermission> getByRoleId(Long roleId);

    List<SysRoleFieldPermission> getByRoleIdAndTable(Long roleId, String tableName);

    void saveRoleFieldPermissions(Long roleId, List<SysRoleFieldPermission> permissions);

    void deleteByRoleId(Long roleId);

    String getPermissionType(Long roleId, String tableName, String fieldName);
}
