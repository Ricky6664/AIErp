package com.erp.flow.controller;

import com.erp.common.result.RT;
import com.erp.flow.dto.BizDocRelationDTO;
import com.erp.flow.entity.DocRelationEntity;
import com.erp.flow.service.BizDocRelationCoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 单据引入/关联关系Controller.
 * 通用单据引入服务接口.
 *
 * @author AI
 */
@Slf4j
@RestController
@RequestMapping("/api/doc-flow")
@RequiredArgsConstructor
@Tag(name = "单据引入与关联关系", description = "通用单据引入/关联关系查询与维护接口")
public class DocImportController {

    private final BizDocRelationCoreService bizDocRelationCoreService;

    @Operation(summary = "创建单据关联关系（引入/下推/复制）")
    @PostMapping("/import")
    public RT<Long> importDoc(@Valid @RequestBody BizDocRelationDTO dto) {
        Long relationId = bizDocRelationCoreService.createRelation(dto);
        return RT.ok(relationId);
    }

    @Operation(summary = "查询可引入源单列表（正向追溯：源单→下游关联单据）")
    @GetMapping("/import/source/{docType}/{docId}")
    public RT<List<DocRelationEntity>> queryImportSources(
            @Parameter(description = "源单据类型") @PathVariable String docType,
            @Parameter(description = "源单据ID") @PathVariable Long docId) {
        List<DocRelationEntity> relations = bizDocRelationCoreService.queryBySource(docType, docId);
        return RT.ok(relations);
    }

    @Operation(summary = "查询单据关联关系（按目标单反向追溯）")
    @GetMapping("/relation/{docType}/{docId}")
    public RT<List<DocRelationEntity>> queryRelations(
            @Parameter(description = "单据类型") @PathVariable String docType,
            @Parameter(description = "单据ID") @PathVariable Long docId) {
        List<DocRelationEntity> relations = bizDocRelationCoreService.queryByTarget(docType, docId);
        return RT.ok(relations);
    }

    @Operation(summary = "删除单据关联关系（回滚操作用）")
    @DeleteMapping("/relation/{relationId}")
    public RT<Void> deleteRelation(
            @Parameter(description = "关联关系ID") @PathVariable Long relationId) {
        bizDocRelationCoreService.deleteRelation(relationId);
        return RT.ok();
    }
}
