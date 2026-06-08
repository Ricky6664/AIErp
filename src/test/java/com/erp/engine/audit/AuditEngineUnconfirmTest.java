package com.erp.engine.audit;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.dto.AuditOperationDTO;
import com.erp.engine.audit.entity.DocumentStatusEntity;
import com.erp.engine.audit.mapper.AuditLogMapper;
import com.erp.engine.audit.mapper.DocumentStatusMapper;
import com.erp.engine.audit.service.AuditConfigService;
import com.erp.engine.audit.service.AuditEngineService;
import com.erp.engine.audit.service.DownstreamCheckResult;
import com.erp.engine.audit.service.DownstreamChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AuditEngineService反审单元测试.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
class AuditEngineUnconfirmTest {

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

    @Test
    void unconfirm_shouldTransitionApprovedToDraft() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        DocumentStatusEntity entity = new DocumentStatusEntity();
        entity.setDocType(DOC_TYPE);
        entity.setDocId(DOC_ID);
        entity.setStatus(2);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(entity);
        when(auditConfigService.getDownstreamCheckers(DOC_TYPE))
                .thenReturn(Collections.emptyList());

        try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
            stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

            AuditOperationDTO dto = new AuditOperationDTO();
            dto.setDocType(DOC_TYPE);
            dto.setDocId(DOC_ID);
            auditEngineService.unconfirm(dto);
        }

        verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 0);
        verify(auditLogMapper).insert(any());
    }

    @Test
    void unconfirm_shouldRejectNonApprovedStatus() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        DocumentStatusEntity entity = new DocumentStatusEntity();
        entity.setDocType(DOC_TYPE);
        entity.setDocId(DOC_ID);
        entity.setStatus(1);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(entity);

        AuditOperationDTO dto = new AuditOperationDTO();
        dto.setDocType(DOC_TYPE);
        dto.setDocId(DOC_ID);

        assertThatThrownBy(() -> auditEngineService.unconfirm(dto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void unconfirm_shouldRejectNonExistentDocument() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(null);

        AuditOperationDTO dto = new AuditOperationDTO();
        dto.setDocType(DOC_TYPE);
        dto.setDocId(DOC_ID);

        assertThatThrownBy(() -> auditEngineService.unconfirm(dto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void unconfirm_shouldRejectWhenDownstreamExists() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);
        DocumentStatusEntity entity = new DocumentStatusEntity();
        entity.setDocType(DOC_TYPE);
        entity.setDocId(DOC_ID);
        entity.setStatus(2);
        when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID)).thenReturn(entity);

        DownstreamChecker checker = mock(DownstreamChecker.class);
        when(checker.check(DOC_ID)).thenReturn(DownstreamCheckResult.exists("PO-2026-001"));
        when(auditConfigService.getDownstreamCheckers(DOC_TYPE))
                .thenReturn(List.of(checker));

        AuditOperationDTO dto = new AuditOperationDTO();
        dto.setDocType(DOC_TYPE);
        dto.setDocId(DOC_ID);

        assertThatThrownBy(() -> auditEngineService.unconfirm(dto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void unconfirm_shouldRejectWhenLocked() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(false);

        AuditOperationDTO dto = new AuditOperationDTO();
        dto.setDocType(DOC_TYPE);
        dto.setDocId(DOC_ID);

        assertThatThrownBy(() -> auditEngineService.unconfirm(dto))
                .isInstanceOf(BusinessException.class);
    }
}
