package com.erp.approval.controller;

import com.erp.approval.dto.RecordActionDTO;
import com.erp.approval.service.IApprovalRecordService;
import com.erp.approval.vo.RecordVO;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审批记录Controller.
 *
 * @author AI
 */
@Slf4j
@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
@Tag(name = "审批记录管理", description = "审批操作记录与查询接口")
public class ApprovalRecordController {

    private final IApprovalRecordService recordService;

    @Operation(summary = "查询审批实例的审批记录")
    @GetMapping("/record/instance/{instanceId}")
    public RT<PageResult<RecordVO>> pageListByInstance(
            @Parameter(description = "审批实例ID") @PathVariable Long instanceId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize) {
        return RT.ok(recordService.pageListByInstance(instanceId, pageNum, pageSize));
    }

    @Operation(summary = "审批操作（通过/驳回）")
    @PostMapping("/record")
    public RT<Long> recordAction(
            @Parameter(description = "审批操作参数") @Valid @RequestBody RecordActionDTO dto) {
        return RT.ok(recordService.recordAction(dto));
    }
}
