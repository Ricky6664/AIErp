package com.erp.finance.controller;

import com.erp.common.result.RT;
import com.erp.finance.service.impl.FinanceWorkbenchAggregateServiceImpl;
import com.erp.finance.vo.FinanceWorkbenchAggregateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 财务基础设置工作台Controller.
 *
 * @author AI
 * @since 2026-06-07
 */
@Slf4j
@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@Tag(name = "财务基础设置工作台", description = "财务工作台聚合数据查询接口")
public class FinanceWorkbenchController {

    private final FinanceWorkbenchAggregateServiceImpl workbenchService;

    @Operation(summary = "获取财务工作台聚合数据")
    @GetMapping("/workbench")
    public RT<FinanceWorkbenchAggregateVO> workbench() {
        return RT.ok(workbenchService.getWorkbenchData());
    }
}
