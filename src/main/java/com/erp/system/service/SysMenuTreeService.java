package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysMenu;

import java.util.List;

/**
 * 菜单树加载 Service 接口.
 *
 * @author AI
 * @since 2026-06-04
 */
public interface SysMenuTreeService extends IServiceX<SysMenu> {

    List<SysMenu> getMenuTree();

    List<SysMenu> getMenuTreeByUserId(Long userId);

    List<SysMenu> buildTree(List<SysMenu> menus, Long parentId);

    List<SysMenu> filterEmptyBranches(List<SysMenu> tree);
}
