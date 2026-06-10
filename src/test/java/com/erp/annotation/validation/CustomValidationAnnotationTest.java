package com.erp.annotation.validation;

import com.erp.common.exception.GlobalExceptionHandler;
import com.erp.common.enums.ErrorCode;
import com.erp.common.result.RT;
import com.erp.common.result.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 自定义校验注解(@Phone/@IdCard)验证测试.
 *
 * <p>任务: P0-001-002-003-001-002 验证字段级校验</p>
 *
 * <p>验证范围:
 * <ul>
 *   <li>@Phone: 正确手机号通过校验, 错误手机号失败</li>
 *   <li>@IdCard: 18位/15位身份证号通过校验, 错误格式失败</li>
 *   <li>null/空值不校验(由 @NotBlank 负责非空)</li>
 *   <li>校验失败时通过 GlobalExceptionHandler 返回 code=30001 + ValidationError列表</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
class CustomValidationAnnotationTest {

    private static Validator validator;
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/test");

    // ========== 测试用 DTO ==========

    /** 手机号校验 DTO */
    static class PhoneDto {
        @Phone(message = "手机号格式不正确")
        private String phone;

        public PhoneDto(String phone) {
            this.phone = phone;
        }

        public String getPhone() { return phone; }
    }

    /** 身份证号校验 DTO */
    static class IdCardDto {
        @IdCard(message = "身份证号格式不正确")
        private String idCard;

        public IdCardDto(String idCard) {
            this.idCard = idCard;
        }

        public String getIdCard() { return idCard; }
    }

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    // ========== @Phone 校验测试 ==========

    @Nested
    @DisplayName("@Phone 注解校验测试")
    class PhoneAnnotationTests {

        @Test
        @DisplayName("合法手机号(138xxxx) - 校验通过")
        void should_pass_when_validPhone() {
            PhoneDto dto = new PhoneDto("13812345678");
            Set<ConstraintViolation<PhoneDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "合法手机号应校验通过");
        }

        @Test
        @DisplayName("合法手机号(159xxxx) - 校验通过")
        void should_pass_when_validPhone159() {
            PhoneDto dto = new PhoneDto("15912345678");
            Set<ConstraintViolation<PhoneDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("合法手机号(199xxxx) - 校验通过")
        void should_pass_when_validPhone199() {
            PhoneDto dto = new PhoneDto("19912345678");
            Set<ConstraintViolation<PhoneDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("错误值(不足11位) - 校验失败")
        void should_fail_when_phoneTooShort() {
            PhoneDto dto = new PhoneDto("1381234");
            Set<ConstraintViolation<PhoneDto>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("手机号格式不正确", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("错误值(非1开头) - 校验失败")
        void should_fail_when_phoneNotStartWith1() {
            PhoneDto dto = new PhoneDto("23812345678");
            Set<ConstraintViolation<PhoneDto>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
        }

        @Test
        @DisplayName("错误值(12开头) - 校验失败")
        void should_fail_when_phoneSecondDigitIs2() {
            PhoneDto dto = new PhoneDto("12812345678");
            Set<ConstraintViolation<PhoneDto>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
        }

        @Test
        @DisplayName("错误值(含字母) - 校验失败")
        void should_fail_when_phoneContainsLetters() {
            PhoneDto dto = new PhoneDto("1381234abcd");
            Set<ConstraintViolation<PhoneDto>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
        }

        @Test
        @DisplayName("null值 - 不校验(通过)")
        void should_pass_when_phoneIsNull() {
            PhoneDto dto = new PhoneDto(null);
            Set<ConstraintViolation<PhoneDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "null值不应触发校验");
        }

        @Test
        @DisplayName("空字符串 - 不校验(通过)")
        void should_pass_when_phoneIsEmpty() {
            PhoneDto dto = new PhoneDto("");
            Set<ConstraintViolation<PhoneDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "空字符串不应触发校验");
        }
    }

    // ========== @IdCard 校验测试 ==========

    @Nested
    @DisplayName("@IdCard 注解校验测试")
    class IdCardAnnotationTests {

        @Test
        @DisplayName("合法18位身份证号 - 校验通过")
        void should_pass_when_validIdCard18() {
            IdCardDto dto = new IdCardDto("110101199003076534");
            Set<ConstraintViolation<IdCardDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "合法18位身份证号应校验通过");
        }

        @Test
        @DisplayName("合法18位身份证号(末尾X) - 校验通过")
        void should_pass_when_validIdCard18WithX() {
            IdCardDto dto = new IdCardDto("11010119900307653X");
            Set<ConstraintViolation<IdCardDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("合法18位身份证号(末尾小写x) - 校验通过")
        void should_pass_when_validIdCard18WithLowerX() {
            IdCardDto dto = new IdCardDto("11010119900307653x");
            Set<ConstraintViolation<IdCardDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("合法15位旧版身份证号 - 校验通过")
        void should_pass_when_validIdCard15() {
            IdCardDto dto = new IdCardDto("110101900307653");
            Set<ConstraintViolation<IdCardDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "合法15位身份证号应校验通过");
        }

        @Test
        @DisplayName("错误值(不足15位) - 校验失败")
        void should_fail_when_idCardTooShort() {
            IdCardDto dto = new IdCardDto("11010190");
            Set<ConstraintViolation<IdCardDto>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("身份证号格式不正确", violations.iterator().next().getMessage());
        }

        @Test
        @DisplayName("错误值(超过18位) - 校验失败")
        void should_fail_when_idCardTooLong() {
            IdCardDto dto = new IdCardDto("11010119900307653456");
            Set<ConstraintViolation<IdCardDto>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
        }

        @Test
        @DisplayName("错误值(含字母非末尾X) - 校验失败")
        void should_fail_when_idCardContainsInvalidChars() {
            IdCardDto dto = new IdCardDto("11010A199003076534");
            Set<ConstraintViolation<IdCardDto>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
        }

        @Test
        @DisplayName("null值 - 不校验(通过)")
        void should_pass_when_idCardIsNull() {
            IdCardDto dto = new IdCardDto(null);
            Set<ConstraintViolation<IdCardDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "null值不应触发校验");
        }

        @Test
        @DisplayName("空字符串 - 不校验(通过)")
        void should_pass_when_idCardIsEmpty() {
            IdCardDto dto = new IdCardDto("");
            Set<ConstraintViolation<IdCardDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "空字符串不应触发校验");
        }
    }

    // ========== 与 GlobalExceptionHandler 集成验证 ==========

    @Nested
    @DisplayName("自定义注解与 GlobalExceptionHandler 集成测试")
    class IntegrationTests {

        @Test
        @DisplayName("@Phone校验失败 - 经Handler返回code=30001 + ValidationError")
        void should_returnValidationError_when_phoneFails() {
            PhoneDto dto = new PhoneDto("12345");
            Set<ConstraintViolation<PhoneDto>> violations = validator.validate(dto);
            ConstraintViolationException exception = new ConstraintViolationException(violations);

            RT<List<ValidationError>> result = handler.handleConstraintViolationException(exception, request);

            assertEquals(ErrorCode.PARAM_INVALID.getCode(), result.getCode(), "错误码应为30001");
            assertEquals("参数校验失败", result.getMessage());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            ValidationError error = result.getData().get(0);
            assertEquals("phone", error.getField());
            assertEquals("手机号格式不正确", error.getMessage());
            assertEquals("12345", error.getRejectedValue());
        }

        @Test
        @DisplayName("@IdCard校验失败 - 经Handler返回code=30001 + ValidationError")
        void should_returnValidationError_when_idCardFails() {
            IdCardDto dto = new IdCardDto("invalid");
            Set<ConstraintViolation<IdCardDto>> violations = validator.validate(dto);
            ConstraintViolationException exception = new ConstraintViolationException(violations);

            RT<List<ValidationError>> result = handler.handleConstraintViolationException(exception, request);

            assertEquals(ErrorCode.PARAM_INVALID.getCode(), result.getCode(), "错误码应为30001");
            assertEquals("参数校验失败", result.getMessage());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            ValidationError error = result.getData().get(0);
            assertEquals("idCard", error.getField());
            assertEquals("身份证号格式不正确", error.getMessage());
            assertEquals("invalid", error.getRejectedValue());
        }
    }
}
