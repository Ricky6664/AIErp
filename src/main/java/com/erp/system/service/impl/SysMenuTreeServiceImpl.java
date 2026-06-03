package com.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.service.ServiceImplX;
import com.erp.system.entity.SysMenu;
import com.erp.system.mapper.SysMenuMapper;
import com.erp.system.service.SysMenuTreeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单树加载 Service 实现.
 *
 * @author AI
 * @since 2026-06-04
 */
@Slf4j
@Service
public class SysMenuTreeServiceImpl extends ServiceImplX<SysMenuMapper, SysMenu> implements SysMenuTreeService {

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
    public List<SysMenu> buildTree(List<SysMenu> menus, Long parentId) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }
        return menus.stream()
                .filter(m -> Objects.equals(m.getParentId(), parentId))
                .sorted(Comparator.comparing(SysMenu::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .peek(m -> m.setChildren(buildTree(menus, m.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<SysMenu> filterEmptyBranches(List<SysMenu> tree) {
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
