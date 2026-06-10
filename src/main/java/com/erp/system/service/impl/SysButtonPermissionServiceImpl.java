package com.erp.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysMenu;
import com.erp.system.mapper.SysMenuMapper;
import com.erp.system.service.SysButtonPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 按钮权限校验 Service 实现.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
public class SysButtonPermissionServiceImpl
        extends ServiceImplX<SysMenuMapper, SysMenu>
        implements SysButtonPermissionService {

    @Override
    public boolean checkPermission(String permissionCode) {
        if (permissionCode == null || permissionCode.isEmpty()) {
            return false;
        }
        return StpUtil.hasPermission(permissionCode);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return baseMapper.selectPermissionCodesByUserId(userId);
    }

    @Override
    public List<String> getRolePermissions(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return baseMapper.selectPermissionCodesByRoleId(roleId);
    }

    @Override
    public List<SysMenu> getButtonsByMenuId(Long menuId) {
        if (menuId == null) {
            return Collections.emptyList();
        }
        return baseMapper.selectButtonsByParentId(menuId);
    }

    @Override
    public List<SysMenu> getButtonsByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return baseMapper.selectButtonsByUserId(userId);
    }

    @Override
    public void refreshCache(Long userId) {
        if (userId == null) {
            return;
        }
        try {
            StpUtil.kickout(userId);
            log.info("用户权限缓存已刷新: userId={}", userId);
        } catch (Exception e) {
            log.debug("刷新用户权限缓存时踢出用户失败(用户可能未在线): userId={}", userId);
        }
    }
}
