package com.erp.approval.controller;

import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.service.ApprovalWorkflowRuntimeService;
import com.erp.common.result.RT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审批流程运行时Controller.
 * 运行时引擎状态机操作接口.
 *
 * @author AI
 */
@Slf4j
@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
@Tag(name = "审批运行时引擎", description = "审批状态机编排与工作流推进接口")
public class ApprovalWorkflowController {

    private final ApprovalWorkflowRuntimeService runtimeService;

    @Operation(summary = "查询工作流状态")
    @GetMapping("/runtime/{instanceId}")
    public RT<ApprovalInstanceEntity> getState(
            @Parameter(description = "审批实例ID") @PathVariable Long instanceId) {
        return RT.ok(runtimeService.getWorkflowState(instanceId));
    }

    @Operation(summary = "推进工作流（通过/驳回当前节点）")
    @PostMapping("/runtime/{instanceId}/advance")
    public RT<Void> advance(
            @Parameter(description = "审批实例ID") @PathVariable Long instanceId,
            @Parameter(description = "推进操作参数") @Valid @RequestBody AdvanceRequest request) {
        runtimeService.advanceWorkflow(instanceId, request.getAction(), request.getComment());
        return RT.ok();
    }

    @Operation(summary = "完成工作流（所有节点通过）")
    @PostMapping("/runtime/{instanceId}/complete")
    public RT<Void> complete(
            @Parameter(description = "审批实例ID") @PathVariable Long instanceId) {
        runtimeService.completeWorkflow(instanceId);
        return RT.ok();
    }

    @Operation(summary = "驳回工作流")
    @PostMapping("/runtime/{instanceId}/reject")
    public RT<Void> reject(
            @Parameter(description = "审批实例ID") @PathVariable Long instanceId,
            @Parameter(description = "驳回原因") @Valid @RequestBody RejectRequest request) {
        runtimeService.rejectWorkflow(instanceId, request.getComment());
        return RT.ok();
    }

    @Operation(summary = "撤回工作流")
    @PostMapping("/runtime/{instanceId}/withdraw")
    public RT<Void> withdraw(
            @Parameter(description = "审批实例ID") @PathVariable Long instanceId) {
        runtimeService.withdrawWorkflow(instanceId);
        return RT.ok();
    }

    @Data
    public static class AdvanceRequest {
        @NotBlank(message = "操作动作不能为空")
        private String action;
        private String comment;
    }

    @Data
    public static class RejectRequest {
        @NotBlank(message = "驳回原因不能为空")
        private String comment;
    }
}
