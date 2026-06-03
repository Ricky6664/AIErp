package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysRole;

import java.util.List;

/**
 * 角色菜单权限 Service 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
public interface SysRoleMenuService extends IServiceX<SysRole> {

    void assignMenus(Long roleId, List<Long> menuIds);

    void assignMenusWithPermissions(Long roleId, List<Long> menuIds, String permissionType);

    void removeRoleMenus(Long roleId, List<Long> menuIds);

    List<Long> getRoleMenuIds(Long roleId);

    boolean hasMenuPermission(Long roleId, Long menuId, String permissionType);

    void copyMenus(Long sourceRoleId, Long targetRoleId);
}
