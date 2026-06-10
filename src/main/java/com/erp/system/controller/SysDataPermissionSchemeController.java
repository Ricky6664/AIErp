package com.erp.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.system.entity.SysDataPermissionScheme;
import com.erp.system.service.SysDataPermissionSchemeService;
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
 * 数据权限方案 Controller.
 *
 * @author AI
 * @since 2026-06-03
 */
@Tag(name = "数据权限方案管理", description = "数据权限方案的CRUD接口")
@RestController
@RequestMapping("/api/system/data-permission-scheme")
@RequiredArgsConstructor
public class SysDataPermissionSchemeController {

    private final SysDataPermissionSchemeService schemeService;

    @Operation(summary = "分页查询数据权限方案列表")
    @SaCheckPermission("system:data-permission-scheme:query")
    @GetMapping
    public RT<PageResult<SysDataPermissionScheme>> pageList(PageQuery query,
            @RequestParam(required = false) String schemeName,
            @RequestParam(required = false) String schemeCode) {
        LambdaQueryWrapper<SysDataPermissionScheme> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(schemeName)) {
            wrapper.like(SysDataPermissionScheme::getSchemeName, schemeName);
        }
        if (StringUtils.hasText(schemeCode)) {
            wrapper.like(SysDataPermissionScheme::getSchemeCode, schemeCode);
        }
        wrapper.orderByDesc(SysDataPermissionScheme::getCreateTime);
        return RT.ok(schemeService.pageList(query, wrapper));
    }

    @Operation(summary = "查询数据权限方案详情")
    @SaCheckPermission("system:data-permission-scheme:query")
    @GetMapping("/{id}")
    public RT<SysDataPermissionScheme> getById(@PathVariable Long id) {
        SysDataPermissionScheme entity = schemeService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return RT.ok(entity);
    }

    @Operation(summary = "新增数据权限方案")
    @SaCheckPermission("system:data-permission-scheme:add")
    @PostMapping
    public RT<Long> create(@Valid @RequestBody SysDataPermissionScheme entity, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        schemeService.save(entity);
        return RT.ok(entity.getId());
    }

    @Operation(summary = "修改数据权限方案")
    @SaCheckPermission("system:data-permission-scheme:edit")
    @PutMapping("/{id}")
    public RT<Void> update(@PathVariable Long id, @Valid @RequestBody SysDataPermissionScheme entity,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RT.paramError(getErrorMsg(bindingResult));
        }
        SysDataPermissionScheme existing = schemeService.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        entity.setId(id);
        schemeService.updateById(entity);
        return RT.ok();
    }

    @Operation(summary = "删除数据权限方案")
    @SaCheckPermission("system:data-permission-scheme:delete")
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
