package com.erp.system.controller;

import com.erp.common.annotation.OperLog;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.system.dto.SysDataViewDTO;
import com.erp.system.service.SysDataViewService;
import com.erp.system.vo.SysDataViewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "数据视图管理", description = "数据视图CRUD、动态查询、字段元数据接口")
@RestController
@RequestMapping("/api/system/data-views")
@RequiredArgsConstructor
public class SysDataViewController {

    private final SysDataViewService sysDataViewService;

    @Operation(summary = "新增数据视图")
    @RequirePermission("system:data-view:create")
    @PostMapping
    public RT<SysDataViewVO.DetailVO> create(@Valid @RequestBody SysDataViewDTO.CreateDTO dto) {
        return RT.ok(sysDataViewService.create(dto));
    }

    @Operation(summary = "修改数据视图")
    @RequirePermission("system:data-view:update")
    @PutMapping("/{id}")
    public RT<SysDataViewVO.DetailVO> update(@PathVariable Long id, @Valid @RequestBody SysDataViewDTO.UpdateDTO dto) {
        return RT.ok(sysDataViewService.update(id, dto));
    }

    @Operation(summary = "删除数据视图")
    @RequirePermission("system:data-view:delete")
    @DeleteMapping("/{id}")
    public RT<Boolean> delete(@PathVariable Long id) {
        return RT.ok(sysDataViewService.delete(id));
    }

    @Operation(summary = "查询数据视图详情")
    @RequirePermission("system:data-view:query")
    @GetMapping("/{id}")
    public RT<SysDataViewVO.DetailVO> getById(@PathVariable Long id) {
        SysDataViewVO.DetailVO vo = sysDataViewService.getById(id);
        if (vo == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return RT.ok(vo);
    }

    @Operation(summary = "分页查询数据视图列表")
    @RequirePermission("system:data-view:query")
    @GetMapping("/page")
    public RT<PageResult<SysDataViewVO.DetailVO>> pageList(@Valid PageQuery query) {
        return RT.ok(sysDataViewService.pageList(query));
    }

    @Operation(summary = "执行数据视图动态查询")
    @RequirePermission("system:data-view:execute")
    @OperLog(value = "执行数据视图查询", type = "QUERY", recordResult = false)
    @PostMapping("/{viewCode}/execute")
    public RT<PageResult<Map<String, Object>>> execute(@PathVariable String viewCode, @RequestBody Map<String, Object> queryParams) {
        return RT.ok(sysDataViewService.executeView(viewCode, queryParams));
    }

    @Operation(summary = "获取数据视图字段元数据")
    @RequirePermission("system:data-view:query")
    @GetMapping("/{viewCode}/meta")
    public RT<SysDataViewVO.DetailVO> getMeta(@PathVariable String viewCode) {
        return RT.ok(sysDataViewService.getViewMeta(viewCode));
    }
}
