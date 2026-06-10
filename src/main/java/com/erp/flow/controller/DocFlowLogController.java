package com.erp.flow.controller;

import com.erp.common.result.RT;
import com.erp.flow.service.BizDocFlowLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 单据流转操作日志Controller.
 * 提供下推/引入/复制/回滚等流转操作日志的查询接口，供前端下推弹窗展示操作历史.
 *
 * @author AI
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "流转操作日志", description = "单据流转操作日志查询与审计追溯接口")
@RequestMapping("/api/doc-flow/log")
public class DocFlowLogController {

    private final BizDocFlowLogService bizDocFlowLogService;

    @Operation(summary = "查询单据下推操作日志列表")
    @GetMapping("/push/{docType}/{docId}")
    public RT<List<Map<String, Object>>> queryPushLogs(
            @Parameter(description = "单据类型") @PathVariable String docType,
            @Parameter(description = "单据ID") @PathVariable Long docId) {
        List<Map<String, Object>> logs = bizDocFlowLogService.queryPushLogs(docType, docId);
        return RT.ok(logs);
    }

    @Operation(summary = "查询单据完整流转历史（下推/引入/复制/回滚）")
    @GetMapping("/history/{docType}/{docId}")
    public RT<List<Map<String, Object>>> queryFlowHistory(
            @Parameter(description = "单据类型") @PathVariable String docType,
            @Parameter(description = "单据ID") @PathVariable Long docId) {
        List<Map<String, Object>> history = bizDocFlowLogService.queryFlowHistory(docType, docId);
        return RT.ok(history);
    }

    @Operation(summary = "统计单据下推操作次数")
    @GetMapping("/push/count/{docType}/{docId}")
    public RT<Long> countPushOperations(
            @Parameter(description = "单据类型") @PathVariable String docType,
            @Parameter(description = "单据ID") @PathVariable Long docId) {
        long count = bizDocFlowLogService.countPushOperations(docType, docId);
        return RT.ok(count);
    }
}
