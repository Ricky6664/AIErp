package com.erp.approval.controller;

import com.erp.approval.dto.MyApprovalQueryDTO;
import com.erp.approval.service.IApprovalMyService;
import com.erp.approval.vo.MyApprovalVO;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 我的审批Controller.
 *
 * @author AI
 */
@Slf4j
@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
@Tag(name = "我的审批", description = "我的审批查询接口（待审/已审/我的申请）")
public class ApprovalMyController {

    private final IApprovalMyService myService;

    @Operation(summary = "查询我的审批列表")
    @GetMapping("/my")
    public RT<PageResult<MyApprovalVO>> pageList(@Valid MyApprovalQueryDTO query) {
        return RT.ok(myService.pageList(query));
    }
}
