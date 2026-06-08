package com.erp.engine.audit;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.dto.AuditApproveDTO;
import com.erp.engine.audit.dto.AuditSubmitDTO;
import com.erp.engine.audit.entity.DocumentStatusEntity;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import com.erp.engine.audit.event.AuditApprovedEvent;
import com.erp.engine.audit.mapper.AuditLogMapper;
import com.erp.engine.audit.mapper.DocumentStatusMapper;
import com.erp.engine.audit.service.ApprovalIntegrationService;
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
 * AuditEngineService单元测试.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
class AuditEngineServiceTest {

    @Mock
    private AuditConfigService auditConfigService;
    @Mock
    private AuditLogMapper auditLogMapper;
    @Mock
    private DocumentStatusMapper documentStatusMapper;
    @Mock
    private ApprovalIntegrationService approvalIntegrationService;
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

    @Test
    void submit_shouldTransitionDraftToSubmitted() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        DocumentStatusEntity entity = new DocumentStatusEntity();
        entity.setDocType(DOC_TYPE);
        entity.setDocId(DOC_ID);
        entity.setStatus(0);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(entity);

        SysAuditConfigEntity config = new SysAuditConfigEntity();
        config.setDocType(DOC_TYPE);
        config.setApprovalEnabled(false);
        config.setAutoConfirm(false);
        when(auditConfigService.getConfig(DOC_TYPE)).thenReturn(config);

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

            AuditSubmitDTO dto = new AuditSubmitDTO(DOC_TYPE, DOC_ID, "请审核");
            auditEngineService.submit(dto);
        }

        verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 1);
        verify(auditLogMapper).insert(any());
        verify(approvalIntegrationService, never()).createApprovalInstance(anyString(), any());
    }

    @Test
    void submit_shouldAutoApproveWhenConfigEnabled() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        DocumentStatusEntity entity = new DocumentStatusEntity();
        entity.setDocType(DOC_TYPE);
        entity.setDocId(DOC_ID);
        entity.setStatus(0);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(entity);

        SysAuditConfigEntity config = new SysAuditConfigEntity();
        config.setDocType(DOC_TYPE);
        config.setApprovalEnabled(false);
        config.setAutoConfirm(true);
        when(auditConfigService.getConfig(DOC_TYPE)).thenReturn(config);

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

            AuditSubmitDTO dto = new AuditSubmitDTO(DOC_TYPE, DOC_ID, null);
            auditEngineService.submit(dto);
        }

        verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 1);
        verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 2);
        verify(eventPublisher).publishEvent(any(AuditApprovedEvent.class));
    }

    @Test
    void submit_shouldTriggerApprovalWhenEnabled() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        DocumentStatusEntity entity = new DocumentStatusEntity();
        entity.setDocType(DOC_TYPE);
        entity.setDocId(DOC_ID);
        entity.setStatus(0);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(entity);

        SysAuditConfigEntity config = new SysAuditConfigEntity();
        config.setDocType(DOC_TYPE);
        config.setApprovalEnabled(true);
        config.setAutoConfirm(false);
        when(auditConfigService.getConfig(DOC_TYPE)).thenReturn(config);

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

            AuditSubmitDTO dto = new AuditSubmitDTO(DOC_TYPE, DOC_ID, "请审核");
            auditEngineService.submit(dto);
        }

        verify(approvalIntegrationService).createApprovalInstance(DOC_TYPE, DOC_ID);
    }

    @Test
    void submit_shouldRejectNonDraftStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        DocumentStatusEntity entity = new DocumentStatusEntity();
        entity.setDocType(DOC_TYPE);
        entity.setDocId(DOC_ID);
        entity.setStatus(1);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(entity);

        AuditSubmitDTO dto = new AuditSubmitDTO(DOC_TYPE, DOC_ID, null);

        assertThatThrownBy(() -> auditEngineService.submit(dto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void approve_shouldTransitionSubmittedToApproved() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        DocumentStatusEntity entity = new DocumentStatusEntity();
        entity.setDocType(DOC_TYPE);
        entity.setDocId(DOC_ID);
        entity.setStatus(1);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(entity);

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

            AuditApproveDTO dto = new AuditApproveDTO(DOC_TYPE, DOC_ID, "同意");
            auditEngineService.approve(dto);
        }

        verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 2);
        verify(auditLogMapper).insert(any());
        verify(eventPublisher).publishEvent(any(AuditApprovedEvent.class));
    }

    @Test
    void approve_shouldRejectNonSubmittedStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        DocumentStatusEntity entity = new DocumentStatusEntity();
        entity.setDocType(DOC_TYPE);
        entity.setDocId(DOC_ID);
        entity.setStatus(2);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(entity);

        AuditApproveDTO dto = new AuditApproveDTO(DOC_TYPE, DOC_ID, "同意");

        assertThatThrownBy(() -> auditEngineService.approve(dto))
                .isInstanceOf(BusinessException.class);
    }
}
