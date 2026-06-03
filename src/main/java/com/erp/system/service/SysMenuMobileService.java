package com.erp.system.service;

import com.erp.common.service.IServiceX;
import com.erp.system.entity.SysMenu;

import java.util.List;

/**
 * 移动端菜单管理 Service 接口.
 *
 * @author AI
 * @since 2026-06-04
 */
public interface SysMenuMobileService extends IServiceX<SysMenu> {

    List<SysMenu> getMobileMenuTree();

    List<SysMenu> getMobileMenuTreeByUserId(Long userId);
}
