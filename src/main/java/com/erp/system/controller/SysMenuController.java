package com.erp.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.system.dto.SysMenuDTO;
import com.erp.system.entity.SysMenu;
import com.erp.system.service.SysMenuMobileService;
import com.erp.system.service.SysMenuService;
import com.erp.system.service.SysMenuTreeService;
import com.erp.system.vo.SysMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "菜单管理", description = "菜单树CRUD、移动端菜单、权限编码管理接口")
@RestController
@RequestMapping("/api/system/menus")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;
    private final SysMenuTreeService sysMenuTreeService;
    private final SysMenuMobileService sysMenuMobileService;

    // ==================== 菜单CRUD ====================

    @Operation(summary = "分页查询菜单列表")
    @SaCheckPermission("system:menu:query")
    @GetMapping
    public RT<PageResult<SysMenuVO.ListVO>> pageList(
            @Valid PageQuery query, BindingResult bindingResult,
            @RequestParam(required = false) String menuName,
            @RequestParam(required = false) String menuType,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Boolean isEnabled) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(menuName)) {
            wrapper.like(SysMenu::getMenuName, menuName);
        }
        if (StringUtils.hasText(menuType)) {
            wrapper.eq(SysMenu::getMenuType, menuType);
        }
        if (parentId != null) {
            wrapper.eq(SysMenu::getParentId, parentId);
        }
        if (isEnabled != null) {
            wrapper.eq(SysMenu::getIsEnabled, isEnabled);
        }
        wrapper.orderByAsc(SysMenu::getSortOrder).orderByDesc(SysMenu::getCreateTime);
        PageResult<SysMenu> pageResult = sysMenuService.pageList(query, wrapper);
        List<SysMenuVO.ListVO> voList = pageResult.getList().stream()
                .map(this::toListVO)
                .collect(Collectors.toList());
        PageResult<SysMenuVO.ListVO> result = PageResult.of(voList,
                pageResult.getTotal(), pageResult.getPageNum(), pageResult.getPageSize());
        return RT.ok(result);
    }

    @Operation(summary = "查询菜单详情")
    @SaCheckPermission("system:menu:query")
    @GetMapping("/{id}")
    public RT<SysMenuVO.DetailVO> getById(@PathVariable Long id) {
        SysMenu entity = sysMenuService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return RT.ok(toDetailVO(entity));
    }

    @Operation(summary = "检查权限编码唯一性")
    @SaCheckPermission("system:menu:query")
    @GetMapping("/check-code")
    public RT<Boolean> checkPermissionCode(@RequestParam String permissionCode,
                                           @RequestParam(required = false) Long excludeId) {
        return RT.ok(sysMenuService.isPermissionCodeUnique(permissionCode, excludeId));
    }

    @Operation(summary = "新增菜单")
    @SaCheckPermission("system:menu:add")
    @PostMapping
    public RT<Long> create(@Valid @RequestBody SysMenuDTO.CreateDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        if (StringUtils.hasText(dto.getPermissionCode())
                && !sysMenuService.isPermissionCodeUnique(dto.getPermissionCode(), null)) {
            return RT.fail(ErrorCode.PARAM_DUPLICATE, "权限编码已存在");
        }
        SysMenu entity = toEntity(dto);
        sysMenuService.save(entity);
        return RT.ok(entity.getId());
    }

    @Operation(summary = "修改菜单")
    @SaCheckPermission("system:menu:edit")
    @PutMapping("/{id}")
    public RT<Void> update(@PathVariable Long id, @Valid @RequestBody SysMenuDTO.UpdateDTO dto,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        SysMenu entity = sysMenuService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (StringUtils.hasText(dto.getPermissionCode())
                && !dto.getPermissionCode().equals(entity.getPermissionCode())
                && !sysMenuService.isPermissionCodeUnique(dto.getPermissionCode(), id)) {
            return RT.fail(ErrorCode.PARAM_DUPLICATE, "权限编码已存在");
        }
        mergeEntity(entity, dto);
        sysMenuService.updateById(entity);
        return RT.ok();
    }

    @Operation(summary = "修改菜单状态(启用/禁用)")
    @SaCheckPermission("system:menu:edit")
    @PutMapping("/{id}/status")
    public RT<Void> updateStatus(@PathVariable Long id, @RequestBody SysMenuDTO.UpdateDTO dto) {
        sysMenuService.updateStatus(id, dto.getIsEnabled());
        return RT.ok();
    }

    @Operation(summary = "删除菜单(含子菜单)")
    @SaCheckPermission("system:menu:delete")
    @DeleteMapping("/{id}")
    public RT<Void> delete(@PathVariable Long id) {
        sysMenuService.deleteMenuWithChildren(id);
        return RT.ok();
    }

    // ==================== 菜单树 ====================

    @Operation(summary = "获取完整菜单树")
    @SaCheckPermission("system:menu:query")
    @GetMapping("/tree")
    public RT<List<SysMenu>> getMenuTree() {
        return RT.ok(sysMenuTreeService.getMenuTree());
    }

    @Operation(summary = "根据用户ID获取菜单树")
    @SaCheckPermission("system:menu:query")
    @GetMapping("/tree/user/{userId}")
    public RT<List<SysMenu>> getMenuTreeByUserId(@PathVariable Long userId) {
        return RT.ok(sysMenuTreeService.getMenuTreeByUserId(userId));
    }

    @Operation(summary = "获取当前用户菜单树(按权限过滤)")
    @GetMapping("/tree/current")
    public RT<List<SysMenu>> getCurrentUserMenuTree() {
        Long userId = StpUtil.getLoginIdAsLong();
        return RT.ok(sysMenuTreeService.getMenuTreeByUserId(userId));
    }

    // ==================== 移动端菜单 ====================

    @Operation(summary = "获取移动端菜单树")
    @SaCheckPermission("system:menu:query")
    @GetMapping("/mobile-tree")
    public RT<List<SysMenu>> getMobileMenuTree() {
        return RT.ok(sysMenuMobileService.getMobileMenuTree());
    }

    @Operation(summary = "根据用户ID获取移动端菜单树")
    @SaCheckPermission("system:menu:query")
    @GetMapping("/mobile-tree/user/{userId}")
    public RT<List<SysMenu>> getMobileMenuTreeByUserId(@PathVariable Long userId) {
        return RT.ok(sysMenuMobileService.getMobileMenuTreeByUserId(userId));
    }

    @Operation(summary = "获取当前用户移动端菜单树(按权限过滤)")
    @GetMapping("/mobile-tree/current")
    public RT<List<SysMenu>> getCurrentUserMobileMenuTree() {
        Long userId = StpUtil.getLoginIdAsLong();
        return RT.ok(sysMenuMobileService.getMobileMenuTreeByUserId(userId));
    }

    // ==================== 移动端菜单CRUD ====================

    @Operation(summary = "新增移动端菜单")
    @SaCheckPermission("system:menu:add")
    @PostMapping("/mobile")
    public RT<Long> createMobile(@Valid @RequestBody SysMenuDTO.CreateDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        if (StringUtils.hasText(dto.getPermissionCode())
                && !sysMenuService.isPermissionCodeUnique(dto.getPermissionCode(), null)) {
            return RT.fail(ErrorCode.PARAM_DUPLICATE, "权限编码已存在");
        }
        SysMenu entity = toEntity(dto);
        sysMenuService.save(entity);
        return RT.ok(entity.getId());
    }

    @Operation(summary = "修改移动端菜单")
    @SaCheckPermission("system:menu:edit")
    @PutMapping("/mobile/{id}")
    public RT<Void> updateMobile(@PathVariable Long id, @Valid @RequestBody SysMenuDTO.UpdateDTO dto,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        SysMenu entity = sysMenuService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (StringUtils.hasText(dto.getPermissionCode())
                && !dto.getPermissionCode().equals(entity.getPermissionCode())
                && !sysMenuService.isPermissionCodeUnique(dto.getPermissionCode(), id)) {
            return RT.fail(ErrorCode.PARAM_DUPLICATE, "权限编码已存在");
        }
        mergeEntity(entity, dto);
        sysMenuService.updateById(entity);
        return RT.ok();
    }

    @Operation(summary = "删除移动端菜单(含子菜单)")
    @SaCheckPermission("system:menu:delete")
    @DeleteMapping("/mobile/{id}")
    public RT<Void> deleteMobile(@PathVariable Long id) {
        sysMenuService.deleteMenuWithChildren(id);
        return RT.ok();
    }

    // ==================== Helper Methods ====================

    private String getErrorMsg(BindingResult bindingResult) {
        return bindingResult.getFieldError() != null
                ? bindingResult.getFieldError().getDefaultMessage()
                : "参数校验失败";
    }

    private SysMenuVO.ListVO toListVO(SysMenu entity) {
        SysMenuVO.ListVO vo = new SysMenuVO.ListVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private SysMenuVO.DetailVO toDetailVO(SysMenu entity) {
        SysMenuVO.DetailVO vo = new SysMenuVO.DetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private SysMenu toEntity(SysMenuDTO.CreateDTO dto) {
        SysMenu entity = new SysMenu();
        BeanUtils.copyProperties(dto, entity);
        if (dto.getIsVisible() == null) {
            entity.setIsVisible(true);
        }
        if (dto.getIsEnabled() == null) {
            entity.setIsEnabled(true);
        }
        if (dto.getIsKeepAlive() == null) {
            entity.setIsKeepAlive(false);
        }
        if (dto.getIsExternalLink() == null) {
            entity.setIsExternalLink(false);
        }
        if (dto.getSortOrder() == null) {
            entity.setSortOrder(0);
        }
        if (dto.getParentId() == null) {
            entity.setParentId(0L);
        }
        return entity;
    }

    private void mergeEntity(SysMenu entity, SysMenuDTO.UpdateDTO dto) {
        if (dto.getParentId() != null) {
            entity.setParentId(dto.getParentId());
        }
        if (StringUtils.hasText(dto.getMenuName())) {
            entity.setMenuName(dto.getMenuName());
        }
        if (StringUtils.hasText(dto.getMenuType())) {
            entity.setMenuType(dto.getMenuType());
        }
        if (dto.getPermissionCode() != null) {
            entity.setPermissionCode(dto.getPermissionCode());
        }
        if (dto.getRoutePath() != null) {
            entity.setRoutePath(dto.getRoutePath());
        }
        if (dto.getRouteName() != null) {
            entity.setRouteName(dto.getRouteName());
        }
        if (dto.getComponentPath() != null) {
            entity.setComponentPath(dto.getComponentPath());
        }
        if (dto.getIcon() != null) {
            entity.setIcon(dto.getIcon());
        }
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
        if (dto.getIsVisible() != null) {
            entity.setIsVisible(dto.getIsVisible());
        }
        if (dto.getIsEnabled() != null) {
            entity.setIsEnabled(dto.getIsEnabled());
        }
        if (dto.getIsKeepAlive() != null) {
            entity.setIsKeepAlive(dto.getIsKeepAlive());
        }
        if (dto.getIsExternalLink() != null) {
            entity.setIsExternalLink(dto.getIsExternalLink());
        }
        if (dto.getExternalUrl() != null) {
            entity.setExternalUrl(dto.getExternalUrl());
        }
    }
}
