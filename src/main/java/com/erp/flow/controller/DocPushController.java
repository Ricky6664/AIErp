package com.erp.flow.controller;

import com.erp.common.result.RT;
import com.erp.flow.dto.BizDocRelationDTO;
import com.erp.flow.entity.DocRelationEntity;
import com.erp.flow.service.BizDocPushService;
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

import java.util.List;

/**
 * 通用单据下推Controller.
 * 提供单据下推、回滚、查询可推目标类型等接口.
 *
 * @author AI
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "单据下推与回滚", description = "通用单据下推/回滚/可推目标类型查询接口")
public class DocPushController {

    private final BizDocPushService bizDocPushService;

    @Operation(summary = "通用下推服务（校验+创建关联+回写已推数量）")
    @PostMapping("/api/engine/flow/push")
    public RT<Long> pushDocument(@Valid @RequestBody BizDocRelationDTO dto) {
        Long relationId = bizDocPushService.pushDocument(dto);
        return RT.ok(relationId);
    }

    @Operation(summary = "下推回滚（删除关联关系+反写源单已推数量）")
    @PostMapping("/api/engine/flow/rollback/{relationId}")
    public RT<Void> rollbackPush(
            @Parameter(description = "关联关系ID") @PathVariable Long relationId) {
        bizDocPushService.rollbackPush(relationId);
        return RT.ok();
    }

    @Operation(summary = "查询已下推关联关系列表")
    @GetMapping("/api/doc-flow/push/relations/{docType}/{docId}")
    public RT<List<DocRelationEntity>> queryPushRelations(
            @Parameter(description = "源单据类型") @PathVariable String docType,
            @Parameter(description = "源单据ID") @PathVariable Long docId) {
        List<DocRelationEntity> relations = bizDocPushService.queryPushRelations(docType, docId);
        return RT.ok(relations);
    }

    @Operation(summary = "查询可下推目标单类型")
    @GetMapping("/api/doc-flow/push/target-type/{sourceDocType}")
    public RT<List<String>> getPushableTargetTypes(
            @Parameter(description = "源单据类型") @PathVariable String sourceDocType) {
        List<String> targetTypes = bizDocPushService.getPushableTargetTypes(sourceDocType);
        return RT.ok(targetTypes);
    }
}
