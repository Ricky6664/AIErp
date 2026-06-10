package com.erp.flow.controller;

import com.erp.common.result.RT;
import com.erp.flow.dto.BizDocRelationDTO;
import com.erp.flow.entity.DocRelationEntity;
import com.erp.flow.service.BizDocCopyService;
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
 * 通用单据复制Controller.
 * 提供单据复制、回滚、查询可复制目标类型等接口.
 *
 * @author AI
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "单据复制与回滚", description = "通用单据复制/回滚/可复制目标类型查询接口")
public class DocCopyController {

    private final BizDocCopyService bizDocCopyService;

    @Operation(summary = "通用复制服务（校验+创建关联+记录日志）")
    @PostMapping("/api/doc-flow/copy")
    public RT<Long> copyDocument(@Valid @RequestBody BizDocRelationDTO dto) {
        Long relationId = bizDocCopyService.copyDocument(dto);
        return RT.ok(relationId);
    }

    @Operation(summary = "复制回滚（删除关联关系）")
    @PostMapping("/api/doc-flow/copy/rollback/{relationId}")
    public RT<Void> rollbackCopy(
            @Parameter(description = "关联关系ID") @PathVariable Long relationId) {
        bizDocCopyService.rollbackCopy(relationId);
        return RT.ok();
    }

    @Operation(summary = "查询已复制关联关系列表")
    @GetMapping("/api/doc-flow/copy/relations/{docType}/{docId}")
    public RT<List<DocRelationEntity>> queryCopyRelations(
            @Parameter(description = "源单据类型") @PathVariable String docType,
            @Parameter(description = "源单据ID") @PathVariable Long docId) {
        List<DocRelationEntity> relations = bizDocCopyService.queryCopyRelations(docType, docId);
        return RT.ok(relations);
    }

    @Operation(summary = "查询可复制目标单类型")
    @GetMapping("/api/doc-flow/copy/target-type/{sourceDocType}")
    public RT<List<String>> getCopyableTargetTypes(
            @Parameter(description = "源单据类型") @PathVariable String sourceDocType) {
        List<String> targetTypes = bizDocCopyService.getCopyableTargetTypes(sourceDocType);
        return RT.ok(targetTypes);
    }
}
