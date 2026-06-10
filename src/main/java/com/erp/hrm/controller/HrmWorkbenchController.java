package com.erp.hrm.controller;

import com.erp.common.result.RT;
import com.erp.hrm.service.impl.HrmWorkbenchAggregateServiceImpl;
import com.erp.hrm.vo.HrmWorkbenchAggregateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/hrm")
@RequiredArgsConstructor
@Tag(name = "HRM工作台", description = "HRM工作台聚合数据查询接口")
public class HrmWorkbenchController {

    private final HrmWorkbenchAggregateServiceImpl workbenchService;

    @Operation(summary = "获取HRM工作台聚合数据")
    @GetMapping("/workbench")
    public RT<HrmWorkbenchAggregateVO> workbench() {
        return RT.ok(workbenchService.getWorkbenchData());
    }
}
