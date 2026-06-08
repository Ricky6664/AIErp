package com.erp.engine.audit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import com.erp.engine.audit.mapper.AuditConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 审核配置服务.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
public class AuditConfigService {

    private final AuditConfigMapper auditConfigMapper;
    private final List<DownstreamChecker> downstreamCheckers;

    /**
     * 根据单据类型获取审核配置.
     */
    public SysAuditConfigEntity getConfig(String docType) {
        SysAuditConfigEntity config = auditConfigMapper.selectOne(
                new LambdaQueryWrapper<SysAuditConfigEntity>()
                        .eq(SysAuditConfigEntity::getDocType, docType));
        if (config == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "审核配置不存在: " + docType);
        }
        return config;
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
