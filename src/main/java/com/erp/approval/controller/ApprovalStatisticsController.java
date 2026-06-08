package com.erp.approval.controller;

import com.erp.approval.service.IApprovalStatisticsService;
import com.erp.approval.vo.ApprovalStatisticsVO;
import com.erp.common.result.RT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审批统计Controller.
 *
 * @author AI
 */
@Slf4j
@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
@Tag(name = "审批统计", description = "审批数据统计接口")
public class ApprovalStatisticsController {

    private final IApprovalStatisticsService statisticsService;

    @Operation(summary = "获取审批统计数据")
    @GetMapping("/statistics")
    public RT<ApprovalStatisticsVO> getStatistics() {
        return RT.ok(statisticsService.getStatistics());
    }
}
