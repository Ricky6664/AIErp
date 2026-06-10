package com.erp.engine.audit;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.dto.AuditOperationDTO;
import com.erp.engine.audit.entity.DocumentStatusEntity;
import com.erp.engine.audit.entity.SysAuditLogEntity;
import com.erp.engine.audit.event.CancelVoidResourceRestoreEvent;
import com.erp.engine.audit.mapper.AuditLogMapper;
import com.erp.engine.audit.mapper.DocumentStatusMapper;
import com.erp.engine.audit.service.AuditConfigService;
import com.erp.engine.audit.service.AuditEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 撤销作废接口单元测试.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
class AuditEngineCancelVoidTest {

    @Mock
    private AuditConfigService auditConfigService;
    @Mock
    private AuditLogMapper auditLogMapper;
    @Mock
    private DocumentStatusMapper documentStatusMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuditEngineService auditEngineService;

    private static final String DOC_TYPE = "PURCHASE_ORDER";
    private static final Long DOC_ID = 1001L;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    private AuditOperationDTO createDTO() {
        AuditOperationDTO dto = new AuditOperationDTO();
        dto.setDocType(DOC_TYPE);
        dto.setDocId(DOC_ID);
        return dto;
    }

    private DocumentStatusEntity createDocStatus(int status) {
        DocumentStatusEntity entity = new DocumentStatusEntity();
        entity.setDocType(DOC_TYPE);
        entity.setDocId(DOC_ID);
        entity.setStatus(status);
        return entity;
    }

    @Test
    void cancelVoid_shouldRestoreToPreviousStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(4));
        when(auditLogMapper.findPreviousStatusBeforeVoid(DOC_TYPE, DOC_ID))
                .thenReturn(2);

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            auditEngineService.cancelVoid(createDTO());
        }

        verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 2);
        verify(auditLogMapper).insert(any());
        verify(eventPublisher).publishEvent(any(CancelVoidResourceRestoreEvent.class));
    }

    @Test
    void cancelVoid_shouldRestoreToDraftStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(4));
        when(auditLogMapper.findPreviousStatusBeforeVoid(DOC_TYPE, DOC_ID))
                .thenReturn(0);

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            auditEngineService.cancelVoid(createDTO());
        }

        verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 0);
    }

    @Test
    void cancelVoid_shouldRestoreToSubmittedStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(4));
        when(auditLogMapper.findPreviousStatusBeforeVoid(DOC_TYPE, DOC_ID))
                .thenReturn(1);

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            auditEngineService.cancelVoid(createDTO());
        }

        verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 1);
    }

    @Test
    void cancelVoid_shouldRejectNonVoidedStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(2));

        assertThatThrownBy(() -> auditEngineService.cancelVoid(createDTO()))
                .isInstanceOf(BusinessException.class);

        verify(documentStatusMapper, never()).updateStatus(anyString(), any(), any(Integer.class));
    }

    @Test
    void cancelVoid_shouldRejectDraftStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(0));

        assertThatThrownBy(() -> auditEngineService.cancelVoid(createDTO()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void cancelVoid_shouldRejectSubmittedStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(1));

        assertThatThrownBy(() -> auditEngineService.cancelVoid(createDTO()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void cancelVoid_shouldRejectRejectedStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(3));

        assertThatThrownBy(() -> auditEngineService.cancelVoid(createDTO()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void cancelVoid_shouldRejectWhenDocNotFound() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(null);

        assertThatThrownBy(() -> auditEngineService.cancelVoid(createDTO()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void cancelVoid_shouldRejectWhenLocked() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(false);

        assertThatThrownBy(() -> auditEngineService.cancelVoid(createDTO()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void cancelVoid_shouldRejectWhenNoVoidLogFound() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(4));
        when(auditLogMapper.findPreviousStatusBeforeVoid(DOC_TYPE, DOC_ID))
                .thenReturn(null);

        assertThatThrownBy(() -> auditEngineService.cancelVoid(createDTO()))
                .isInstanceOf(BusinessException.class);

        verify(documentStatusMapper, never()).updateStatus(anyString(), any(), any(Integer.class));
    }

    @Test
    void cancelVoid_shouldRecordAuditLogWithCorrectFields() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(4));
        when(auditLogMapper.findPreviousStatusBeforeVoid(DOC_TYPE, DOC_ID))
                .thenReturn(2);

        ArgumentCaptor<SysAuditLogEntity> logCaptor = ArgumentCaptor.forClass(SysAuditLogEntity.class);

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            auditEngineService.cancelVoid(createDTO());
        }

        verify(auditLogMapper).insert(logCaptor.capture());
        SysAuditLogEntity captured = logCaptor.getValue();
        assertThat(captured.getDocType()).isEqualTo(DOC_TYPE);
        assertThat(captured.getDocId()).isEqualTo(DOC_ID);
        assertThat(captured.getOperationType()).isEqualTo("UNVOID");
        assertThat(captured.getOperatorId()).isEqualTo(1L);
        assertThat(captured.getFromStatus()).isEqualTo(4);
        assertThat(captured.getToStatus()).isEqualTo(2);
        assertThat(captured.getOpinion()).isEqualTo("撤销作废，恢复至状态：2");
        assertThat(captured.getCreatedAt()).isNotNull();
    }

    @Test
    void cancelVoid_shouldPublishEventWithCorrectRestoredStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(4));
        when(auditLogMapper.findPreviousStatusBeforeVoid(DOC_TYPE, DOC_ID))
                .thenReturn(1);

        ArgumentCaptor<CancelVoidResourceRestoreEvent> eventCaptor =
                ArgumentCaptor.forClass(CancelVoidResourceRestoreEvent.class);

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            auditEngineService.cancelVoid(createDTO());
        }

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        CancelVoidResourceRestoreEvent event = eventCaptor.getValue();
        assertThat(event.getDocType()).isEqualTo(DOC_TYPE);
        assertThat(event.getDocId()).isEqualTo(DOC_ID);
        assertThat(event.getRestoredStatus()).isEqualTo(1);
    }
}
