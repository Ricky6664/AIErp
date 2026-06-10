package com.erp.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysRole;
import com.erp.system.mapper.SysRoleMapper;
import com.erp.system.mapper.UserMapper;
import com.erp.system.service.SysRoleMenuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 角色菜单权限 Service 实现.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
public class SysRoleMenuServiceImpl extends ServiceImplX<SysRoleMapper, SysRole> implements SysRoleMenuService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        assignMenusWithPermissions(roleId, menuIds, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenusWithPermissions(Long roleId, List<Long> menuIds, String permissionType) {
        if (roleId == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "角色ID不能为空");
        }
        if (!existsById(roleId)) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "角色不存在: id=" + roleId);
        }
        if (CollectionUtils.isEmpty(menuIds)) {
            log.warn("菜单ID列表为空, 跳过分配: roleId={}", roleId);
            return;
        }

        baseMapper.deleteRoleMenuAssociations(roleId);
        for (Long menuId : menuIds) {
            if (permissionType != null) {
                baseMapper.insertRoleMenuWithPermission(roleId, menuId, permissionType);
            } else {
                baseMapper.insertRoleMenu(roleId, menuId);
            }
        }

        kickoutUsersByRoleId(roleId);

        log.info("角色菜单分配完成: roleId={}, menuCount={}, permissionType={}", roleId, menuIds.size(), permissionType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRoleMenus(Long roleId, List<Long> menuIds) {
        if (roleId == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "角色ID不能为空");
        }
        if (CollectionUtils.isEmpty(menuIds)) {
            return;
        }

        baseMapper.deleteRoleMenuByMenuIds(roleId, menuIds);

        kickoutUsersByRoleId(roleId);

        log.info("角色菜单移除完成: roleId={}, removedMenuCount={}", roleId, menuIds.size());
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return baseMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public boolean hasMenuPermission(Long roleId, Long menuId, String permissionType) {
        if (roleId == null || menuId == null) {
            return false;
        }
        int count = baseMapper.countRoleMenuPermission(roleId, menuId, permissionType);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyMenus(Long sourceRoleId, Long targetRoleId) {
        if (sourceRoleId == null || targetRoleId == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "源角色ID和目标角色ID不能为空");
        }
        if (!existsById(sourceRoleId)) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "源角色不存在: id=" + sourceRoleId);
        }
        if (!existsById(targetRoleId)) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "目标角色不存在: id=" + targetRoleId);
        }

        List<Long> sourceMenuIds = baseMapper.selectMenuIdsByRoleId(sourceRoleId);
        if (CollectionUtils.isEmpty(sourceMenuIds)) {
            log.info("源角色无菜单权限, 跳过复制: sourceRoleId={}", sourceRoleId);
            return;
        }

        baseMapper.deleteRoleMenuAssociations(targetRoleId);
        for (Long menuId : sourceMenuIds) {
            baseMapper.insertRoleMenu(targetRoleId, menuId);
        }

        log.info("角色菜单权限复制完成: sourceRoleId={}, targetRoleId={}, menuCount={}",
                sourceRoleId, targetRoleId, sourceMenuIds.size());
    }

    private void kickoutUsersByRoleId(Long roleId) {
        List<Long> userIds = userMapper.selectUserIdsByRoleId(roleId);
        if (userIds != null) {
            for (Long userId : userIds) {
                try {
                    StpUtil.kickout(userId);
                } catch (Exception e) {
                    log.debug("角色菜单变更踢出用户失败(用户可能未在线): userId={}, roleId={}", userId, roleId);
                }
            }
        }
    }
}
