package com.erp.engine.audit;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.engine.audit.dto.AuditOperationDTO;
import com.erp.engine.audit.entity.DocumentStatusEntity;
import com.erp.engine.audit.entity.SysAuditLogEntity;
import com.erp.engine.audit.mapper.AuditLogMapper;
import com.erp.engine.audit.mapper.DocumentStatusMapper;
import com.erp.engine.audit.service.AuditConfigService;
import com.erp.engine.audit.service.AuditEngineService;
import com.erp.engine.audit.service.DownstreamCheckResult;
import com.erp.engine.audit.service.DownstreamChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AuditEngineService反审单元测试.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuditEngineService 反审单元测试")
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

    private static final String DOC_TYPE = "sale_order";
    private static final Long DOC_ID = 2001L;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    private DocumentStatusEntity createDocStatus(Long docId, Integer status) {
        DocumentStatusEntity entity = new DocumentStatusEntity();
        entity.setDocType(DOC_TYPE);
        entity.setDocId(docId);
        entity.setStatus(status);
        return entity;
    }

    private AuditOperationDTO createDTO(Long docId) {
        AuditOperationDTO dto = new AuditOperationDTO();
        dto.setDocType(DOC_TYPE);
        dto.setDocId(docId);
        return dto;
    }

    @Nested
    @DisplayName("正常反审流程")
    class NormalUnconfirm {

        @Test
        @DisplayName("1-正常反审-无下游: 已审核(status=2)单据反审成功, 状态2→0")
        void unconfirm_shouldTransitionApprovedToDraft() {
            when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .thenReturn(true);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                    .thenReturn(createDocStatus(DOC_ID, 2));
            when(auditConfigService.getDownstreamCheckers(DOC_TYPE))
                    .thenReturn(Collections.emptyList());

            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
                auditEngineService.unconfirm(createDTO(DOC_ID));
            }

            verify(documentStatusMapper).updateStatus(DOC_TYPE, DOC_ID, 0);
            verify(auditLogMapper).insert(any());
        }

        @Test
        @DisplayName("8-反审后日志验证: sys_audit_log记录operation_type=UNAUDIT, from_status=2, to_status=0")
        void unconfirm_shouldRecordCorrectAuditLog() {
            when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .thenReturn(true);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                    .thenReturn(createDocStatus(DOC_ID, 2));
            when(auditConfigService.getDownstreamCheckers(DOC_TYPE))
                    .thenReturn(Collections.emptyList());

            ArgumentCaptor<SysAuditLogEntity> logCaptor =
                    ArgumentCaptor.forClass(SysAuditLogEntity.class);

            try (MockedStatic<StpUtil> stpMock = mockStatic(StpUtil.class)) {
                stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
                auditEngineService.unconfirm(createDTO(DOC_ID));
            }

            verify(auditLogMapper).insert(logCaptor.capture());
            SysAuditLogEntity captured = logCaptor.getValue();
            assertThat(captured.getDocType()).isEqualTo(DOC_TYPE);
            assertThat(captured.getDocId()).isEqualTo(DOC_ID);
            assertThat(captured.getOperationType()).isEqualTo("UNAUDIT");
            assertThat(captured.getFromStatus()).isEqualTo(2);
            assertThat(captured.getToStatus()).isEqualTo(0);
            assertThat(captured.getOperatorId()).isEqualTo(1L);
            assertThat(captured.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("下游关联拦截")
    class DownstreamCheck {

        @Test
        @DisplayName("2-反审拦截-有下游: 关联发货单DN-20260001时抛出BusinessException(错误码40001)")
        void unconfirm_shouldRejectWhenDownstreamExists() {
            when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .thenReturn(true);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, 2002L))
                    .thenReturn(createDocStatus(2002L, 2));

            DownstreamChecker checker = mock(DownstreamChecker.class);
            when(checker.check(2002L))
                    .thenReturn(DownstreamCheckResult.exists("DN-20260001"));
            when(auditConfigService.getDownstreamCheckers(DOC_TYPE))
                    .thenReturn(List.of(checker));

            assertThatThrownBy(() -> auditEngineService.unconfirm(createDTO(2002L)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("code")
                    .isEqualTo(ErrorCode.BUSINESS_ERROR.getCode());
            verify(documentStatusMapper, never()).updateStatus(anyString(), any(), any(Integer.class));
        }

        @Test
        @DisplayName("9-多下游检查器: 第一个检查器拦截即返回, 后续不执行")
        void unconfirm_shouldStopAtFirstDownstreamChecker() {
            when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .thenReturn(true);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, DOC_ID))
                    .thenReturn(createDocStatus(DOC_ID, 2));

            DownstreamChecker checker1 = mock(DownstreamChecker.class);
            when(checker1.check(DOC_ID))
                    .thenReturn(DownstreamCheckResult.exists("DN-20260001"));
            DownstreamChecker checker2 = mock(DownstreamChecker.class);

            when(auditConfigService.getDownstreamCheckers(DOC_TYPE))
                    .thenReturn(List.of(checker1, checker2));

            assertThatThrownBy(() -> auditEngineService.unconfirm(createDTO(DOC_ID)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("code")
                    .isEqualTo(ErrorCode.BUSINESS_ERROR.getCode());
            verify(checker2, never()).check(any());
        }
    }

    @Nested
    @DisplayName("非已审核状态拦截")
    class NonApprovedStatus {

        @Test
        @DisplayName("3-反审拦截-草稿态: status=0时抛出DATA_STATUS_INVALID异常")
        void unconfirm_shouldRejectDraftStatus() {
            when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .thenReturn(true);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, 2003L))
                    .thenReturn(createDocStatus(2003L, 0));

            assertThatThrownBy(() -> auditEngineService.unconfirm(createDTO(2003L)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("code")
                    .isEqualTo(ErrorCode.DATA_STATUS_INVALID.getCode());
        }

        @Test
        @DisplayName("4-反审拦截-已提交态: status=1时抛出DATA_STATUS_INVALID异常")
        void unconfirm_shouldRejectSubmittedStatus() {
            when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .thenReturn(true);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, 2004L))
                    .thenReturn(createDocStatus(2004L, 1));

            assertThatThrownBy(() -> auditEngineService.unconfirm(createDTO(2004L)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("code")
                    .isEqualTo(ErrorCode.DATA_STATUS_INVALID.getCode());
        }

        @Test
        @DisplayName("5-反审拦截-已驳回态: status=3时抛出DATA_STATUS_INVALID异常")
        void unconfirm_shouldRejectRejectedStatus() {
            when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .thenReturn(true);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, 2005L))
                    .thenReturn(createDocStatus(2005L, 3));

            assertThatThrownBy(() -> auditEngineService.unconfirm(createDTO(2005L)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("code")
                    .isEqualTo(ErrorCode.DATA_STATUS_INVALID.getCode());
        }

        @Test
        @DisplayName("6-反审拦截-已作废态: status=4时抛出DATA_STATUS_INVALID异常")
        void unconfirm_shouldRejectVoidedStatus() {
            when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .thenReturn(true);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, 2006L))
                    .thenReturn(createDocStatus(2006L, 4));

            assertThatThrownBy(() -> auditEngineService.unconfirm(createDTO(2006L)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("code")
                    .isEqualTo(ErrorCode.DATA_STATUS_INVALID.getCode());
        }
    }

    @Nested
    @DisplayName("异常场景")
    class EdgeCases {

        @Test
        @DisplayName("10-反审不存在单据: docId=99999时抛出DATA_NOT_FOUND异常")
        void unconfirm_shouldRejectNonExistentDocument() {
            when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .thenReturn(true);
            when(documentStatusMapper.selectForUpdate(DOC_TYPE, 99999L))
                    .thenReturn(null);

            assertThatThrownBy(() -> auditEngineService.unconfirm(createDTO(99999L)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("code")
                    .isEqualTo(ErrorCode.DATA_NOT_FOUND.getCode());
        }

        @Test
        @DisplayName("分布式锁已被占用时抛出OPERATION_TOO_FREQUENT异常")
        void unconfirm_shouldRejectWhenLocked() {
            when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                    .thenReturn(false);

            assertThatThrownBy(() -> auditEngineService.unconfirm(createDTO(DOC_ID)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("code")
                    .isEqualTo(ErrorCode.OPERATION_TOO_FREQUENT.getCode());
        }
    }
}
