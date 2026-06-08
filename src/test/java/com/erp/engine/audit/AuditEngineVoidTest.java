package com.erp.engine.audit;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.dto.AuditVoidDTO;
import com.erp.engine.audit.entity.DocumentStatusEntity;
import com.erp.engine.audit.entity.SysAuditLogEntity;
import com.erp.engine.audit.event.VoidResourceReleaseEvent;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 作废接口单元测试.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
class AuditEngineVoidTest {

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

    private AuditVoidDTO createDTO(String voidReason) {
        AuditVoidDTO dto = new AuditVoidDTO();
        dto.setDocType(DOC_TYPE);
        dto.setDocId(DOC_ID);
        dto.setVoidReason(voidReason);
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
    void voidDocument_shouldVoidApprovedDoc() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(2));

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            auditEngineService.voidDocument(createDTO("测试作废"));
        }

        verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 4);
        verify(auditLogMapper).insert(any());
        verify(eventPublisher).publishEvent(any(VoidResourceReleaseEvent.class));
    }

    @Test
    void voidDocument_shouldVoidDraftDoc() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(0));

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            auditEngineService.voidDocument(createDTO("草稿作废"));
        }

        verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 4);
    }

    @Test
    void voidDocument_shouldVoidSubmittedDoc() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(1));

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            auditEngineService.voidDocument(createDTO("已提交作废"));
        }

        verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 4);
    }

    @Test
    void voidDocument_shouldRejectRejectedStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(3));

        assertThatThrownBy(() -> auditEngineService.voidDocument(createDTO("驳回作废")))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void voidDocument_shouldRejectAlreadyVoidedStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(4));

        assertThatThrownBy(() -> auditEngineService.voidDocument(createDTO("重复作废")))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void voidDocument_shouldRejectWhenDocNotFound() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(null);

        assertThatThrownBy(() -> auditEngineService.voidDocument(createDTO("不存在单据")))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void voidDocument_shouldRejectWhenLocked() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(false);

        assertThatThrownBy(() -> auditEngineService.voidDocument(createDTO("并发作废")))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void voidDocument_shouldRecordAuditLogWithCorrectFields() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(2));

        ArgumentCaptor<SysAuditLogEntity> logCaptor = ArgumentCaptor.forClass(SysAuditLogEntity.class);

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            auditEngineService.voidDocument(createDTO("测试作废原因"));
        }

        verify(auditLogMapper).insert(logCaptor.capture());
        SysAuditLogEntity captured = logCaptor.getValue();
        assertThat(captured.getDocType()).isEqualTo(DOC_TYPE);
        assertThat(captured.getDocId()).isEqualTo(DOC_ID);
        assertThat(captured.getOperationType()).isEqualTo("VOID");
        assertThat(captured.getOperatorId()).isEqualTo(1L);
        assertThat(captured.getFromStatus()).isEqualTo(2);
        assertThat(captured.getToStatus()).isEqualTo(4);
        assertThat(captured.getOpinion()).isEqualTo("测试作废原因");
        assertThat(captured.getCreatedAt()).isNotNull();
    }

    @Test
    void voidDocument_shouldPublishEventWithCorrectFromStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                .thenReturn(createDocStatus(2));

        ArgumentCaptor<VoidResourceReleaseEvent> eventCaptor =
                ArgumentCaptor.forClass(VoidResourceReleaseEvent.class);

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            auditEngineService.voidDocument(createDTO("资源释放测试"));
        }

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        VoidResourceReleaseEvent event = eventCaptor.getValue();
        assertThat(event.getDocType()).isEqualTo(DOC_TYPE);
        assertThat(event.getDocId()).isEqualTo(DOC_ID);
        assertThat(event.getFromStatus()).isEqualTo(2);
    }
}
