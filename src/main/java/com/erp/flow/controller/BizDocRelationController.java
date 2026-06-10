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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 单据关联关系Controller.
 * 提供单据引入/下推/复制关联关系的CRUD接口.
 *
 * @author AI
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "单据关联关系", description = "单据引入/下推/复制关联关系维护接口")
@RequestMapping("/api/doc-flow/relation")
public class BizDocRelationController {

    private final BizDocRelationCoreService bizDocRelationCoreService;

    @Operation(summary = "创建单据关联关系")
    @PostMapping
    public RT<Long> createRelation(@Valid @RequestBody BizDocRelationDTO dto) {
        Long relationId = bizDocRelationCoreService.createRelation(dto);
        return RT.ok(relationId);
    }

    @Operation(summary = "按源单据查询关联关系")
    @GetMapping("/source/{docType}/{docId}")
    public RT<List<DocRelationEntity>> queryBySource(
            @Parameter(description = "源单据类型") @PathVariable String docType,
            @Parameter(description = "源单据ID") @PathVariable Long docId) {
        List<DocRelationEntity> relations = bizDocRelationCoreService.queryBySource(docType, docId);
        return RT.ok(relations);
    }

    @Operation(summary = "按目标单据查询关联关系")
    @GetMapping("/target/{docType}/{docId}")
    public RT<List<DocRelationEntity>> queryByTarget(
            @Parameter(description = "目标单据类型") @PathVariable String docType,
            @Parameter(description = "目标单据ID") @PathVariable Long docId) {
        List<DocRelationEntity> relations = bizDocRelationCoreService.queryByTarget(docType, docId);
        return RT.ok(relations);
    }

    @Operation(summary = "更新单据关联关系")
    @PutMapping("/{relationId}")
    public RT<Long> updateRelation(
            @Parameter(description = "关联关系ID") @PathVariable Long relationId,
            @Valid @RequestBody BizDocRelationDTO dto) {
        Long id = bizDocRelationCoreService.updateRelation(relationId, dto);
        return RT.ok(id);
    }

    @Operation(summary = "删除单据关联关系")
    @DeleteMapping("/{relationId}")
    public RT<Void> deleteRelation(
            @Parameter(description = "关联关系ID") @PathVariable Long relationId) {
        bizDocRelationCoreService.deleteRelation(relationId);
        return RT.ok();
    }
}
