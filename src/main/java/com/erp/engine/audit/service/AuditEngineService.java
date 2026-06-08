package com.erp.engine.audit.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.dto.AuditApproveDTO;
import com.erp.engine.audit.dto.AuditSubmitDTO;
import com.erp.engine.audit.entity.DocumentStatusEntity;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import com.erp.engine.audit.entity.SysAuditLogEntity;
import com.erp.engine.audit.event.AuditApprovedEvent;
import com.erp.engine.audit.mapper.AuditLogMapper;
import com.erp.engine.audit.mapper.DocumentStatusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 审核引擎核心Service.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditEngineService {

    private final AuditConfigService auditConfigService;
    private final AuditLogMapper auditLogMapper;
    private final DocumentStatusMapper documentStatusMapper;
    private final ApprovalIntegrationService approvalIntegrationService;
    private final ApplicationEventPublisher eventPublisher;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String LOCK_PREFIX = "audit:lock:";
    private static final Duration LOCK_TTL = Duration.ofSeconds(10);

    /**
     * 提交审核.
     */
    @Transactional(rollbackFor = Exception.class)
    public void submit(AuditSubmitDTO dto) {
        String lockKey = LOCK_PREFIX + dto.getDocType() + ":" + dto.getDocId();
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", LOCK_TTL);
        if (Boolean.FALSE.equals(locked)) {
            throw new BusinessException(ErrorCode.OPERATION_TOO_FREQUENT, "单据正在处理中，请勿重复提交");
        }
        try {
            DocumentStatusEntity docStatus = documentStatusMapper
                    .selectForUpdate(dto.getDocType(), dto.getDocId());
            if (docStatus == null) {
                throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "单据不存在");
            }
            if (docStatus.getStatus() != 0) {
                throw new BusinessException(ErrorCode.DATA_STATUS_INVALID,
                        "仅草稿状态单据可提交审核，当前状态：" + docStatus.getStatus());
            }

            SysAuditConfigEntity config = auditConfigService.getConfig(dto.getDocType());

            int fromStatus = docStatus.getStatus();
            documentStatusMapper.updateStatus(dto.getDocType(), dto.getDocId(), 1);

            insertAuditLog(dto.getDocType(), dto.getDocId(), "SUBMIT",
                    fromStatus, 1, dto.getSubmitRemark());

            if (Boolean.TRUE.equals(config.getApprovalEnabled())) {
                approvalIntegrationService.createApprovalInstance(
                        dto.getDocType(), dto.getDocId());
            } else if (Boolean.TRUE.equals(config.getAutoConfirm())) {
                documentStatusMapper.updateStatus(dto.getDocType(), dto.getDocId(), 2);
                insertAuditLog(dto.getDocType(), dto.getDocId(), "APPROVE",
                        1, 2, "系统自动审核通过");
                eventPublisher.publishEvent(new AuditApprovedEvent(
                        this, dto.getDocType(), dto.getDocId()));
            }
        } finally {
            stringRedisTemplate.delete(lockKey);
        }
    }

    /**
     * 审核通过.
     */
    @Transactional(rollbackFor = Exception.class)
    public void approve(AuditApproveDTO dto) {
        String lockKey = LOCK_PREFIX + dto.getDocType() + ":" + dto.getDocId();
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", LOCK_TTL);
        if (Boolean.FALSE.equals(locked)) {
            throw new BusinessException(ErrorCode.OPERATION_TOO_FREQUENT, "单据正在处理中，请稍后重试");
        }
        try {
            DocumentStatusEntity docStatus = documentStatusMapper
                    .selectForUpdate(dto.getDocType(), dto.getDocId());
            if (docStatus == null) {
                throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "单据不存在");
            }
            if (docStatus.getStatus() != 1) {
                throw new BusinessException(ErrorCode.DATA_STATUS_INVALID,
                        "仅已提交状态单据可审核通过，当前状态：" + docStatus.getStatus());
            }

            documentStatusMapper.updateStatus(dto.getDocType(), dto.getDocId(), 2);

            insertAuditLog(dto.getDocType(), dto.getDocId(), "APPROVE",
                    1, 2, dto.getOpinion());

            eventPublisher.publishEvent(new AuditApprovedEvent(
                    this, dto.getDocType(), dto.getDocId()));
        } finally {
            stringRedisTemplate.delete(lockKey);
        }
    }

    private void insertAuditLog(String docType, Long docId, String operationType,
                                 int fromStatus, int toStatus, String opinion) {
        SysAuditLogEntity log = new SysAuditLogEntity();
        log.setDocType(docType);
        log.setDocId(docId);
        log.setOperationType(operationType);
        log.setOperatorId(StpUtil.getLoginIdAsLong());
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setOpinion(opinion);
        log.setCreatedAt(LocalDateTime.now());
        auditLogMapper.insert(log);
    }
}
