package com.erp.engine.audit;

import com.erp.engine.audit.dto.AuditVoidDTO;
import com.erp.engine.audit.event.VoidResourceReleaseEvent;
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
 * 作废资源释放事件验证与DTO参数校验测试.
 *
 * @author AI
 */
@DisplayName("VoidResourceReleaseEvent 验证测试")
class VoidResourceReleaseEventTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ============================================================
    // VoidResourceReleaseEvent 事件结构验证
    // ============================================================

    @Test
    @DisplayName("事件应包含docType、docId、fromStatus三个字段")
    void shouldContainAllRequiredFields() {
        VoidResourceReleaseEvent event = new VoidResourceReleaseEvent(
                this, "sale_order", 1001L, 2);

        assertThat(event.getDocType()).isEqualTo("sale_order");
        assertThat(event.getDocId()).isEqualTo(1001L);
        assertThat(event.getFromStatus()).isEqualTo(2);
    }

    @Test
    @DisplayName("已审核(fromStatus=2)作废事件携带完整信息")
    void shouldCarryFromStatusTwoForApprovedDoc() {
        VoidResourceReleaseEvent event = new VoidResourceReleaseEvent(
                this, "purchase_order", 3001L, 2);

        assertThat(event.getFromStatus()).isEqualTo(2);
        assertThat(event.getDocType()).isEqualTo("purchase_order");
        assertThat(event.getDocId()).isEqualTo(3001L);
    }

    @Test
    @DisplayName("草稿(fromStatus=0)作废事件fromStatus=0，下游不应释放预留")
    void shouldCarryFromStatusZeroForDraftDoc() {
        VoidResourceReleaseEvent event = new VoidResourceReleaseEvent(
                this, "sale_order", 3002L, 0);

        assertThat(event.getFromStatus()).isZero();
        assertThat(event.getDocType()).isEqualTo("sale_order");
    }

    @Test
    @DisplayName("已提交(fromStatus=1)作废事件正确携带状态")
    void shouldCarryFromStatusOneForSubmittedDoc() {
        VoidResourceReleaseEvent event = new VoidResourceReleaseEvent(
                this, "sale_order", 3003L, 1);

        assertThat(event.getFromStatus()).isEqualTo(1);
    }

    @Test
    @DisplayName("事件source应为发布者的this引用")
    void shouldSetSourceToPublisher() {
        VoidResourceReleaseEventTest publisher = this;
        VoidResourceReleaseEvent event = new VoidResourceReleaseEvent(
                publisher, "sale_order", 1001L, 2);

        assertThat(event.getSource()).isSameAs(publisher);
    }

    // ============================================================
    // AuditVoidDTO 参数校验测试
    // ============================================================

    @Test
    @DisplayName("voidReason为null → 校验失败")
    void shouldFailValidationWhenVoidReasonIsNull() {
        AuditVoidDTO dto = new AuditVoidDTO();
        dto.setDocType("sale_order");
        dto.setDocId(1001L);
        dto.setVoidReason(null);

        Set<ConstraintViolation<AuditVoidDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getMessage().equals("作废原因不能为空"));
    }

    @Test
    @DisplayName("voidReason为空字符串 → 校验失败")
    void shouldFailValidationWhenVoidReasonIsEmpty() {
        AuditVoidDTO dto = new AuditVoidDTO();
        dto.setDocType("sale_order");
        dto.setDocId(1001L);
        dto.setVoidReason("");

        Set<ConstraintViolation<AuditVoidDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getMessage().equals("作废原因不能为空"));
    }

    @Test
    @DisplayName("voidReason为纯空格 → 校验失败（@NotBlank拦截）")
    void shouldFailValidationWhenVoidReasonIsWhitespace() {
        AuditVoidDTO dto = new AuditVoidDTO();
        dto.setDocType("sale_order");
        dto.setDocId(1001L);
        dto.setVoidReason("   ");

        Set<ConstraintViolation<AuditVoidDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getMessage().equals("作废原因不能为空"));
    }

    @Test
    @DisplayName("voidReason合法 → 校验通过")
    void shouldPassValidationWhenVoidReasonIsValid() {
        AuditVoidDTO dto = new AuditVoidDTO();
        dto.setDocType("sale_order");
        dto.setDocId(1001L);
        dto.setVoidReason("客户取消订单");

        Set<ConstraintViolation<AuditVoidDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("docType为空 → 校验失败（父类@NotBlank）")
    void shouldFailValidationWhenDocTypeIsBlank() {
        AuditVoidDTO dto = new AuditVoidDTO();
        dto.setDocType("");
        dto.setDocId(1001L);
        dto.setVoidReason("测试");

        Set<ConstraintViolation<AuditVoidDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getMessage().equals("单据类型不能为空"));
    }

    @Test
    @DisplayName("docId为null → 校验失败（父类@NotNull）")
    void shouldFailValidationWhenDocIdIsNull() {
        AuditVoidDTO dto = new AuditVoidDTO();
        dto.setDocType("sale_order");
        dto.setDocId(null);
        dto.setVoidReason("测试");

        Set<ConstraintViolation<AuditVoidDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getMessage().equals("单据ID不能为空"));
    }
}
