package com.erp.module.org.controller;

import com.erp.common.result.RT;
import com.erp.module.org.service.OrgWorkbenchService;
import com.erp.module.org.vo.WorkbenchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 组织架构工作台Controller.
 *
 * @author AI
 * @since 2026-06-08
 */
@Slf4j
@RestController
@RequestMapping("/api/org")
@RequiredArgsConstructor
@Tag(name = "组织架构工作台", description = "组织架构工作台聚合数据查询接口")
public class OrgWorkbenchController {

    private final OrgWorkbenchService workbenchService;

    @Operation(summary = "获取组织架构工作台聚合数据")
    @GetMapping("/workbench")
    public RT<WorkbenchVO> workbench() {
        return RT.ok(workbenchService.getWorkbenchData());
    }
}
