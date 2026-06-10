package com.erp.engine.audit;

import com.erp.engine.audit.callback.ApprovalCallbackImpl;
import com.erp.engine.audit.entity.DocumentStatusEntity;
import com.erp.engine.audit.entity.SysAuditLogEntity;
import com.erp.engine.audit.event.AuditApprovedEvent;
import com.erp.engine.audit.mapper.AuditLogMapper;
import com.erp.engine.audit.mapper.DocumentStatusMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 审批回调测试（专注P1-002回调验证）.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("审批回调测试")
class ApprovalCallbackTest {

    @Mock
    private DocumentStatusMapper documentStatusMapper;

    @Mock
    private AuditLogMapper auditLogMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ApprovalCallbackImpl callback;

    private static final String BIZ_TYPE = "sale_order";

    @BeforeEach
    void setUp() {
        // InjectMocks handles setup
    }

    // === 审批通过回调 ===

    @Test
    @DisplayName("审批通过回调：status 1→2，日志含'审批流程通过'，发布事件")
    void approved_shouldUpdateStatusAndLog() {
        DocumentStatusEntity docStatus = new DocumentStatusEntity();
        docStatus.setStatus(1);
        when(documentStatusMapper.selectForUpdate(BIZ_TYPE, 5001L)).thenReturn(docStatus);
        when(documentStatusMapper.updateStatus(BIZ_TYPE, 5001L, 2)).thenReturn(1);

        callback.approved(BIZ_TYPE, 5001L, "同意");

        verify(documentStatusMapper).updateStatus(BIZ_TYPE, 5001L, 2);

        ArgumentCaptor<SysAuditLogEntity> logCaptor = ArgumentCaptor.forClass(SysAuditLogEntity.class);
        verify(auditLogMapper).insert(logCaptor.capture());
        SysAuditLogEntity logEntity = logCaptor.getValue();
        assertEquals(BIZ_TYPE, logEntity.getDocType());
        assertEquals(5001L, logEntity.getDocId());
        assertEquals("APPROVE", logEntity.getOperationType());
        assertEquals(1, logEntity.getFromStatus());
        assertEquals(2, logEntity.getToStatus());
        assertEquals("审批流程通过: 同意", logEntity.getOpinion());

        verify(eventPublisher).publishEvent(any(AuditApprovedEvent.class));
    }

    // === 审批驳回回调 ===

    @Test
    @DisplayName("审批驳回回调：status 1→3，日志含'审批流程驳回'")
    void rejected_shouldUpdateStatusAndLog() {
        DocumentStatusEntity docStatus = new DocumentStatusEntity();
        docStatus.setStatus(1);
        when(documentStatusMapper.selectForUpdate(BIZ_TYPE, 5002L)).thenReturn(docStatus);
        when(documentStatusMapper.updateStatus(BIZ_TYPE, 5002L, 3)).thenReturn(1);

        callback.rejected(BIZ_TYPE, 5002L, "金额超标");

        verify(documentStatusMapper).updateStatus(BIZ_TYPE, 5002L, 3);

        ArgumentCaptor<SysAuditLogEntity> logCaptor = ArgumentCaptor.forClass(SysAuditLogEntity.class);
        verify(auditLogMapper).insert(logCaptor.capture());
        SysAuditLogEntity logEntity = logCaptor.getValue();
        assertEquals(BIZ_TYPE, logEntity.getDocType());
        assertEquals(5002L, logEntity.getDocId());
        assertEquals("REJECT", logEntity.getOperationType());
        assertEquals(1, logEntity.getFromStatus());
        assertEquals(3, logEntity.getToStatus());
        assertEquals("审批流程驳回: 金额超标", logEntity.getOpinion());
    }

    // === 回调幂等 ===

    @Test
    @DisplayName("已Approved(2)的单据再次approved回调被忽略")
    void approved_shouldIgnoreWhenAlreadyApproved() {
        DocumentStatusEntity docStatus = new DocumentStatusEntity();
        docStatus.setStatus(2);
        when(documentStatusMapper.selectForUpdate(BIZ_TYPE, 5001L)).thenReturn(docStatus);

        callback.approved(BIZ_TYPE, 5001L, "再次通过");

        verify(documentStatusMapper, never()).updateStatus(anyString(), anyLong(), anyInt());
        verify(auditLogMapper, never()).insert(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("已Rejected(3)的单据再次rejected回调被忽略")
    void rejected_shouldIgnoreWhenAlreadyRejected() {
        DocumentStatusEntity docStatus = new DocumentStatusEntity();
        docStatus.setStatus(3);
        when(documentStatusMapper.selectForUpdate(BIZ_TYPE, 5002L)).thenReturn(docStatus);

        callback.rejected(BIZ_TYPE, 5002L, "再次驳回");

        verify(documentStatusMapper, never()).updateStatus(anyString(), anyLong(), anyInt());
        verify(auditLogMapper, never()).insert(any());
    }

    @Test
    @DisplayName("单据不存在时approved回调被忽略")
    void approved_shouldIgnoreWhenDocNotFound() {
        when(documentStatusMapper.selectForUpdate(BIZ_TYPE, 9999L)).thenReturn(null);

        callback.approved(BIZ_TYPE, 9999L, "同意");

        verify(documentStatusMapper, never()).updateStatus(anyString(), anyLong(), anyInt());
        verify(auditLogMapper, never()).insert(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
