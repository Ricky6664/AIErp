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

import java.util.Collections;
import java.util.List;

/**
 * 通用单据复制服务.
 * 负责单据复制操作：源单校验→复制规则校验→创建复制关联→记录日志.
 * 遵循事务一致性与并发控制要求.
 *
 * @author AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BizDocCopyService {

    private final BizDocRelationCoreService relationCoreService;
    private final DocRelationMapper docRelationMapper;

    /**
     * 执行单据复制操作.
     * 校验复制规则后创建单据关联，记录操作日志.
     *
     * @param dto 复制请求参数
     * @return 关联关系ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long copyDocument(BizDocRelationDTO dto) {
        validateCopyRules(dto);

        Long relationId = relationCoreService.createRelation(dto);

        log.info("单据复制成功: relationId={}, sourceDocType={}, sourceDocId={}, targetDocType={}, targetDocId={}, qty={}, operator={}",
                relationId, dto.getSourceDocType(), dto.getSourceDocId(),
                dto.getTargetDocType(), dto.getTargetDocId(),
                dto.getRelationQty(), StpUtil.getLoginIdAsLong());
        return relationId;
    }

    /**
     * 复制回滚操作.
     * 删除复制关联关系记录.
     *
     * @param relationId 关联关系ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void rollbackCopy(Long relationId) {
        DocRelationEntity relation = docRelationMapper.selectById(relationId);
        if (relation == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "关联关系不存在: " + relationId);
        }
        if (!"copy".equals(relation.getRelationType())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "仅支持复制类型关联的回滚操作");
        }

        relationCoreService.deleteRelation(relationId);

        log.info("复制回滚成功: relationId={}, sourceDocType={}, sourceDocId={}, targetDocType={}, targetDocId={}, operator={}",
                relationId, relation.getSourceDocType(), relation.getSourceDocId(),
                relation.getTargetDocType(), relation.getTargetDocId(),
                StpUtil.getLoginIdAsLong());
    }

    /**
     * 查询复制关联关系列表.
     *
     * @param sourceDocType 源单据类型
     * @param sourceDocId   源单据ID
     * @return 关联关系列表
     */
    public List<DocRelationEntity> queryCopyRelations(String sourceDocType, Long sourceDocId) {
        LambdaQueryWrapper<DocRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocRelationEntity::getSourceDocType, sourceDocType)
               .eq(DocRelationEntity::getSourceDocId, sourceDocId)
               .eq(DocRelationEntity::getRelationType, "copy")
               .orderByDesc(DocRelationEntity::getId);
        return docRelationMapper.selectList(wrapper);
    }

    /**
     * 获取可复制目标类型列表.
     *
     * @param sourceDocType 源单据类型
     * @return 可复制的目标单据类型列表
     */
    public List<String> getCopyableTargetTypes(String sourceDocType) {
        LambdaQueryWrapper<DocRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocRelationEntity::getSourceDocType, sourceDocType)
               .eq(DocRelationEntity::getRelationType, "copy")
               .select(DocRelationEntity::getTargetDocType)
               .groupBy(DocRelationEntity::getTargetDocType);
        List<DocRelationEntity> list = docRelationMapper.selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(DocRelationEntity::getTargetDocType)
                .distinct()
                .toList();
    }

    /**
     * 校验复制业务规则.
     * 复制操作不限制关联数量（与引入/下推不同），但要求关联类型必须为copy.
     */
    private void validateCopyRules(BizDocRelationDTO dto) {
        if (!"copy".equals(dto.getRelationType())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "复制操作要求关联类型为 copy");
        }
    }
}
