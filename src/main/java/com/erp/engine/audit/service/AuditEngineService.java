package com.erp.engine.audit.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.dto.AuditApproveDTO;
import com.erp.engine.audit.dto.AuditOperationDTO;
import com.erp.engine.audit.dto.AuditSubmitDTO;
import com.erp.engine.audit.dto.AuditVoidDTO;
import com.erp.engine.audit.entity.DocumentStatusEntity;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import com.erp.engine.audit.entity.SysAuditLogEntity;
import com.erp.engine.audit.event.AuditApprovedEvent;
import com.erp.engine.audit.event.VoidResourceReleaseEvent;
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
import java.util.List;

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

    /**
     * 作废单据.
     * 状态流转：Draft(0)/Submitted(1)/Approved(2) -> Voided(4)
     */
    @Transactional(rollbackFor = Exception.class)
    public void voidDocument(AuditVoidDTO dto) {
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

            int currentStatus = docStatus.getStatus();
            if (currentStatus != 0 && currentStatus != 1 && currentStatus != 2) {
                throw new BusinessException(ErrorCode.DATA_STATUS_INVALID,
                        "仅草稿/已提交/已审核状态可作废，当前状态：" + currentStatus);
            }

            documentStatusMapper.updateStatus(dto.getDocType(), dto.getDocId(), 4);

            insertAuditLog(dto.getDocType(), dto.getDocId(), "VOID",
                    currentStatus, 4, dto.getVoidReason());

            eventPublisher.publishEvent(new VoidResourceReleaseEvent(
                    this, dto.getDocType(), dto.getDocId(), currentStatus));

            log.info("作废成功: docType={}, docId={}, fromStatus={}, operator={}",
                    dto.getDocType(), dto.getDocId(), currentStatus, StpUtil.getLoginIdAsLong());
        } finally {
            stringRedisTemplate.delete(lockKey);
        }
    }

    /**
     * 反审操作.
     * 状态流转：Approved(2) -> Draft(0)
     * 前提条件：不存在下游关联单据
     */
    @Transactional(rollbackFor = Exception.class)
    public void unconfirm(AuditOperationDTO dto) {
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
            if (docStatus.getStatus() != 2) {
                throw new BusinessException(ErrorCode.DATA_STATUS_INVALID,
                        "仅已审核状态单据可反审，当前状态：" + docStatus.getStatus());
            }

            List<DownstreamChecker> checkers = auditConfigService
                    .getDownstreamCheckers(dto.getDocType());
            for (DownstreamChecker checker : checkers) {
                DownstreamCheckResult result = checker.check(dto.getDocId());
                if (result.isHasDownstream()) {
                    throw new BusinessException(ErrorCode.BUSINESS_ERROR, String.format(
                            "该单据已有关联下游单据[%s]，无法反审",
                            result.getDownstreamDocNo()));
                }
            }

            int fromStatus = docStatus.getStatus();
            documentStatusMapper.updateStatus(dto.getDocType(), dto.getDocId(), 0);

            insertAuditLog(dto.getDocType(), dto.getDocId(), "UNAUDIT",
                    fromStatus, 0, "反审操作");

            log.info("反审成功: docType={}, docId={}, operator={}",
                    dto.getDocType(), dto.getDocId(), StpUtil.getLoginIdAsLong());
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
