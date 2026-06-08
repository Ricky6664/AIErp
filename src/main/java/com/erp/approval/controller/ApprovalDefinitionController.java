package com.erp.approval.controller;

import com.erp.approval.dto.DefinitionCreateDTO;
import com.erp.approval.dto.DefinitionQueryDTO;
import com.erp.approval.dto.DefinitionUpdateDTO;
import com.erp.approval.service.IApprovalDefinitionService;
import com.erp.approval.vo.DefinitionDetailVO;
import com.erp.approval.vo.DefinitionListVO;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审批定义Controller.
 * 审批定义CRUD与预置流程加载接口.
 *
 * @author AI
 */
@Slf4j
@RestController
@RequestMapping("/api/approval/definition")
@RequiredArgsConstructor
@Tag(name = "审批定义管理", description = "审批定义CRUD与预置流程加载")
public class ApprovalDefinitionController {

    private final IApprovalDefinitionService definitionService;

    @Operation(summary = "分页查询审批定义列表")
    @GetMapping("/page")
    public RT<PageResult<DefinitionListVO>> page(DefinitionQueryDTO query) {
        return RT.ok(definitionService.pageList(query));
    }

    @Operation(summary = "查询审批定义详情")
    @GetMapping("/{id}")
    public RT<DefinitionDetailVO> getById(
            @Parameter(description = "审批定义ID") @PathVariable Long id) {
        return RT.ok(definitionService.getById(id));
    }

    @Operation(summary = "新增审批定义")
    @PostMapping
    public RT<Long> create(
            @Parameter(description = "审批定义创建参数") @Valid @RequestBody DefinitionCreateDTO dto) {
        return RT.ok(definitionService.create(dto));
    }

    @Operation(summary = "修改审批定义")
    @PutMapping("/{id}")
    public RT<Void> update(
            @Parameter(description = "审批定义ID") @PathVariable Long id,
            @Parameter(description = "审批定义更新参数") @Valid @RequestBody DefinitionUpdateDTO dto) {
        definitionService.update(id, dto);
        return RT.ok();
    }

    @Operation(summary = "删除审批定义")
    @DeleteMapping("/{id}")
    public RT<Void> delete(
            @Parameter(description = "审批定义ID") @PathVariable Long id) {
        definitionService.delete(id);
        return RT.ok();
    }
}
