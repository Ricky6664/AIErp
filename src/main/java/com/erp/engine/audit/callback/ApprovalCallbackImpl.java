package com.erp.engine.audit.callback;

import com.erp.engine.audit.entity.DocumentStatusEntity;
import com.erp.engine.audit.entity.SysAuditLogEntity;
import com.erp.engine.audit.event.AuditApprovedEvent;
import com.erp.engine.audit.mapper.AuditLogMapper;
import com.erp.engine.audit.mapper.DocumentStatusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * P1-002审批回调实现.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalCallbackImpl {

    private static final Long SYSTEM_USER_ID = 0L;

    private final DocumentStatusMapper documentStatusMapper;
    private final AuditLogMapper auditLogMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 审批通过回调.
     */
    @Transactional(rollbackFor = Exception.class)
    public void approved(String bizType, Long bizId, String approvalOpinion) {
        DocumentStatusEntity docStatus = documentStatusMapper
                .selectForUpdate(bizType, bizId);
        if (docStatus == null || docStatus.getStatus() != 1) {
            log.warn("审批通过回调忽略: 单据不存在或非已提交状态 bizType={}, bizId={}, status={}",
                    bizType, bizId, docStatus != null ? docStatus.getStatus() : null);
            return;
        }

        documentStatusMapper.updateStatus(bizType, bizId, 2);

        SysAuditLogEntity logEntity = new SysAuditLogEntity();
        logEntity.setDocType(bizType);
        logEntity.setDocId(bizId);
        logEntity.setOperationType("APPROVE");
        logEntity.setOperatorId(SYSTEM_USER_ID);
        logEntity.setFromStatus(1);
        logEntity.setToStatus(2);
        logEntity.setOpinion("审批流程通过: " + approvalOpinion);
        logEntity.setCreatedAt(LocalDateTime.now());
        auditLogMapper.insert(logEntity);

        eventPublisher.publishEvent(new AuditApprovedEvent(this, bizType, bizId));
    }

    /**
     * 审批驳回回调.
     */
    @Transactional(rollbackFor = Exception.class)
    public void rejected(String bizType, Long bizId, String rejectReason) {
        DocumentStatusEntity docStatus = documentStatusMapper
                .selectForUpdate(bizType, bizId);
        if (docStatus == null || docStatus.getStatus() != 1) {
            log.warn("审批驳回回调忽略: 单据不存在或非已提交状态 bizType={}, bizId={}, status={}",
                    bizType, bizId, docStatus != null ? docStatus.getStatus() : null);
            return;
        }

        documentStatusMapper.updateStatus(bizType, bizId, 3);

        SysAuditLogEntity logEntity = new SysAuditLogEntity();
        logEntity.setDocType(bizType);
        logEntity.setDocId(bizId);
        logEntity.setOperationType("REJECT");
        logEntity.setOperatorId(SYSTEM_USER_ID);
        logEntity.setFromStatus(1);
        logEntity.setToStatus(3);
        logEntity.setOpinion("审批流程驳回: " + rejectReason);
        logEntity.setCreatedAt(LocalDateTime.now());
        auditLogMapper.insert(logEntity);
    }
}
