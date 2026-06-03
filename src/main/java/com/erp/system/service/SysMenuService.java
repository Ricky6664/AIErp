package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysMenu;

import java.util.List;

/**
 * 菜单管理 Service 接口.
 *
 * @author AI
 * @since 2026-06-03
 */
public interface SysMenuService extends IServiceX<SysMenu> {

    List<SysMenu> getMenuTree();

    List<SysMenu> getMenuTreeByUserId(Long userId);

    boolean isPermissionCodeUnique(String permissionCode, Long excludeId);

    void updateStatus(Long menuId, Boolean enabled);

    void deleteMenuWithChildren(Long menuId);
}
