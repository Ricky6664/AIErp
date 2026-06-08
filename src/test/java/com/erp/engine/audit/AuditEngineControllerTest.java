package com.erp.engine.audit;

import com.erp.common.exception.BusinessException;
import com.erp.common.result.RT;
import com.erp.engine.audit.controller.AuditEngineController;
import com.erp.engine.audit.dto.AuditApproveDTO;
import com.erp.engine.audit.dto.AuditOperationDTO;
import com.erp.engine.audit.dto.AuditSubmitDTO;
import com.erp.engine.audit.dto.AuditVoidDTO;
import com.erp.engine.audit.service.AuditEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * AuditEngineController单元测试.
 *
 * @author AI
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuditEngineController 单元测试")
class AuditEngineControllerTest {

    @Mock
    private AuditEngineService auditEngineService;

    @InjectMocks
    private AuditEngineController controller;

    private static final String DOC_TYPE = "sale_order";

    @Nested
    @DisplayName("POST /api/engine/audit/submit")
    class Submit {

        @Test
        @DisplayName("正常提交 → 返回RT.ok")
        void shouldReturnOkOnSubmit() {
            AuditSubmitDTO dto = new AuditSubmitDTO(DOC_TYPE, 1001L, "请审核");

            RT<Void> result = controller.submit(dto);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getCode()).isEqualTo(0);
            verify(auditEngineService).submit(dto);
        }

        @Test
        @DisplayName("Service抛出BusinessException → 向上传播")
        void shouldPropagateBusinessException() {
            AuditSubmitDTO dto = new AuditSubmitDTO(DOC_TYPE, 1001L, "请审核");
            doThrow(new BusinessException(com.erp.common.enums.ErrorCode.DATA_STATUS_INVALID))
                    .when(auditEngineService).submit(any());

            assertThatThrownBy(() -> controller.submit(dto))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("POST /api/engine/audit/approve")
    class Approve {

        @Test
        @DisplayName("正常审核通过 → 返回RT.ok")
        void shouldReturnOkOnApprove() {
            AuditApproveDTO dto = new AuditApproveDTO(DOC_TYPE, 1001L, "同意");

            RT<Void> result = controller.approve(dto);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getCode()).isEqualTo(0);
            verify(auditEngineService).approve(dto);
        }

        @Test
        @DisplayName("Service抛出BusinessException → 向上传播")
        void shouldPropagateBusinessException() {
            AuditApproveDTO dto = new AuditApproveDTO(DOC_TYPE, 1001L, "同意");
            doThrow(new BusinessException(com.erp.common.enums.ErrorCode.DATA_STATUS_INVALID))
                    .when(auditEngineService).approve(any());

            assertThatThrownBy(() -> controller.approve(dto))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("POST /api/engine/audit/unconfirm")
    class Unconfirm {

        @Test
        @DisplayName("正常反审 → 返回RT.ok")
        void shouldReturnOkOnUnconfirm() {
            AuditOperationDTO dto = new AuditOperationDTO();
            dto.setDocType(DOC_TYPE);
            dto.setDocId(1001L);

            RT<Void> result = controller.unconfirm(dto);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getCode()).isEqualTo(0);
            verify(auditEngineService).unconfirm(dto);
        }

        @Test
        @DisplayName("Service抛出BusinessException → 向上传播")
        void shouldPropagateBusinessException() {
            AuditOperationDTO dto = new AuditOperationDTO();
            dto.setDocType(DOC_TYPE);
            dto.setDocId(1001L);
            doThrow(new BusinessException(com.erp.common.enums.ErrorCode.BUSINESS_ERROR))
                    .when(auditEngineService).unconfirm(any());

            assertThatThrownBy(() -> controller.unconfirm(dto))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("POST /api/engine/audit/void")
    class VoidDocument {

        @Test
        @DisplayName("正常作废 → 返回RT.ok")
        void shouldReturnOkOnVoid() {
            AuditVoidDTO dto = new AuditVoidDTO();
            dto.setDocType(DOC_TYPE);
            dto.setDocId(1001L);
            dto.setVoidReason("客户取消订单");

            RT<Void> result = controller.voidDocument(dto);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getCode()).isEqualTo(0);
            verify(auditEngineService).voidDocument(dto);
        }

        @Test
        @DisplayName("Service抛出BusinessException → 向上传播")
        void shouldPropagateBusinessException() {
            AuditVoidDTO dto = new AuditVoidDTO();
            dto.setDocType(DOC_TYPE);
            dto.setDocId(1001L);
            dto.setVoidReason("测试作废");
            doThrow(new BusinessException(com.erp.common.enums.ErrorCode.DATA_STATUS_INVALID))
                    .when(auditEngineService).voidDocument(any());

            assertThatThrownBy(() -> controller.voidDocument(dto))
                    .isInstanceOf(BusinessException.class);
        }
    }
}
