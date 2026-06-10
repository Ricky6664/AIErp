package com.erp.approval.controller;

import com.erp.approval.dto.InstanceCreateDTO;
import com.erp.approval.dto.InstanceQueryDTO;
import com.erp.approval.service.IApprovalInstanceService;
import com.erp.approval.vo.InstanceVO;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * 审批实例Controller.
 *
 * @author AI
 */
@Slf4j
@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
@Tag(name = "审批实例管理", description = "审批实例提交/查询/撤回接口")
public class ApprovalInstanceController {

    private final IApprovalInstanceService instanceService;

    @Operation(summary = "分页查询审批实例列表")
    @GetMapping("/instance")
    public RT<PageResult<InstanceVO>> pageList(@Valid InstanceQueryDTO query) {
        return RT.ok(instanceService.pageList(query));
    }

    @Operation(summary = "根据ID查询审批实例详情")
    @GetMapping("/instance/{id}")
    public RT<InstanceVO> getById(
            @Parameter(description = "审批实例ID") @PathVariable Long id) {
        return RT.ok(instanceService.getById(id));
    }

    @Operation(summary = "提交审批（创建审批实例）")
    @PostMapping("/instance")
    public RT<Long> submit(
            @Parameter(description = "审批实例创建参数") @Valid @RequestBody InstanceCreateDTO dto) {
        return RT.ok(instanceService.submit(dto));
    }

    @Operation(summary = "撤回审批")
    @PostMapping("/instance/{id}/withdraw")
    public RT<Void> withdraw(
            @Parameter(description = "审批实例ID") @PathVariable Long id) {
        instanceService.withdraw(id);
        return RT.ok();
    }
}
