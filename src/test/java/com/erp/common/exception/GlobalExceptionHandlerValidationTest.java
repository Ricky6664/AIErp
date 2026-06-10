package com.erp.common.exception;

import com.erp.common.enums.ErrorCode;
import com.erp.common.result.RT;
import com.erp.common.result.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GlobalExceptionHandler 校验异常处理器单元测试.
 *
 * <p>任务: P0-001-002-003-001-001 引入Hibernate Validator依赖+配置全局校验异常处理器</p>
 *
 * <p>验证范围:
 * <ul>
 *   <li>MethodArgumentNotValidException: 返回 code=30001, data=ValidationError列表</li>
 *   <li>ConstraintViolationException: 返回 code=30001, data=ValidationError列表</li>
 *   <li>BindException: 返回 code=30001, data=ValidationError列表</li>
 *   <li>字段级错误提取: field/message/rejectedValue 完整</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
class GlobalExceptionHandlerValidationTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest("POST", "/api/test");
    }

    // ========== 测试用 DTO ==========

    /** 用于模拟 @RequestBody 参数校验的 DTO */
    static class UserDto {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @Email(message = "邮箱格式错误")
        private String email;

        @Min(value = 18, message = "年龄必须大于等于18")
        private Integer age;

        public UserDto(String username, String email, Integer age) {
            this.username = username;
            this.email = email;
            this.age = age;
        }

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public Integer getAge() { return age; }
    }

    /** 用于模拟 ConstraintViolation 的 DTO */
    static class QueryParam {
        @NotBlank(message = "查询关键字不能为空")
        private String keyword;

        @Min(value = 1, message = "页码必须大于等于1")
        private Integer pageNum;

        public QueryParam(String keyword, Integer pageNum) {
            this.keyword = keyword;
            this.pageNum = pageNum;
        }

        public String getKeyword() { return keyword; }
        public Integer getPageNum() { return pageNum; }
    }

    // ========== MethodArgumentNotValidException ==========

    @Nested
    @DisplayName("MethodArgumentNotValidException 处理测试")
    class MethodArgumentNotValidTests {

        @Test
        @DisplayName("单字段校验失败 - 返回 code=30001 + 1个ValidationError")
        void should_returnOneError_when_singleFieldFails() {
            UserDto dto = new UserDto("", "test@example.com", 20);
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(dto, "userDto");
            bindingResult.addError(new FieldError("userDto", "username", "", true, null, null, "用户名不能为空"));
            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

            RT<List<ValidationError>> result = handler.handleMethodArgumentNotValidException(exception, request);

            assertEquals(ErrorCode.PARAM_INVALID.getCode(), result.getCode(), "校验失败 code 应为 30001");
            assertEquals("参数校验失败", result.getMessage());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            ValidationError error = result.getData().get(0);
            assertEquals("username", error.getField());
            assertEquals("用户名不能为空", error.getMessage());
            assertEquals("", error.getRejectedValue());
        }

        @Test
        @DisplayName("多字段校验失败 - 返回多个ValidationError")
        void should_returnMultipleErrors_when_multipleFieldsFail() {
            UserDto dto = new UserDto("", "not-email", 10);
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(dto, "userDto");
            bindingResult.addError(new FieldError("userDto", "username", "", true, null, null, "用户名不能为空"));
            bindingResult.addError(new FieldError("userDto", "email", "not-email", true, null, null, "邮箱格式错误"));
            bindingResult.addError(new FieldError("userDto", "age", 10, true, null, null, "年龄必须大于等于18"));
            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

            RT<List<ValidationError>> result = handler.handleMethodArgumentNotValidException(exception, request);

            assertEquals(ErrorCode.PARAM_INVALID.getCode(), result.getCode());
            assertEquals(3, result.getData().size());

            List<String> fieldNames = result.getData().stream().map(ValidationError::getField).toList();
            assertTrue(fieldNames.contains("username"));
            assertTrue(fieldNames.contains("email"));
            assertTrue(fieldNames.contains("age"));
        }

        @Test
        @DisplayName("rejectedValue 正确携带非法值")
        void should_carryRejectedValue_when_fieldHasInvalidValue() {
            UserDto dto = new UserDto("test", "not-email", 20);
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(dto, "userDto");
            bindingResult.addError(new FieldError("userDto", "email", "not-email", true, null, null, "邮箱格式错误"));
            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

            RT<List<ValidationError>> result = handler.handleMethodArgumentNotValidException(exception, request);

            ValidationError error = result.getData().get(0);
            assertEquals("not-email", error.getRejectedValue());
        }

        @Test
        @DisplayName("timestamp 字段正常填充")
        void should_fillTimestamp_when_handlerReturns() {
            UserDto dto = new UserDto("", "test@example.com", 20);
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(dto, "userDto");
            bindingResult.addError(new FieldError("userDto", "username", "", true, null, null, "用户名不能为空"));
            MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

            long before = System.currentTimeMillis();
            RT<List<ValidationError>> result = handler.handleMethodArgumentNotValidException(exception, request);
            long after = System.currentTimeMillis();

            assertTrue(result.getTimestamp() >= before);
            assertTrue(result.getTimestamp() <= after);
        }
    }

    // ========== BindException ==========

    @Nested
    @DisplayName("BindException 处理测试")
    class BindExceptionTests {

        @Test
        @DisplayName("表单绑定校验失败 - 返回 code=30001 + ValidationError列表")
        void should_returnValidationErrorList_when_bindExceptionOccurs() {
            UserDto dto = new UserDto("", null, null);
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(dto, "userDto");
            bindingResult.addError(new FieldError("userDto", "username", "", true, null, null, "用户名不能为空"));
            BindException exception = new BindException(bindingResult);

            RT<List<ValidationError>> result = handler.handleBindException(exception, request);

            assertEquals(ErrorCode.PARAM_INVALID.getCode(), result.getCode());
            assertEquals("参数校验失败", result.getMessage());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals("username", result.getData().get(0).getField());
        }

        @Test
        @DisplayName("rejectedValue 在表单绑定时同样携带")
        void should_carryRejectedValue_when_bindException() {
            UserDto dto = new UserDto("test", "bad-email", 20);
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(dto, "userDto");
            bindingResult.addError(new FieldError("userDto", "email", "bad-email", true, null, null, "邮箱格式错误"));
            BindException exception = new BindException(bindingResult);

            RT<List<ValidationError>> result = handler.handleBindException(exception, request);

            assertEquals("bad-email", result.getData().get(0).getRejectedValue());
        }
    }

    // ========== ConstraintViolationException ==========

    @Nested
    @DisplayName("ConstraintViolationException 处理测试")
    class ConstraintViolationTests {

        private Validator validator;

        @BeforeEach
        void setUpValidator() {
            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                validator = factory.getValidator();
            }
        }

        @Test
        @DisplayName("单字段约束违反 - 返回 code=30001 + 1个ValidationError")
        void should_returnOneError_when_singleConstraintViolation() {
            QueryParam param = new QueryParam("", 1);
            Set<ConstraintViolation<QueryParam>> violations = validator.validate(param);
            ConstraintViolationException exception = new ConstraintViolationException(violations);

            RT<List<ValidationError>> result = handler.handleConstraintViolationException(exception, request);

            assertEquals(ErrorCode.PARAM_INVALID.getCode(), result.getCode());
            assertEquals("参数校验失败", result.getMessage());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            ValidationError error = result.getData().get(0);
            assertEquals("keyword", error.getField());
            assertEquals("查询关键字不能为空", error.getMessage());
        }

        @Test
        @DisplayName("多字段约束违反 - 返回多个ValidationError")
        void should_returnMultipleErrors_when_multipleConstraintViolations() {
            QueryParam param = new QueryParam("", 0);
            Set<ConstraintViolation<QueryParam>> violations = validator.validate(param);
            ConstraintViolationException exception = new ConstraintViolationException(violations);

            RT<List<ValidationError>> result = handler.handleConstraintViolationException(exception, request);

            assertEquals(ErrorCode.PARAM_INVALID.getCode(), result.getCode());
            assertEquals(2, result.getData().size());

            List<String> fieldNames = result.getData().stream().map(ValidationError::getField).toList();
            assertTrue(fieldNames.contains("keyword"));
            assertTrue(fieldNames.contains("pageNum"));
        }

        @Test
        @DisplayName("rejectedValue 携带非法值")
        void should_carryRejectedValue_when_constraintViolation() {
            QueryParam param = new QueryParam("", 1);
            Set<ConstraintViolation<QueryParam>> violations = validator.validate(param);
            ConstraintViolationException exception = new ConstraintViolationException(violations);

            RT<List<ValidationError>> result = handler.handleConstraintViolationException(exception, request);

            ValidationError error = result.getData().get(0);
            assertEquals("", error.getRejectedValue());
        }
    }

    // ========== 错误码一致性 ==========

    @Nested
    @DisplayName("错误码一致性测试")
    class ErrorCodeConsistencyTests {

        @Test
        @DisplayName("三种校验异常均返回相同错误码 30001")
        void should_returnSameCode_when_allThreeValidationExceptions() {
            // MethodArgumentNotValidException
            UserDto dto = new UserDto("", null, null);
            BeanPropertyBindingResult br1 = new BeanPropertyBindingResult(dto, "userDto");
            br1.addError(new FieldError("userDto", "username", "", true, null, null, "用户名不能为空"));
            RT<List<ValidationError>> r1 = handler.handleMethodArgumentNotValidException(
                    new MethodArgumentNotValidException(null, br1), request);

            // BindException
            BeanPropertyBindingResult br2 = new BeanPropertyBindingResult(dto, "userDto");
            br2.addError(new FieldError("userDto", "username", "", true, null, null, "用户名不能为空"));
            RT<List<ValidationError>> r2 = handler.handleBindException(
                    new BindException(br2), request);

            // ConstraintViolationException
            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                Validator validator = factory.getValidator();
                QueryParam param = new QueryParam("", 1);
                RT<List<ValidationError>> r3 = handler.handleConstraintViolationException(
                        new ConstraintViolationException(validator.validate(param)), request);

                assertEquals(r1.getCode(), r2.getCode());
                assertEquals(r2.getCode(), r3.getCode());
                assertEquals(ErrorCode.PARAM_INVALID.getCode(), r1.getCode());
                assertEquals("参数校验失败", r1.getMessage());
                assertEquals("参数校验失败", r2.getMessage());
                assertEquals("参数校验失败", r3.getMessage());
            }
        }
    }
}
