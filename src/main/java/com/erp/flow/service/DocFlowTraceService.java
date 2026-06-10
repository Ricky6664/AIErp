package com.erp.flow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.flow.entity.DocRelationEntity;
import com.erp.flow.mapper.DocRelationMapper;
import com.erp.flow.vo.RelationListVO;
import com.erp.flow.vo.TraceNodeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 单据追溯服务.
 * 提供单据追溯链路的递归查询与树形结构组装.
 *
 * @author AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocFlowTraceService {

    private final DocRelationMapper docRelationMapper;

    public TraceNodeVO trace(String docType, Long docId) {
        Set<String> visited = new HashSet<>();
        TraceNodeVO root = findUltimateRoot(docType, docId, visited);
        visited.clear();
        buildDownstreamTree(root, visited);
        return root;
    }

    public List<RelationListVO> getRelations(String docType, Long docId) {
        List<DocRelationEntity> allRelations = new ArrayList<>();

        List<DocRelationEntity> asSource = docRelationMapper.selectList(
                new LambdaQueryWrapper<DocRelationEntity>()
                        .eq(DocRelationEntity::getSourceDocType, docType)
                        .eq(DocRelationEntity::getSourceDocId, docId)
        );
        if (asSource != null) {
            allRelations.addAll(asSource);
        }

        List<DocRelationEntity> asTarget = docRelationMapper.selectList(
                new LambdaQueryWrapper<DocRelationEntity>()
                        .eq(DocRelationEntity::getTargetDocType, docType)
                        .eq(DocRelationEntity::getTargetDocId, docId)
        );
        if (asTarget != null) {
            allRelations.addAll(asTarget);
        }

        return allRelations.stream().map(this::toRelationListVO).collect(Collectors.toList());
    }

    private TraceNodeVO findUltimateRoot(String docType, Long docId, Set<String> visited) {
        String key = docType + ":" + docId;
        if (!visited.add(key)) {
            TraceNodeVO cycle = new TraceNodeVO();
            cycle.setDocType(docType);
            cycle.setDocId(docId);
            return cycle;
        }

        List<DocRelationEntity> upstream = docRelationMapper.selectList(
                new LambdaQueryWrapper<DocRelationEntity>()
                        .eq(DocRelationEntity::getTargetDocType, docType)
                        .eq(DocRelationEntity::getTargetDocId, docId)
        );

        if (upstream != null && !upstream.isEmpty()) {
            DocRelationEntity firstUpstream = upstream.get(0);
            return findUltimateRoot(firstUpstream.getSourceDocType(), firstUpstream.getSourceDocId(), visited);
        }

        TraceNodeVO root = new TraceNodeVO();
        root.setDocType(docType);
        root.setDocId(docId);
        return root;
    }

    private void buildDownstreamTree(TraceNodeVO node, Set<String> visited) {
        String key = node.getDocType() + ":" + node.getDocId();
        if (!visited.add(key)) {
            return;
        }

        List<DocRelationEntity> downstream = docRelationMapper.selectList(
                new LambdaQueryWrapper<DocRelationEntity>()
                        .eq(DocRelationEntity::getSourceDocType, node.getDocType())
                        .eq(DocRelationEntity::getSourceDocId, node.getDocId())
        );

        if (downstream == null || downstream.isEmpty()) {
            return;
        }

        List<TraceNodeVO> children = new ArrayList<>();
        for (DocRelationEntity rel : downstream) {
            TraceNodeVO child = new TraceNodeVO();
            child.setDocType(rel.getTargetDocType());
            child.setDocId(rel.getTargetDocId());
            child.setRelationType(rel.getRelationType());
            buildDownstreamTree(child, visited);
            children.add(child);
        }
        node.setChildren(children);
    }

    private RelationListVO toRelationListVO(DocRelationEntity entity) {
        RelationListVO vo = new RelationListVO();
        vo.setId(entity.getId());
        vo.setSourceDocType(entity.getSourceDocType());
        vo.setSourceDocId(entity.getSourceDocId());
        vo.setTargetDocType(entity.getTargetDocType());
        vo.setTargetDocId(entity.getTargetDocId());
        vo.setRelationType(entity.getRelationType());
        vo.setRelationQty(entity.getRelationQty());
        return vo;
    }
}
