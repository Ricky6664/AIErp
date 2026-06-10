package com.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysMenu;
import com.erp.system.mapper.SysMenuMapper;
import com.erp.system.service.SysMenuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单管理 Service 实现.
 *
 * @author AI
 * @since 2026-06-04
 */
@Slf4j
@Service
public class SysMenuServiceImpl extends ServiceImplX<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> getMenuTree() {
        List<SysMenu> allMenus = baseMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .orderByAsc(SysMenu::getSortOrder)
        );
        return buildTree(allMenus, null);
    }

    @Override
    public List<SysMenu> getMenuTreeByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        List<String> userPermissions = sysMenuMapper.selectPermissionCodesByUserId(userId);

        List<SysMenu> allMenus = baseMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getIsEnabled, true)
                        .eq(SysMenu::getIsVisible, true)
                        .orderByAsc(SysMenu::getSortOrder)
        );

        if (CollectionUtils.isEmpty(userPermissions)) {
            return Collections.emptyList();
        }

        List<SysMenu> filteredMenus = allMenus.stream()
                .filter(m -> m.getPermissionCode() == null
                        || m.getPermissionCode().isEmpty()
                        || userPermissions.contains(m.getPermissionCode()))
                .collect(Collectors.toList());

        List<SysMenu> tree = buildTree(filteredMenus, null);

        return filterEmptyBranches(tree);
    }

    @Override
    public boolean isPermissionCodeUnique(String permissionCode, Long excludeId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getPermissionCode, permissionCode);
        if (excludeId != null) {
            wrapper.ne(SysMenu::getId, excludeId);
        }
        return baseMapper.selectCount(wrapper) == 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long menuId, Boolean enabled) {
        if (menuId == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "菜单ID不能为空");
        }
        SysMenu menu = baseMapper.selectById(menuId);
        if (menu == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "菜单不存在: id=" + menuId);
        }
        menu.setIsEnabled(enabled);
        baseMapper.updateById(menu);
        log.info("菜单状态更新: menuId={}, enabled={}", menuId, enabled);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenuWithChildren(Long menuId) {
        if (menuId == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "菜单ID不能为空");
        }
        if (!existsById(menuId)) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "菜单不存在: id=" + menuId);
        }

        List<SysMenu> allMenus = baseMapper.selectList(null);

        List<Long> idsToDelete = new ArrayList<>();
        idsToDelete.add(menuId);
        collectChildIds(allMenus, menuId, idsToDelete);

        baseMapper.deleteByIds(idsToDelete);
        log.info("菜单级联删除: rootId={}, deletedCount={}", menuId, idsToDelete.size());
    }

    private List<SysMenu> buildTree(List<SysMenu> menus, Long parentId) {
        return menus.stream()
                .filter(m -> Objects.equals(m.getParentId(), parentId))
                .sorted(Comparator.comparing(SysMenu::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .peek(m -> m.setChildren(buildTree(menus, m.getId())))
                .collect(Collectors.toList());
    }

    private void collectChildIds(List<SysMenu> allMenus, Long parentId, List<Long> result) {
        List<SysMenu> children = allMenus.stream()
                .filter(m -> Objects.equals(m.getParentId(), parentId))
                .collect(Collectors.toList());
        for (SysMenu child : children) {
            result.add(child.getId());
            collectChildIds(allMenus, child.getId(), result);
        }
    }

    private List<SysMenu> filterEmptyBranches(List<SysMenu> tree) {
        if (tree == null) {
            return Collections.emptyList();
        }
        return tree.stream()
                .filter(m -> {
                    List<SysMenu> filteredChildren = filterEmptyBranches(m.getChildren());
                    m.setChildren(filteredChildren);
                    return m.getPermissionCode() != null && !m.getPermissionCode().isEmpty()
                            || !CollectionUtils.isEmpty(filteredChildren);
                })
                .collect(Collectors.toList());
    }
}
