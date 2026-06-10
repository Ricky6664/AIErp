package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysMenu;

import java.util.List;

/**
 * 按钮权限校验 Service 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
public interface SysButtonPermissionService extends IServiceX<SysMenu> {

    boolean checkPermission(String permissionCode);

    List<String> getUserPermissions(Long userId);

    List<String> getRolePermissions(Long roleId);

    List<SysMenu> getButtonsByMenuId(Long menuId);

    List<SysMenu> getButtonsByUserId(Long userId);

    void refreshCache(Long userId);
}
