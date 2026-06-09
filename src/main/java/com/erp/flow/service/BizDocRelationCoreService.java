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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 单据关联关系核心业务Service.
 * 负责单据引入/下推/复制关联关系的维护，
 * 遵循事务一致性与并发控制要求.
 *
 * @author AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BizDocRelationCoreService {

    private static final Set<String> VALID_RELATION_TYPES =
            new HashSet<>(Arrays.asList("import", "push", "copy"));

    private final DocRelationMapper docRelationMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long createRelation(BizDocRelationDTO dto) {
        validateBusinessRules(dto);

        DocRelationEntity entity = buildEntity(dto);
        docRelationMapper.insert(entity);

        log.info("单据关联关系创建成功: id={}, sourceDocType={}, sourceDocId={}, targetDocType={}, targetDocId={}, relationType={}, operator={}",
                entity.getId(), dto.getSourceDocType(), dto.getSourceDocId(),
                dto.getTargetDocType(), dto.getTargetDocId(),
                dto.getRelationType(), StpUtil.getLoginIdAsLong());
        return entity.getId();
    }

    public List<DocRelationEntity> queryBySource(String docType, Long docId) {
        LambdaQueryWrapper<DocRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocRelationEntity::getSourceDocType, docType)
               .eq(DocRelationEntity::getSourceDocId, docId)
               .orderByDesc(DocRelationEntity::getId);
        return docRelationMapper.selectList(wrapper);
    }

    public List<DocRelationEntity> queryByTarget(String docType, Long docId) {
        LambdaQueryWrapper<DocRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocRelationEntity::getTargetDocType, docType)
               .eq(DocRelationEntity::getTargetDocId, docId)
               .orderByDesc(DocRelationEntity::getId);
        return docRelationMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long updateRelation(Long relationId, BizDocRelationDTO dto) {
        DocRelationEntity entity = docRelationMapper.selectById(relationId);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        validateBusinessRules(dto);

        entity.setSourceDocType(dto.getSourceDocType());
        entity.setSourceDocId(dto.getSourceDocId());
        entity.setSourceDetailId(dto.getSourceDetailId());
        entity.setTargetDocType(dto.getTargetDocType());
        entity.setTargetDocId(dto.getTargetDocId());
        entity.setTargetDetailId(dto.getTargetDetailId());
        entity.setRelationType(dto.getRelationType());
        entity.setRelationQty(dto.getRelationQty());

        docRelationMapper.updateById(entity);

        log.info("单据关联关系更新: id={}, sourceDocType={}, sourceDocId={}, targetDocType={}, targetDocId={}, relationType={}, 操作人={}",
                relationId, entity.getSourceDocType(), entity.getSourceDocId(),
                entity.getTargetDocType(), entity.getTargetDocId(),
                entity.getRelationType(), StpUtil.getLoginIdAsLong());
        return relationId;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRelation(Long relationId) {
        DocRelationEntity entity = docRelationMapper.selectById(relationId);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        docRelationMapper.deleteById(relationId);
        log.info("单据关联关系删除: id={}, sourceDocType={}, sourceDocId={}, operator={}",
                relationId, entity.getSourceDocType(), entity.getSourceDocId(),
                StpUtil.getLoginIdAsLong());
    }

    private void validateBusinessRules(BizDocRelationDTO dto) {
        if (!VALID_RELATION_TYPES.contains(dto.getRelationType())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "关联类型无效: " + dto.getRelationType() + "，有效值: import/push/copy");
        }

        if (!"copy".equals(dto.getRelationType()) && dto.getRelationQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "引入/下推操作的关联数量必须大于0");
        }
    }

    private DocRelationEntity buildEntity(BizDocRelationDTO dto) {
        DocRelationEntity entity = new DocRelationEntity();
        entity.setSourceDocType(dto.getSourceDocType());
        entity.setSourceDocId(dto.getSourceDocId());
        entity.setSourceDetailId(dto.getSourceDetailId());
        entity.setTargetDocType(dto.getTargetDocType());
        entity.setTargetDocId(dto.getTargetDocId());
        entity.setTargetDetailId(dto.getTargetDetailId());
        entity.setRelationType(dto.getRelationType());
        entity.setRelationQty(dto.getRelationQty());
        return entity;
    }
}
