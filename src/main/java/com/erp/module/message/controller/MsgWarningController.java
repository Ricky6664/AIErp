package com.erp.module.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.erp.common.result.RT;
import com.erp.module.message.service.MsgWarningService;
import com.erp.module.message.vo.WarningDashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 预警接口Controller.
 *
 * @author AI
 */
@RestController
@RequestMapping("/api/message/alert")
@RequiredArgsConstructor
@Tag(name = "业务预警管理", description = "预警看板、预警处理接口")
public class MsgWarningController {

    private final MsgWarningService warningService;

    @Operation(summary = "获取预警看板数据")
    @GetMapping
    @SaCheckPermission("message:alert:query")
    public RT<WarningDashboardVO> getWarningDashboard(
            @Parameter(description = "模块过滤") @RequestParam(required = false) String module) {
        return RT.ok(warningService.getWarningDashboard(module));
    }

    @Operation(summary = "处理预警")
    @PostMapping("/{id}/handle")
    @SaCheckPermission("message:alert:handle")
    public RT<Void> handleWarning(
            @Parameter(description = "预警ID") @PathVariable Long id) {
        warningService.handleWarning(id);
        return RT.ok();
    }
}
