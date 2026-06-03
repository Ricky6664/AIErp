package com.erp.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.system.entity.SysFieldPermissionScheme;
import com.erp.system.service.SysFieldPermissionSchemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

/**
 * 字段权限方案 Controller.
 *
 * @author AI
 * @since 2026-06-03
 */
@Tag(name = "字段权限方案管理", description = "字段权限方案的CRUD接口")
@RestController
@RequestMapping("/api/system/field-permission-scheme")
@RequiredArgsConstructor
public class SysFieldPermissionSchemeController {

    private final SysFieldPermissionSchemeService schemeService;

    @Operation(summary = "分页查询字段权限方案列表")
    @SaCheckPermission("system:field-permission-scheme:query")
    @GetMapping
    public RT<PageResult<SysFieldPermissionScheme>> pageList(PageQuery query,
            @RequestParam(required = false) String schemeName,
            @RequestParam(required = false) String schemeCode,
            @RequestParam(required = false) String tableName) {
        LambdaQueryWrapper<SysFieldPermissionScheme> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(schemeName)) {
            wrapper.like(SysFieldPermissionScheme::getSchemeName, schemeName);
        }
        if (StringUtils.hasText(schemeCode)) {
            wrapper.like(SysFieldPermissionScheme::getSchemeCode, schemeCode);
        }
        if (StringUtils.hasText(tableName)) {
            wrapper.eq(SysFieldPermissionScheme::getTableName, tableName);
        }
        wrapper.orderByDesc(SysFieldPermissionScheme::getCreateTime);
        return RT.ok(schemeService.pageList(query, wrapper));
    }

    @Operation(summary = "查询字段权限方案详情")
    @SaCheckPermission("system:field-permission-scheme:query")
    @GetMapping("/{id}")
    public RT<SysFieldPermissionScheme> getById(@PathVariable Long id) {
        SysFieldPermissionScheme entity = schemeService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return RT.ok(entity);
    }

    @Operation(summary = "新增字段权限方案")
    @SaCheckPermission("system:field-permission-scheme:add")
    @PostMapping
    public RT<Long> create(@Valid @RequestBody SysFieldPermissionScheme entity, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        schemeService.save(entity);
        return RT.ok(entity.getId());
    }

    @Operation(summary = "修改字段权限方案")
    @SaCheckPermission("system:field-permission-scheme:edit")
    @PutMapping("/{id}")
    public RT<Void> update(@PathVariable Long id, @Valid @RequestBody SysFieldPermissionScheme entity,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        SysFieldPermissionScheme existing = schemeService.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        entity.setId(id);
        schemeService.updateById(entity);
        return RT.ok();
    }

    @Operation(summary = "删除字段权限方案")
    @SaCheckPermission("system:field-permission-scheme:delete")
    @DeleteMapping("/{id}")
    public RT<Void> delete(@PathVariable Long id) {
        schemeService.removeById(id);
        return RT.ok();
    }

    private String getErrorMsg(BindingResult bindingResult) {
        return bindingResult.getFieldError() != null
                ? bindingResult.getFieldError().getDefaultMessage()
                : "参数校验失败";
    }
}
