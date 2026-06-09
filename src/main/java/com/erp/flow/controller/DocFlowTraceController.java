package com.erp.flow.controller;

import com.erp.common.result.RT;
import com.erp.flow.service.DocFlowTraceService;
import com.erp.flow.vo.RelationListVO;
import com.erp.flow.vo.TraceNodeVO;
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

/**
 * 单据追溯Controller.
 * 提供单据追溯链路树和关联关系查询接口.
 *
 * @author AI
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "单据追溯", description = "单据追溯链路与关联关系查询接口")
@RequestMapping("/api/engine/flow")
public class DocFlowTraceController {

    private final DocFlowTraceService docFlowTraceService;

    @Operation(summary = "单据追溯链路查询")
    @GetMapping("/trace/{docType}/{docId}")
    public RT<TraceNodeVO> trace(
            @Parameter(description = "单据类型") @PathVariable String docType,
            @Parameter(description = "单据ID") @PathVariable Long docId) {
        TraceNodeVO traceTree = docFlowTraceService.trace(docType, docId);
        return RT.ok(traceTree);
    }

    @Operation(summary = "单据关联关系查询")
    @GetMapping("/relation/{docType}/{docId}")
    public RT<List<RelationListVO>> getRelations(
            @Parameter(description = "单据类型") @PathVariable String docType,
            @Parameter(description = "单据ID") @PathVariable Long docId) {
        List<RelationListVO> relations = docFlowTraceService.getRelations(docType, docId);
        return RT.ok(relations);
    }
}
