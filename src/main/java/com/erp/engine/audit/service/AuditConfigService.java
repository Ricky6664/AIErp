package com.erp.engine.audit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.constants.AuditParamConstants;
import com.erp.engine.audit.dto.SysAuditConfigUpdateDTO;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import com.erp.engine.audit.mapper.AuditConfigMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 审核配置服务.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditConfigService {

    private final AuditConfigMapper auditConfigMapper;
    private final List<DownstreamChecker> downstreamCheckers;
    private final StringRedisTemplate stringRedisTemplate;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 根据单据类型获取审核配置（带Redis缓存）.
     */
    public SysAuditConfigEntity getConfig(String docType) {
        String cacheKey = AuditParamConstants.CONFIG_CACHE_PREFIX + docType;
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return OBJECT_MAPPER.readValue(cached, SysAuditConfigEntity.class);
            } catch (Exception e) {
                log.warn("审核配置缓存反序列化失败，回源查询: docType={}", docType, e);
            }
        }

        SysAuditConfigEntity config = auditConfigMapper.selectOne(
                new LambdaQueryWrapper<SysAuditConfigEntity>()
                        .eq(SysAuditConfigEntity::getDocType, docType));
        if (config == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "审核配置不存在: " + docType);
        }

        try {
            String json = OBJECT_MAPPER.writeValueAsString(config);
            stringRedisTemplate.opsForValue().set(cacheKey, json,
                    AuditParamConstants.CONFIG_CACHE_TTL_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("审核配置缓存写入失败: docType={}", docType, e);
        }

        return config;
    }

    /**
     * 更新审核配置.
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(SysAuditConfigUpdateDTO dto) {
        if (Boolean.TRUE.equals(dto.getAutoConfirm())
                && Boolean.TRUE.equals(dto.getApprovalEnabled())) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR,
                    "自动确认(auto_confirm)与审批流程(approval_enabled)不可同时启用");
        }

        SysAuditConfigEntity existing = auditConfigMapper.selectOne(
                new LambdaQueryWrapper<SysAuditConfigEntity>()
                        .eq(SysAuditConfigEntity::getDocType, dto.getDocType()));
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "审核配置不存在: " + dto.getDocType());
        }

        if (dto.getAutoConfirm() != null) {
            existing.setAutoConfirm(dto.getAutoConfirm());
        }
        if (dto.getApprovalEnabled() != null) {
            existing.setApprovalEnabled(dto.getApprovalEnabled());
        }
        if (dto.getApprovalFlowConfig() != null) {
            existing.setApprovalFlowConfig(dto.getApprovalFlowConfig());
        }
        auditConfigMapper.updateById(existing);

        String cacheKey = AuditParamConstants.CONFIG_CACHE_PREFIX + dto.getDocType();
        stringRedisTemplate.delete(cacheKey);
        log.info("审核配置更新并清除缓存: docType={}", dto.getDocType());
    }

    /**
     * 获取指定单据类型的下游检查器列表.
     */
    public List<DownstreamChecker> getDownstreamCheckers(String docType) {
        List<DownstreamChecker> result = new ArrayList<>();
        for (DownstreamChecker checker : downstreamCheckers) {
            if (checker.supportedDocType().equals(docType)) {
                result.add(checker);
            }
        }
        return result;
    }
}
