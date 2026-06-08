package com.erp.engine.audit;

import com.erp.engine.audit.dto.AuditSubmitDTO;
import com.erp.engine.audit.entity.SysAuditConfigEntity;
import com.erp.engine.audit.event.DocCreatedEvent;
import com.erp.engine.audit.listener.AutoConfirmListener;
import com.erp.engine.audit.service.AuditConfigService;
import com.erp.engine.audit.service.AuditEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AutoConfirmListener单元测试.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AutoConfirmListener 单元测试")
class AutoConfirmListenerTest {

    @Mock
    private AuditConfigService auditConfigService;

    @Mock
    private AuditEngineService auditEngineService;

    @InjectMocks
    private AutoConfirmListener listener;

    private static final String DOC_TYPE = "sale_order";
    private static final Long DOC_ID = 6001L;

    private DocCreatedEvent event;

    @BeforeEach
    void setUp() {
        event = new DocCreatedEvent(this, DOC_TYPE, DOC_ID);
    }

    @Nested
    @DisplayName("自动确认生效场景")
    class AutoConfirmEnabled {

        @Test
        @DisplayName("autoConfirm=true时自动调用submit提交审核")
        void shouldAutoSubmitWhenAutoConfirmTrue() {
            SysAuditConfigEntity config = new SysAuditConfigEntity();
            config.setDocType(DOC_TYPE);
            config.setAutoConfirm(true);
            config.setApprovalEnabled(false);
            when(auditConfigService.getConfig(DOC_TYPE)).thenReturn(config);

            listener.onDocCreated(event);

            ArgumentCaptor<AuditSubmitDTO> captor = ArgumentCaptor.forClass(AuditSubmitDTO.class);
            verify(auditEngineService).submit(captor.capture());
            AuditSubmitDTO submitted = captor.getValue();
            assertThat(submitted.getDocType()).isEqualTo(DOC_TYPE);
            assertThat(submitted.getDocId()).isEqualTo(DOC_ID);
            assertThat(submitted.getSubmitRemark()).isEqualTo("系统自动确认");
        }
    }

    @Nested
    @DisplayName("自动确认未启用场景")
    class AutoConfirmDisabled {

        @Test
        @DisplayName("autoConfirm=false时不调用submit")
        void shouldNotSubmitWhenAutoConfirmFalse() {
            SysAuditConfigEntity config = new SysAuditConfigEntity();
            config.setDocType(DOC_TYPE);
            config.setAutoConfirm(false);
            when(auditConfigService.getConfig(DOC_TYPE)).thenReturn(config);

            listener.onDocCreated(event);

            verify(auditEngineService, never()).submit(any());
        }

        @Test
        @DisplayName("autoConfirm=null时不调用submit")
        void shouldNotSubmitWhenAutoConfirmNull() {
            SysAuditConfigEntity config = new SysAuditConfigEntity();
            config.setDocType(DOC_TYPE);
            config.setAutoConfirm(null);
            when(auditConfigService.getConfig(DOC_TYPE)).thenReturn(config);

            listener.onDocCreated(event);

            verify(auditEngineService, never()).submit(any());
        }
    }

    @Nested
    @DisplayName("异常隔离场景")
    class ExceptionIsolation {

        @Test
        @DisplayName("getConfig抛出异常时不传播，主流程不中断")
        void shouldNotPropagateWhenGetConfigThrows() {
            when(auditConfigService.getConfig(DOC_TYPE))
                    .thenThrow(new RuntimeException("Redis连接失败"));

            assertDoesNotThrow(() -> listener.onDocCreated(event));
            verify(auditEngineService, never()).submit(any());
        }

        @Test
        @DisplayName("submit抛出异常时不传播，主流程不中断")
        void shouldNotPropagateWhenSubmitThrows() {
            SysAuditConfigEntity config = new SysAuditConfigEntity();
            config.setDocType(DOC_TYPE);
            config.setAutoConfirm(true);
            when(auditConfigService.getConfig(DOC_TYPE)).thenReturn(config);
            doThrow(new RuntimeException("单据状态异常"))
                    .when(auditEngineService).submit(any());

            assertDoesNotThrow(() -> listener.onDocCreated(event));
        }
    }
}
