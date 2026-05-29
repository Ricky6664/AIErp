package com.erp.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ValidationError 字段校验错误项单元测试.
 *
 * <p>任务: P0-001-002-003-001-001 引入Hibernate Validator依赖+配置全局校验异常处理器</p>
 *
 * <p>验证范围:
 * <ul>
 *   <li>构造方法: 全参/无参</li>
 *   <li>静态工厂方法: of(field, message, rejectedValue), of(field, message)</li>
 *   <li>Getter/Setter/Equals/HashCode/ToString(Lombok @Data)</li>
 *   <li>JSON序列化: @JsonInclude(NON_NULL) null字段忽略</li>
 *   <li>Serializable 序列化/反序列化</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
class ValidationErrorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ========== 构造方法 ==========

    @Nested
    @DisplayName("构造方法测试")
    class ConstructorTests {

        @Test
        @DisplayName("无参构造 - 字段均为null")
        void should_createEmptyInstance_when_noArgConstructor() {
            ValidationError error = new ValidationError();

            assertNull(error.getField());
            assertNull(error.getMessage());
            assertNull(error.getRejectedValue());
        }

        @Test
        @DisplayName("全参构造 - 字段赋值正确")
        void should_setAllFields_when_allArgConstructor() {
            ValidationError error = new ValidationError("username", "用户名不能为空", "");

            assertEquals("username", error.getField());
            assertEquals("用户名不能为空", error.getMessage());
            assertEquals("", error.getRejectedValue());
        }

        @Test
        @DisplayName("全参构造 - rejectedValue可为null")
        void should_allowNullRejectedValue_when_allArgConstructor() {
            ValidationError error = new ValidationError("email", "邮箱格式错误", null);

            assertEquals("email", error.getField());
            assertEquals("邮箱格式错误", error.getMessage());
            assertNull(error.getRejectedValue());
        }
    }

    // ========== 静态工厂方法 ==========

    @Nested
    @DisplayName("静态工厂方法测试")
    class FactoryMethodTests {

        @Test
        @DisplayName("of(field, message, rejectedValue) - 完整参数")
        void should_createInstance_when_ofWithAllParams() {
            ValidationError error = ValidationError.of("phone", "手机号格式错误", "12345");

            assertEquals("phone", error.getField());
            assertEquals("手机号格式错误", error.getMessage());
            assertEquals("12345", error.getRejectedValue());
        }

        @Test
        @DisplayName("of(field, message) - 无非法值, rejectedValue=null")
        void should_createInstanceWithNullRejectedValue_when_ofWithTwoParams() {
            ValidationError error = ValidationError.of("idCard", "身份证号格式错误");

            assertEquals("idCard", error.getField());
            assertEquals("身份证号格式错误", error.getMessage());
            assertNull(error.getRejectedValue());
        }

        @Test
        @DisplayName("of() - 支持嵌套路径字段名(点号分隔)")
        void should_supportNestedFieldPath_when_ofWithDottedPath() {
            ValidationError error = ValidationError.of("user.email", "邮箱格式错误", "abc");

            assertEquals("user.email", error.getField());
        }

        @Test
        @DisplayName("of() - rejectedValue可为任意类型(String/Integer/Object)")
        void should_supportAnyTypeRejectedValue_when_ofWithDifferentTypes() {
            ValidationError strError = ValidationError.of("name", "名称错误", "abc");
            ValidationError intError = ValidationError.of("age", "年龄错误", 18);
            ValidationError objError = ValidationError.of("data", "数据错误", new int[]{1, 2});

            assertEquals("abc", strError.getRejectedValue());
            assertEquals(18, intError.getRejectedValue());
            assertInstanceOf(int[].class, objError.getRejectedValue());
        }
    }

    // ========== Getter/Setter ==========

    @Nested
    @DisplayName("Getter/Setter 测试")
    class GetterSetterTests {

        @Test
        @DisplayName("setField/getField - 正确读写")
        void should_readWriteField_when_setterGetter() {
            ValidationError error = new ValidationError();
            error.setField("username");

            assertEquals("username", error.getField());
        }

        @Test
        @DisplayName("setMessage/getMessage - 正确读写")
        void should_readWriteMessage_when_setterGetter() {
            ValidationError error = new ValidationError();
            error.setMessage("必填项");

            assertEquals("必填项", error.getMessage());
        }

        @Test
        @DisplayName("setRejectedValue/getRejectedValue - 正确读写")
        void should_readWriteRejectedValue_when_setterGetter() {
            ValidationError error = new ValidationError();
            error.setRejectedValue("invalid");

            assertEquals("invalid", error.getRejectedValue());
        }
    }

    // ========== Equals / HashCode ==========

    @Nested
    @DisplayName("Equals/HashCode 测试")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("equals - 字段完全相同返回true")
        void should_beEqual_when_sameFieldValues() {
            ValidationError a = ValidationError.of("username", "不能为空", "");
            ValidationError b = ValidationError.of("username", "不能为空", "");

            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }

        @Test
        @DisplayName("equals - 字段不同返回false")
        void should_notBeEqual_when_differentFieldValues() {
            ValidationError a = ValidationError.of("username", "不能为空", "");
            ValidationError b = ValidationError.of("email", "不能为空", "");

            assertNotEquals(a, b);
        }

        @Test
        @DisplayName("equals - rejectedValue不同返回false")
        void should_notBeEqual_when_differentRejectedValue() {
            ValidationError a = ValidationError.of("age", "错误", 10);
            ValidationError b = ValidationError.of("age", "错误", 20);

            assertNotEquals(a, b);
        }
    }

    // ========== JSON 序列化 ==========

    @Nested
    @DisplayName("JSON 序列化测试")
    class JsonSerializationTests {

        @Test
        @DisplayName("序列化 - 完整字段全部输出")
        void should_serializeAllFields_when_allPresent() throws Exception {
            ValidationError error = ValidationError.of("username", "不能为空", "");
            String json = objectMapper.writeValueAsString(error);
            JsonNode node = objectMapper.readTree(json);

            assertEquals("username", node.get("field").asText());
            assertEquals("不能为空", node.get("message").asText());
            assertEquals("", node.get("rejectedValue").asText());
        }

        @Test
        @DisplayName("序列化 - @JsonInclude(NON_NULL) 忽略null字段")
        void should_excludeNullFields_when_jsonIncludeNonNull() throws Exception {
            ValidationError error = ValidationError.of("email", "格式错误");
            String json = objectMapper.writeValueAsString(error);
            JsonNode node = objectMapper.readTree(json);

            assertTrue(node.has("field"));
            assertTrue(node.has("message"));
            assertFalse(node.has("rejectedValue"), "rejectedValue=null 时应被忽略");
        }

        @Test
        @DisplayName("序列化 - 所有字段为null时输出空对象")
        void should_serializeEmptyObject_when_allFieldsNull() throws Exception {
            ValidationError error = new ValidationError();
            String json = objectMapper.writeValueAsString(error);
            JsonNode node = objectMapper.readTree(json);

            assertEquals(0, node.size(), "所有null字段被忽略后应为空对象");
        }

        @Test
        @DisplayName("@JsonInclude注解 - 类级别已声明NON_NULL")
        void should_declareJsonIncludeAnnotation_when_classLevel() {
            JsonInclude annotation = ValidationError.class.getAnnotation(JsonInclude.class);
            assertNotNull(annotation);
            assertEquals(JsonInclude.Include.NON_NULL, annotation.value());
        }
    }

    // ========== Serializable ==========

    @Nested
    @DisplayName("Serializable 序列化测试")
    class SerializableTests {

        @Test
        @DisplayName("实现Serializable接口")
        void should_implementSerializable_when_classDeclaration() {
            assertTrue(Serializable.class.isAssignableFrom(ValidationError.class));
        }

        @Test
        @DisplayName("Java序列化/反序列化 - 字段保持完整")
        void should_preserveFields_when_javaSerializationRoundTrip() throws Exception {
            ValidationError original = ValidationError.of("username", "不能为空", "");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(original);
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                ValidationError restored = (ValidationError) ois.readObject();
                assertEquals(original, restored);
                assertEquals("username", restored.getField());
                assertEquals("不能为空", restored.getMessage());
                assertEquals("", restored.getRejectedValue());
            }
        }

        @Test
        @DisplayName("serialVersionUID - 已显式声明为1L")
        void should_declareSerialVersionUID_when_reflection() throws Exception {
            var field = ValidationError.class.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            assertEquals(1L, field.getLong(null));
        }
    }
}
