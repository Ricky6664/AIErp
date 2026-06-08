package com.erp.engine.audit;

import com.erp.engine.audit.dto.AuditOperationDTO;
import com.erp.engine.audit.event.CancelVoidResourceRestoreEvent;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 撤销作废资源恢复事件验证与DTO参数校验测试.
 *
 * @author AI
 */
@DisplayName("CancelVoidResourceRestoreEvent 验证测试")
class CancelVoidResourceRestoreEventTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ============================================================
    // CancelVoidResourceRestoreEvent 事件结构验证
    // ============================================================

    @Test
    @DisplayName("事件应包含docType、docId、restoredStatus三个字段")
    void shouldContainAllRequiredFields() {
        CancelVoidResourceRestoreEvent event = new CancelVoidResourceRestoreEvent(
                this, "sale_order", 1001L, 2);

        assertThat(event.getDocType()).isEqualTo("sale_order");
        assertThat(event.getDocId()).isEqualTo(1001L);
        assertThat(event.getRestoredStatus()).isEqualTo(2);
    }

    @Test
    @DisplayName("恢复至已审核(restoredStatus=2)事件携带完整信息")
    void shouldCarryRestoredStatusTwoForApprovedDoc() {
        CancelVoidResourceRestoreEvent event = new CancelVoidResourceRestoreEvent(
                this, "purchase_order", 4001L, 2);

        assertThat(event.getRestoredStatus()).isEqualTo(2);
        assertThat(event.getDocType()).isEqualTo("purchase_order");
        assertThat(event.getDocId()).isEqualTo(4001L);
    }

    @Test
    @DisplayName("恢复至已提交(restoredStatus=1)事件正确携带状态")
    void shouldCarryRestoredStatusOneForSubmittedDoc() {
        CancelVoidResourceRestoreEvent event = new CancelVoidResourceRestoreEvent(
                this, "sale_order", 4002L, 1);

        assertThat(event.getRestoredStatus()).isEqualTo(1);
        assertThat(event.getDocType()).isEqualTo("sale_order");
    }

    @Test
    @DisplayName("恢复至草稿(restoredStatus=0)事件正确携带状态，库存模块不应重新预留")
    void shouldCarryRestoredStatusZeroForDraftDoc() {
        CancelVoidResourceRestoreEvent event = new CancelVoidResourceRestoreEvent(
                this, "sale_order", 4003L, 0);

        assertThat(event.getRestoredStatus()).isZero();
        assertThat(event.getDocType()).isEqualTo("sale_order");
    }

    @Test
    @DisplayName("事件source应为发布者的this引用")
    void shouldSetSourceToPublisher() {
        CancelVoidResourceRestoreEventTest publisher = this;
        CancelVoidResourceRestoreEvent event = new CancelVoidResourceRestoreEvent(
                publisher, "sale_order", 1001L, 2);

        assertThat(event.getSource()).isSameAs(publisher);
    }

    // ============================================================
    // AuditOperationDTO 参数校验测试
    // ============================================================

    @Test
    @DisplayName("docType为null → 校验失败")
    void shouldFailValidationWhenDocTypeIsNull() {
        AuditOperationDTO dto = new AuditOperationDTO();
        dto.setDocType(null);
        dto.setDocId(1001L);

        Set<ConstraintViolation<AuditOperationDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getMessage().equals("单据类型不能为空"));
    }

    @Test
    @DisplayName("docType为空字符串 → 校验失败")
    void shouldFailValidationWhenDocTypeIsEmpty() {
        AuditOperationDTO dto = new AuditOperationDTO();
        dto.setDocType("");
        dto.setDocId(1001L);

        Set<ConstraintViolation<AuditOperationDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getMessage().equals("单据类型不能为空"));
    }

    @Test
    @DisplayName("docType为纯空格 → 校验失败（@NotBlank拦截）")
    void shouldFailValidationWhenDocTypeIsWhitespace() {
        AuditOperationDTO dto = new AuditOperationDTO();
        dto.setDocType("   ");
        dto.setDocId(1001L);

        Set<ConstraintViolation<AuditOperationDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getMessage().equals("单据类型不能为空"));
    }

    @Test
    @DisplayName("docId为null → 校验失败")
    void shouldFailValidationWhenDocIdIsNull() {
        AuditOperationDTO dto = new AuditOperationDTO();
        dto.setDocType("sale_order");
        dto.setDocId(null);

        Set<ConstraintViolation<AuditOperationDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getMessage().equals("单据ID不能为空"));
    }

    @Test
    @DisplayName("docType和docId均合法 → 校验通过")
    void shouldPassValidationWhenAllFieldsValid() {
        AuditOperationDTO dto = new AuditOperationDTO();
        dto.setDocType("sale_order");
        dto.setDocId(1001L);

        Set<ConstraintViolation<AuditOperationDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
