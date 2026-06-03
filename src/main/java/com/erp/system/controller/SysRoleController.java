package com.erp.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.system.dto.SysRoleDTO;
import com.erp.system.entity.SysRole;
import com.erp.system.entity.SysRoleDataScope;
import com.erp.system.entity.SysRoleFieldPermission;
import com.erp.system.service.SysButtonPermissionService;
import com.erp.system.service.SysRoleDataScopeService;
import com.erp.system.service.SysRoleFieldPermissionService;
import com.erp.system.service.SysRoleMenuService;
import com.erp.system.service.SysRoleService;
import com.erp.system.vo.SysRoleVO;
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

@Tag(name = "角色管理", description = "角色CRUD、菜单权限、数据权限、字段权限配置接口")
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;
    private final SysRoleMenuService sysRoleMenuService;
    private final SysRoleDataScopeService sysRoleDataScopeService;
    private final SysRoleFieldPermissionService sysRoleFieldPermissionService;
    private final SysButtonPermissionService sysButtonPermissionService;

    // ==================== 角色CRUD ====================

    @Operation(summary = "分页查询角色列表")
    @SaCheckPermission("system:role:query")
    @GetMapping
    public RT<PageResult<SysRoleVO.ListVO>> pageList(
            @Valid PageQuery query, BindingResult bindingResult,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Boolean isEnabled) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roleCode)) {
            wrapper.like(SysRole::getRoleCode, roleCode);
        }
        if (StringUtils.hasText(roleName)) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        if (isEnabled != null) {
            wrapper.eq(SysRole::getIsEnabled, isEnabled);
        }
        wrapper.orderByAsc(SysRole::getSortOrder).orderByDesc(SysRole::getCreateTime);
        PageResult<SysRole> pageResult = sysRoleService.pageList(query, wrapper);
        List<SysRoleVO.ListVO> voList = pageResult.getList().stream()
                .map(this::toListVO)
                .collect(Collectors.toList());
        PageResult<SysRoleVO.ListVO> result = PageResult.of(voList,
                pageResult.getTotal(), pageResult.getPageNum(), pageResult.getPageSize());
        return RT.ok(result);
    }

    @Operation(summary = "查询角色详情")
    @SaCheckPermission("system:role:query")
    @GetMapping("/{id}")
    public RT<SysRoleVO.DetailVO> getById(@PathVariable Long id) {
        SysRole entity = sysRoleService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return RT.ok(toDetailVO(entity));
    }

    @Operation(summary = "检查角色编码唯一性")
    @SaCheckPermission("system:role:query")
    @GetMapping("/check-code")
    public RT<Boolean> checkRoleCode(@RequestParam String roleCode,
                                     @RequestParam(required = false) Long excludeId) {
        return RT.ok(sysRoleService.isRoleCodeUnique(roleCode, excludeId));
    }

    @Operation(summary = "新增角色")
    @SaCheckPermission("system:role:add")
    @PostMapping
    public RT<Long> create(@Valid @RequestBody SysRoleDTO.CreateDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        if (!sysRoleService.isRoleCodeUnique(dto.getRoleCode(), null)) {
            return RT.fail(ErrorCode.PARAM_DUPLICATE, "角色编码已存在");
        }
        SysRole entity = toEntity(dto);
        sysRoleService.save(entity);
        return RT.ok(entity.getId());
    }

    @Operation(summary = "修改角色")
    @SaCheckPermission("system:role:edit")
    @PutMapping("/{id}")
    public RT<Void> update(@PathVariable Long id, @Valid @RequestBody SysRoleDTO.UpdateDTO dto,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        SysRole entity = sysRoleService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (StringUtils.hasText(dto.getRoleCode())
                && !dto.getRoleCode().equals(entity.getRoleCode())
                && !sysRoleService.isRoleCodeUnique(dto.getRoleCode(), id)) {
            return RT.fail(ErrorCode.PARAM_DUPLICATE, "角色编码已存在");
        }
        mergeEntity(entity, dto);
        sysRoleService.updateById(entity);
        return RT.ok();
    }

    @Operation(summary = "修改角色状态(启用/禁用)")
    @SaCheckPermission("system:role:edit")
    @PutMapping("/{id}/status")
    public RT<Void> updateStatus(@PathVariable Long id,
                                 @RequestBody SysRoleDTO.UpdateDTO dto) {
        sysRoleService.updateStatus(id, dto.getIsEnabled());
        return RT.ok();
    }

    @Operation(summary = "删除角色(软删除, 清理关联数据)")
    @SaCheckPermission("system:role:delete")
    @DeleteMapping("/{id}")
    public RT<Void> delete(@PathVariable Long id) {
        sysRoleService.deleteRoleWithCleanup(id);
        return RT.ok();
    }

    // ==================== 角色菜单权限 ====================

    @Operation(summary = "获取角色菜单权限ID列表")
    @SaCheckPermission("system:role:menu:query")
    @GetMapping("/{id}/menus")
    public RT<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        return RT.ok(sysRoleMenuService.getRoleMenuIds(id));
    }

    @Operation(summary = "分配角色菜单权限")
    @SaCheckPermission("system:role:menu:assign")
    @PostMapping("/{id}/menus")
    public RT<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysRoleMenuService.assignMenus(id, menuIds);
        return RT.ok();
    }

    @Operation(summary = "分配角色菜单权限(含权限类型)")
    @SaCheckPermission("system:role:menu:assign")
    @PostMapping("/{id}/menus-with-permissions")
    public RT<Void> assignMenusWithPermissions(@PathVariable Long id,
                                                @RequestBody List<Long> menuIds,
                                                @RequestParam String permissionType) {
        sysRoleMenuService.assignMenusWithPermissions(id, menuIds, permissionType);
        return RT.ok();
    }

    @Operation(summary = "移除角色菜单权限")
    @SaCheckPermission("system:role:menu:assign")
    @DeleteMapping("/{id}/menus")
    public RT<Void> removeRoleMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysRoleMenuService.removeRoleMenus(id, menuIds);
        return RT.ok();
    }

    @Operation(summary = "检查角色是否有某菜单权限")
    @SaCheckPermission("system:role:menu:query")
    @GetMapping("/{id}/menus/{menuId}/check")
    public RT<Boolean> checkMenuPermission(@PathVariable Long id,
                                            @PathVariable Long menuId,
                                            @RequestParam String permissionType) {
        return RT.ok(sysRoleMenuService.hasMenuPermission(id, menuId, permissionType));
    }

    @Operation(summary = "复制菜单权限(源角色→目标角色)")
    @SaCheckPermission("system:role:menu:assign")
    @PostMapping("/menus/copy")
    public RT<Void> copyMenus(@RequestParam Long sourceRoleId,
                               @RequestParam Long targetRoleId) {
        sysRoleMenuService.copyMenus(sourceRoleId, targetRoleId);
        return RT.ok();
    }

    // ==================== 角色数据权限 ====================

    @Operation(summary = "获取角色数据权限配置")
    @SaCheckPermission("system:role:data:query")
    @GetMapping("/{id}/data-scopes")
    public RT<List<SysRoleDataScope>> getDataScopes(@PathVariable Long id) {
        return RT.ok(sysRoleDataScopeService.getByRoleId(id));
    }

    @Operation(summary = "保存角色数据权限配置")
    @SaCheckPermission("system:role:data:assign")
    @PostMapping("/{id}/data-scopes")
    public RT<Void> saveDataScopes(@PathVariable Long id,
                                    @RequestBody List<SysRoleDataScope> scopes) {
        sysRoleDataScopeService.saveRoleDataScopes(id, scopes);
        return RT.ok();
    }

    @Operation(summary = "删除角色数据权限配置")
    @SaCheckPermission("system:role:data:assign")
    @DeleteMapping("/{id}/data-scopes")
    public RT<Void> deleteDataScopes(@PathVariable Long id) {
        sysRoleDataScopeService.deleteByRoleId(id);
        return RT.ok();
    }

    @Operation(summary = "获取角色数据权限范围类型")
    @SaCheckPermission("system:role:data:query")
    @GetMapping("/{id}/data-scopes/type")
    public RT<String> getDataScopeType(@PathVariable Long id) {
        return RT.ok(sysRoleDataScopeService.getScopeType(id));
    }

    // ==================== 角色字段权限 ====================

    @Operation(summary = "获取角色字段权限配置")
    @SaCheckPermission("system:role:field:query")
    @GetMapping("/{id}/field-permissions")
    public RT<List<SysRoleFieldPermission>> getFieldPermissions(@PathVariable Long id) {
        return RT.ok(sysRoleFieldPermissionService.getByRoleId(id));
    }

    @Operation(summary = "获取角色指定表的字段权限")
    @SaCheckPermission("system:role:field:query")
    @GetMapping("/{id}/field-permissions/{tableName}")
    public RT<List<SysRoleFieldPermission>> getFieldPermissionsByTable(
            @PathVariable Long id, @PathVariable String tableName) {
        return RT.ok(sysRoleFieldPermissionService.getByRoleIdAndTable(id, tableName));
    }

    @Operation(summary = "保存角色字段权限配置")
    @SaCheckPermission("system:role:field:assign")
    @PostMapping("/{id}/field-permissions")
    public RT<Void> saveFieldPermissions(@PathVariable Long id,
                                          @RequestBody List<SysRoleFieldPermission> permissions) {
        sysRoleFieldPermissionService.saveRoleFieldPermissions(id, permissions);
        return RT.ok();
    }

    @Operation(summary = "删除角色字段权限配置")
    @SaCheckPermission("system:role:field:assign")
    @DeleteMapping("/{id}/field-permissions")
    public RT<Void> deleteFieldPermissions(@PathVariable Long id) {
        sysRoleFieldPermissionService.deleteByRoleId(id);
        return RT.ok();
    }

    @Operation(summary = "获取角色指定字段的权限类型")
    @SaCheckPermission("system:role:field:query")
    @GetMapping("/{id}/field-permissions/{tableName}/{fieldName}/type")
    public RT<String> getFieldPermissionType(@PathVariable Long id,
                                              @PathVariable String tableName,
                                              @PathVariable String fieldName) {
        return RT.ok(sysRoleFieldPermissionService.getPermissionType(id, tableName, fieldName));
    }

    // ==================== 按钮权限查询 ====================

    @Operation(summary = "获取角色按钮权限列表")
    @SaCheckPermission("system:role:query")
    @GetMapping("/{id}/button-permissions")
    public RT<List<String>> getRoleButtonPermissions(@PathVariable Long id) {
        return RT.ok(sysButtonPermissionService.getRolePermissions(id));
    }

    // ==================== Helper Methods ====================

    private String getErrorMsg(BindingResult bindingResult) {
        return bindingResult.getFieldError() != null
                ? bindingResult.getFieldError().getDefaultMessage()
                : "参数校验失败";
    }

    private SysRoleVO.ListVO toListVO(SysRole entity) {
        SysRoleVO.ListVO vo = new SysRoleVO.ListVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private SysRoleVO.DetailVO toDetailVO(SysRole entity) {
        SysRoleVO.DetailVO vo = new SysRoleVO.DetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private SysRole toEntity(SysRoleDTO.CreateDTO dto) {
        SysRole entity = new SysRole();
        BeanUtils.copyProperties(dto, entity);
        if (dto.getIsEnabled() == null) {
            entity.setIsEnabled(true);
        }
        if (dto.getSortOrder() == null) {
            entity.setSortOrder(0);
        }
        return entity;
    }

    private void mergeEntity(SysRole entity, SysRoleDTO.UpdateDTO dto) {
        if (StringUtils.hasText(dto.getRoleCode())) {
            entity.setRoleCode(dto.getRoleCode());
        }
        if (StringUtils.hasText(dto.getRoleName())) {
            entity.setRoleName(dto.getRoleName());
        }
        if (dto.getRoleDesc() != null) {
            entity.setRoleDesc(dto.getRoleDesc());
        }
        if (dto.getDataScope() != null) {
            entity.setDataScope(dto.getDataScope());
        }
        if (dto.getIsEnabled() != null) {
            entity.setIsEnabled(dto.getIsEnabled());
        }
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
    }
}
