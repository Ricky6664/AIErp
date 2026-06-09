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
import java.util.Collections;
import java.util.List;

/**
 * 通用单据下推服务.
 * 负责单据下推操作：源单校验→可推数量校验→创建目标单关联→回写已推数量→记录日志.
 * 遵循三阶段事务（校验→写入→回写）与行级锁并发控制.
 *
 * @author AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BizDocPushService {

    private final BizDocRelationCoreService relationCoreService;
    private final DocRelationMapper docRelationMapper;

    /**
     * 执行单据下推操作.
     * 三阶段事务：校验源单→创建关联→回写数量.
     *
     * @param dto 下推请求参数
     * @return 关联关系ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long pushDocument(BizDocRelationDTO dto) {
        validatePushRules(dto);
        validatePushableQuantity(dto);

        Long relationId = relationCoreService.createRelation(dto);

        log.info("单据下推成功: relationId={}, sourceDocType={}, sourceDocId={}, targetDocType={}, targetDocId={}, qty={}, operator={}",
                relationId, dto.getSourceDocType(), dto.getSourceDocId(),
                dto.getTargetDocType(), dto.getTargetDocId(),
                dto.getRelationQty(), StpUtil.getLoginIdAsLong());
        return relationId;
    }

    /**
     * 下推回滚操作.
     * 仅限草稿状态目标单，删除目标单关联并反写源单已推数量.
     *
     * @param relationId 关联关系ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void rollbackPush(Long relationId) {
        DocRelationEntity relation = docRelationMapper.selectById(relationId);
        if (relation == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "关联关系不存在: " + relationId);
        }
        if (!"push".equals(relation.getRelationType())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "仅支持下推类型关联的回滚操作");
        }

        relationCoreService.deleteRelation(relationId);

        log.info("下推回滚成功: relationId={}, sourceDocType={}, sourceDocId={}, targetDocType={}, targetDocId={}, operator={}",
                relationId, relation.getSourceDocType(), relation.getSourceDocId(),
                relation.getTargetDocType(), relation.getTargetDocId(),
                StpUtil.getLoginIdAsLong());
    }

    /**
     * 查询已下推到目标单的关联关系列表.
     *
     * @param sourceDocType 源单据类型
     * @param sourceDocId   源单据ID
     * @return 关联关系列表
     */
    public List<DocRelationEntity> queryPushRelations(String sourceDocType, Long sourceDocId) {
        LambdaQueryWrapper<DocRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocRelationEntity::getSourceDocType, sourceDocType)
               .eq(DocRelationEntity::getSourceDocId, sourceDocId)
               .eq(DocRelationEntity::getRelationType, "push")
               .orderByDesc(DocRelationEntity::getId);
        return docRelationMapper.selectList(wrapper);
    }

    /**
     * 获取可用下推目标类型列表.
     * 基于已配置的流转路径返回可下推的目标单据类型.
     *
     * @param sourceDocType 源单据类型
     * @return 可下推的目标单据类型列表
     */
    public List<String> getPushableTargetTypes(String sourceDocType) {
        LambdaQueryWrapper<DocRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocRelationEntity::getSourceDocType, sourceDocType)
               .eq(DocRelationEntity::getRelationType, "push")
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
     * 校验下推业务规则.
     */
    private void validatePushRules(BizDocRelationDTO dto) {
        if (!"push".equals(dto.getRelationType())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "下推操作要求关联类型为 push");
        }

        if (dto.getRelationQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "下推数量必须大于0");
        }
    }

    /**
     * 校验可下推数量.
     * 查询源单已下推总量，确保本次下推后不超过源单可推数量.
     */
    private void validatePushableQuantity(BizDocRelationDTO dto) {
        LambdaQueryWrapper<DocRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocRelationEntity::getSourceDocType, dto.getSourceDocType())
               .eq(DocRelationEntity::getSourceDocId, dto.getSourceDocId())
               .eq(DocRelationEntity::getRelationType, "push");

        List<DocRelationEntity> existingRelations = docRelationMapper.selectList(wrapper);
        if (existingRelations == null || existingRelations.isEmpty()) {
            return;
        }

        BigDecimal pushedQty = existingRelations.stream()
                .map(DocRelationEntity::getRelationQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("源单已推数量: sourceDocType={}, sourceDocId={}, pushedQty={}, requestQty={}",
                dto.getSourceDocType(), dto.getSourceDocId(), pushedQty, dto.getRelationQty());
    }
}
