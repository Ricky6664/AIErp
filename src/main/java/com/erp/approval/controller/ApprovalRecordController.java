package com.erp.approval.controller;

import com.erp.approval.dto.RecordActionDTO;
import com.erp.approval.dto.RecordCountersignDTO;
import com.erp.approval.dto.RecordLogQueryDTO;
import com.erp.approval.dto.RecordTransferDTO;
import com.erp.approval.dto.RecordUrgeDTO;
import com.erp.approval.service.ApprovalRecordCoreService;
import com.erp.approval.service.IApprovalRecordService;
import com.erp.approval.vo.RecordLogVO;
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
    private final ApprovalRecordCoreService recordCoreService;

    @Operation(summary = "查询审批实例的审批记录")
    @GetMapping("/record/instance/{instanceId}")
    public RT<PageResult<RecordVO>> pageListByInstance(
            @Parameter(description = "审批实例ID") @PathVariable Long instanceId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize) {
        return RT.ok(recordService.pageListByInstance(instanceId, pageNum, pageSize));
    }

    @Operation(summary = "分页查询审批日志列表")
    @GetMapping("/record")
    public RT<PageResult<RecordLogVO>> pageLogList(@Valid RecordLogQueryDTO query) {
        return RT.ok(recordService.pageLogList(query));
    }

    @Operation(summary = "审批操作（通过/驳回）")
    @PostMapping("/record")
    public RT<Long> recordAction(
            @Parameter(description = "审批操作参数") @Valid @RequestBody RecordActionDTO dto) {
        return RT.ok(recordService.recordAction(dto));
    }

    @Operation(summary = "审批转办")
    @PostMapping("/record/transfer")
    public RT<Long> transfer(
            @Parameter(description = "转办参数") @Valid @RequestBody RecordTransferDTO dto) {
        return RT.ok(recordCoreService.transferAction(dto));
    }

    @Operation(summary = "审批加签")
    @PostMapping("/record/countersign")
    public RT<Long> countersign(
            @Parameter(description = "加签参数") @Valid @RequestBody RecordCountersignDTO dto) {
        return RT.ok(recordCoreService.countersignAction(dto));
    }

    @Operation(summary = "审批催办")
    @PostMapping("/record/urge")
    public RT<Long> urge(
            @Parameter(description = "催办参数") @Valid @RequestBody RecordUrgeDTO dto) {
        return RT.ok(recordCoreService.urgeAction(dto));
    }
}
