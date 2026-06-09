package com.erp.flow.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.flow.dto.BizDocRelationDTO;
import com.erp.flow.entity.DocRelationEntity;
import com.erp.flow.mapper.DocRelationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 单据流转操作日志服务.
 * 记录下推/引入/复制/回滚等流转操作日志，提供操作历史查询与审计追溯能力.
 *
 * @author AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BizDocFlowLogService {

    private final DocRelationMapper docRelationMapper;

    public Map<String, Object> buildLogEntry(String operationType, BizDocRelationDTO dto, Long relationId) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("timestamp", LocalDateTime.now().toString());
        entry.put("operationType", operationType);
        entry.put("relationId", relationId);
        entry.put("sourceDocType", dto.getSourceDocType());
        entry.put("sourceDocId", dto.getSourceDocId());
        entry.put("sourceDetailId", dto.getSourceDetailId());
        entry.put("targetDocType", dto.getTargetDocType());
        entry.put("targetDocId", dto.getTargetDocId());
        entry.put("targetDetailId", dto.getTargetDetailId());
        entry.put("relationQty", dto.getRelationQty());
        entry.put("operatorId", StpUtil.getLoginIdAsLong());
        return entry;
    }

    public void recordPushOperation(BizDocRelationDTO dto, Long relationId) {
        Map<String, Object> logEntry = buildLogEntry("push", dto, relationId);
        log.info("流转操作日志-下推: {}", logEntry);
    }

    public void recordRollbackOperation(DocRelationEntity relation) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("timestamp", LocalDateTime.now().toString());
        entry.put("operationType", "rollback");
        entry.put("relationId", relation.getId());
        entry.put("sourceDocType", relation.getSourceDocType());
        entry.put("sourceDocId", relation.getSourceDocId());
        entry.put("targetDocType", relation.getTargetDocType());
        entry.put("targetDocId", relation.getTargetDocId());
        entry.put("operatorId", StpUtil.getLoginIdAsLong());
        log.info("流转操作日志-回滚: {}", entry);
    }

    public List<Map<String, Object>> queryPushLogs(String docType, Long docId) {
        LambdaQueryWrapper<DocRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(DocRelationEntity::getSourceDocType, docType)
                           .eq(DocRelationEntity::getSourceDocId, docId)
                           .or()
                           .eq(DocRelationEntity::getTargetDocType, docType)
                           .eq(DocRelationEntity::getTargetDocId, docId))
               .eq(DocRelationEntity::getRelationType, "push")
               .orderByDesc(DocRelationEntity::getId);
        List<DocRelationEntity> list = docRelationMapper.selectList(wrapper);
        return list.stream().map(entity -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("relationId", entity.getId());
            entry.put("sourceDocType", entity.getSourceDocType());
            entry.put("sourceDocId", entity.getSourceDocId());
            entry.put("sourceDetailId", entity.getSourceDetailId());
            entry.put("targetDocType", entity.getTargetDocType());
            entry.put("targetDocId", entity.getTargetDocId());
            entry.put("targetDetailId", entity.getTargetDetailId());
            entry.put("relationType", entity.getRelationType());
            entry.put("relationQty", entity.getRelationQty());
            entry.put("createTime", entity.getCreateTime());
            return entry;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> queryFlowHistory(String docType, Long docId) {
        LambdaQueryWrapper<DocRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(DocRelationEntity::getSourceDocType, docType)
                           .eq(DocRelationEntity::getSourceDocId, docId)
                           .or()
                           .eq(DocRelationEntity::getTargetDocType, docType)
                           .eq(DocRelationEntity::getTargetDocId, docId))
               .orderByDesc(DocRelationEntity::getId);
        List<DocRelationEntity> list = docRelationMapper.selectList(wrapper);
        return list.stream().map(entity -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("relationId", entity.getId());
            entry.put("sourceDocType", entity.getSourceDocType());
            entry.put("sourceDocId", entity.getSourceDocId());
            entry.put("sourceDetailId", entity.getSourceDetailId());
            entry.put("targetDocType", entity.getTargetDocType());
            entry.put("targetDocId", entity.getTargetDocId());
            entry.put("targetDetailId", entity.getTargetDetailId());
            entry.put("relationType", entity.getRelationType());
            entry.put("relationQty", entity.getRelationQty());
            entry.put("createTime", entity.getCreateTime());
            return entry;
        }).collect(Collectors.toList());
    }

    public long countPushOperations(String docType, Long docId) {
        LambdaQueryWrapper<DocRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(DocRelationEntity::getSourceDocType, docType)
                           .eq(DocRelationEntity::getSourceDocId, docId)
                           .or()
                           .eq(DocRelationEntity::getTargetDocType, docType)
                           .eq(DocRelationEntity::getTargetDocId, docId))
               .eq(DocRelationEntity::getRelationType, "push");
        return docRelationMapper.selectCount(wrapper);
    }
}
