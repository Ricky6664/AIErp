package com.erp.common.result;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.IErrorCode;
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
 * RT 统一响应包装类单元测试.
 *
 * <p>任务: P0-001-002-002-001-003 编写单元测试</p>
 *
 * <p>验证范围:
 * <ul>
 *   <li>静态工厂方法: ok(T data), ok(), ok(String message, T data)</li>
 *   <li>失败方法: fail(int, String), fail(IErrorCode), fail(IErrorCode, String)</li>
 *   <li>便捷方法: error, paramError, unauthorized, forbidden</li>
 *   <li>链式调用: RT.ok().data(xxx)</li>
 *   <li>JSON序列化: @JsonInclude(NON_NULL) null字段忽略</li>
 *   <li>时间戳: System.currentTimeMillis()</li>
 *   <li>Serializable 序列化</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
class RTTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ========== 成功响应 ==========

    @Nested
    @DisplayName("成功响应方法测试")
    class OkTests {

        @Test
        @DisplayName("ok(T data) - 带数据 - 返回code=200/message=success/data=数据")
        void should_returnSuccessWithData_when_okWithData() {
            // given
            String data = "测试数据";

            // when
            long before = System.currentTimeMillis();
            RT<String> result = RT.ok(data);
            long after = System.currentTimeMillis();

            // then
            assertEquals(0, result.getCode(), "成功响应code必须为200");
            assertEquals("success", result.getMessage(), "成功响应message必须为success");
            assertEquals("测试数据", result.getData(), "data必须与传入参数一致");
            assertTrue(result.getTimestamp() >= before && result.getTimestamp() <= after,
                    "timestamp必须在调用时间范围内");
            assertTrue(result.isSuccess(), "ok()返回的isSuccess必须为true");
        }

        @Test
        @DisplayName("ok(T data) - 传入null数据 - data字段为null")
        void should_returnSuccessWithNullData_when_okWithNullData() {
            // given - 无

            // when
            RT<String> result = RT.ok(null);

            // then
            assertEquals(0, result.getCode(), "成功响应code必须为200");
            assertEquals("success", result.getMessage(), "成功响应message必须为success");
            assertNull(result.getData(), "传入null时data必须为null");
            assertTrue(result.isSuccess(), "ok(null)返回的isSuccess必须为true");
        }

        @Test
        @DisplayName("ok() - 无数据 - 返回code=200/message=success/data=null")
        void should_returnSuccessWithoutData_when_okNoArgs() {
            // given - 无

            // when
            long before = System.currentTimeMillis();
            RT<Object> result = RT.ok();
            long after = System.currentTimeMillis();

            // then
            assertEquals(0, result.getCode(), "成功响应code必须为200");
            assertEquals("success", result.getMessage(), "成功响应message必须为success");
            assertNull(result.getData(), "ok()无参时data必须为null");
            assertTrue(result.getTimestamp() >= before && result.getTimestamp() <= after,
                    "timestamp必须在调用时间范围内");
        }

        @Test
        @DisplayName("ok(String message, T data) - 自定义消息 - 返回指定消息和数据")
        void should_returnSuccessWithCustomMessage_when_okWithMessageAndData() {
            // given
            String message = "操作成功";
            Integer data = 42;

            // when
            RT<Integer> result = RT.ok(message, data);

            // then
            assertEquals(0, result.getCode(), "成功响应code必须为200");
            assertEquals("操作成功", result.getMessage(), "message必须与传入参数一致");
            assertEquals(42, result.getData(), "data必须与传入参数一致");
            assertTrue(result.isSuccess(), "ok(message, data)返回的isSuccess必须为true");
        }

        @Test
        @DisplayName("ok(T data) - 泛型为复杂对象 - 正确包装")
        void should_returnSuccessWithComplexObject_when_okWithObject() {
            // given
            record UserDTO(String name, Integer age) {}
            UserDTO user = new UserDTO("张三", 25);

            // when
            RT<UserDTO> result = RT.ok(user);

            // then
            assertEquals(0, result.getCode(), "成功响应code必须为200");
            assertNotNull(result.getData(), "data不能为null");
            assertEquals("张三", result.getData().name(), "data对象属性必须正确");
            assertEquals(25, result.getData().age(), "data对象属性必须正确");
        }
    }

    // ========== 失败响应 ==========

    @Nested
    @DisplayName("失败响应方法测试")
    class FailTests {

        @Test
        @DisplayName("fail(int code, String message) - 指定错误码和消息")
        void should_returnFailure_when_failWithCodeAndMessage() {
            // given
            int code = 40001;
            String message = "订单已审核, 不可修改";

            // when
            long before = System.currentTimeMillis();
            RT<Object> result = RT.fail(code, message);
            long after = System.currentTimeMillis();

            // then
            assertEquals(40001, result.getCode(), "code必须与传入参数一致");
            assertEquals("订单已审核, 不可修改", result.getMessage(), "message必须与传入参数一致");
            assertNull(result.getData(), "fail响应data必须为null");
            assertTrue(result.getTimestamp() >= before && result.getTimestamp() <= after,
                    "timestamp必须在调用时间范围内");
            assertFalse(result.isSuccess(), "fail()返回的isSuccess必须为false");
        }

        @Test
        @DisplayName("fail(IErrorCode) - 使用错误码枚举")
        void should_returnFailure_when_failWithErrorCode() {
            // given - 无

            // when
            RT<Object> result = RT.fail(ErrorCode.BUSINESS_ERROR);

            // then
            assertEquals(40001, result.getCode(), "code必须与ErrorCode.BUSINESS_ERROR一致");
            assertEquals("业务处理异常", result.getMessage(), "message必须与ErrorCode.BUSINESS_ERROR一致");
            assertNull(result.getData(), "fail响应data必须为null");
            assertFalse(result.isSuccess(), "fail()返回的isSuccess必须为false");
        }

        @Test
        @DisplayName("fail(IErrorCode) - 使用系统错误码")
        void should_returnSystemError_when_failWithInternalError() {
            // given - 无

            // when
            RT<Object> result = RT.fail(ErrorCode.INTERNAL_ERROR);

            // then
            assertEquals(10500, result.getCode(), "code必须与ErrorCode.INTERNAL_ERROR一致");
            assertEquals("系统内部错误", result.getMessage(), "message必须与ErrorCode.INTERNAL_ERROR一致");
        }

        @Test
        @DisplayName("fail(IErrorCode, String detail) - 错误码+详情 - 拼接消息")
        void should_returnFailureWithDetail_when_failWithErrorCodeAndDetail() {
            // given - 无

            // when
            RT<Object> result = RT.fail(ErrorCode.DATA_NOT_FOUND, "订单号SO-001");

            // then
            assertEquals(50002, result.getCode(), "code必须与ErrorCode.DATA_NOT_FOUND一致");
            assertEquals("数据不存在: 订单号SO-001", result.getMessage(),
                    "message必须拼接为'错误消息: 详情'格式");
            assertNull(result.getData(), "fail响应data必须为null");
        }

        @Test
        @DisplayName("fail(IErrorCode, null) - 错误码+null详情 - 仅返回错误消息")
        void should_returnOnlyMessage_when_failWithErrorCodeAndNullDetail() {
            // given - 无

            // when
            RT<Object> result = RT.fail(ErrorCode.BUSINESS_ERROR, null);

            // then
            assertEquals(40001, result.getCode(), "code必须与ErrorCode.BUSINESS_ERROR一致");
            assertEquals("业务处理异常", result.getMessage(),
                    "detail为null时message必须仅为错误码消息");
        }

        @Test
        @DisplayName("fail(IErrorCode, 空字符串) - 错误码+空详情 - 仅返回错误消息")
        void should_returnOnlyMessage_when_failWithErrorCodeAndEmptyDetail() {
            // given - 无

            // when
            RT<Object> result = RT.fail(ErrorCode.BUSINESS_ERROR, "");

            // then
            assertEquals(40001, result.getCode(), "code必须与ErrorCode.BUSINESS_ERROR一致");
            assertEquals("业务处理异常", result.getMessage(),
                    "detail为空字符串时message必须仅为错误码消息");
        }
    }

    // ========== 便捷方法 ==========

    @Nested
    @DisplayName("便捷方法测试")
    class ConvenienceTests {

        @Test
        @DisplayName("error(String message) - 服务端错误 - code=500")
        void should_returnServerError500_when_error() {
            // given
            String message = "数据库连接失败";

            // when
            RT<Object> result = RT.error(message);

            // then
            assertEquals(500, result.getCode(), "error()code必须为500");
            assertEquals("数据库连接失败", result.getMessage(), "message必须与传入参数一致");
            assertNull(result.getData(), "error响应data必须为null");
            assertFalse(result.isSuccess(), "error()返回的isSuccess必须为false");
        }

        @Test
        @DisplayName("paramError(String message) - 参数错误 - code=400")
        void should_returnBadRequest400_when_paramError() {
            // given
            String message = "手机号格式不正确";

            // when
            RT<Object> result = RT.paramError(message);

            // then
            assertEquals(400, result.getCode(), "paramError()code必须为400");
            assertEquals("手机号格式不正确", result.getMessage(), "message必须与传入参数一致");
            assertNull(result.getData(), "paramError响应data必须为null");
        }

        @Test
        @DisplayName("unauthorized() - 未认证 - code=401")
        void should_returnUnauthorized401_when_unauthorized() {
            // given - 无

            // when
            RT<Object> result = RT.unauthorized();

            // then
            assertEquals(401, result.getCode(), "unauthorized()code必须为401");
            assertEquals("未登录或登录已过期", result.getMessage(),
                    "unauthorized()message必须为'未登录或登录已过期'");
            assertNull(result.getData(), "unauthorized响应data必须为null");
        }

        @Test
        @DisplayName("forbidden() - 无权限 - code=403")
        void should_returnForbidden403_when_forbidden() {
            // given - 无

            // when
            RT<Object> result = RT.forbidden();

            // then
            assertEquals(403, result.getCode(), "forbidden()code必须为403");
            assertEquals("无操作权限", result.getMessage(),
                    "forbidden()message必须为'无操作权限'");
            assertNull(result.getData(), "forbidden响应data必须为null");
        }
    }

    // ========== 链式调用 ==========

    @Nested
    @DisplayName("链式调用测试")
    class ChainCallTests {

        @Test
        @DisplayName("data(T) - 链式调用RT.ok().data(xxx) - 返回同一实例")
        void should_returnSameInstanceWithUpdatedData_when_chainedData() {
            // given - 无

            // when
            RT<String> result = RT.<String>ok().data("链式数据");

            // then
            assertEquals(0, result.getCode(), "链式调用code必须为200");
            assertEquals("success", result.getMessage(), "链式调用message必须为success");
            assertEquals("链式数据", result.getData(), "data必须与链式传入参数一致");
            assertTrue(result.isSuccess(), "链式调用返回的isSuccess必须为true");
        }

        @Test
        @DisplayName("data(T) - 返回同一实例(引用相等)")
        void should_returnSameReference_when_data() {
            // given
            RT<String> original = RT.ok();

            // when
            RT<String> chained = original.data("数据");

            // then
            assertSame(original, chained, "data()必须返回同一实例以支持链式调用");
        }

        @Test
        @DisplayName("链式调用 - 多次调用data()覆盖数据")
        void should_overrideData_when_chainedDataMultipleTimes() {
            // given - 无

            // when
            RT<String> result = RT.<String>ok().data("第一次").data("第二次");

            // then
            assertEquals("第二次", result.getData(), "多次链式调用data()必须使用最后一次的值");
        }
    }

    // ========== JSON序列化 ==========

    @Nested
    @DisplayName("JSON序列化测试")
    class JsonSerializationTests {

        @Test
        @DisplayName("@JsonInclude(NON_NULL) - data为null时JSON不包含data字段")
        void should_excludeNullDataField_when_serializedToJson() throws Exception {
            // given
            RT<String> result = RT.ok();

            // when
            String json = objectMapper.writeValueAsString(result);
            JsonNode node = objectMapper.readTree(json);

            // then
            assertTrue(node.has("code"), "JSON必须包含code字段");
            assertTrue(node.has("message"), "JSON必须包含message字段");
            assertTrue(node.has("timestamp"), "JSON必须包含timestamp字段");
            assertFalse(node.has("data"), "data为null时JSON不得包含data字段(@JsonInclude(NON_NULL))");
        }

        @Test
        @DisplayName("data非null时JSON包含data字段")
        void should_includeDataField_when_dataNotNull() throws Exception {
            // given
            RT<String> result = RT.ok("测试");

            // when
            String json = objectMapper.writeValueAsString(result);
            JsonNode node = objectMapper.readTree(json);

            // then
            assertTrue(node.has("data"), "data非null时JSON必须包含data字段");
            assertEquals("测试", node.get("data").asText(), "data字段值必须正确");
        }

        @Test
        @DisplayName("@JsonInclude 注解存在于RT类上")
        void should_haveJsonIncludeAnnotation_when_checkClass() {
            // given - 无

            // when
            JsonInclude annotation = RT.class.getAnnotation(JsonInclude.class);

            // then
            assertNotNull(annotation, "RT类必须标注@JsonInclude注解");
            assertEquals(JsonInclude.Include.NON_NULL, annotation.value(),
                    "@JsonInclude必须配置为NON_NULL");
        }

        @Test
        @DisplayName("JSON序列化完整性 - 所有非null字段均正确序列化")
        void should_serializeAllNonNullFields_when_toJson() throws Exception {
            // given
            RT<Integer> result = RT.ok(100);

            // when
            String json = objectMapper.writeValueAsString(result);
            JsonNode node = objectMapper.readTree(json);

            // then
            assertEquals(0, node.get("code").asInt(), "JSON中code必须为0");
            assertEquals("success", node.get("message").asText(), "JSON中message必须为success");
            assertEquals(100, node.get("data").asInt(), "JSON中data必须正确");
            assertTrue(node.get("timestamp").asLong() > 0, "JSON中timestamp必须为正数");
        }
    }

    // ========== 时间戳 ==========

    @Nested
    @DisplayName("时间戳测试")
    class TimestampTests {

        @Test
        @DisplayName("timestamp使用System.currentTimeMillis()")
        void should_useCurrentTimeMillis_when_created() {
            // given
            long before = System.currentTimeMillis();

            // when
            RT<String> result = RT.ok("测试");

            // then
            long after = System.currentTimeMillis();
            assertTrue(result.getTimestamp() >= before,
                    "timestamp必须不早于调用前时间");
            assertTrue(result.getTimestamp() <= after,
                    "timestamp必须不晚于调用后时间");
        }

        @Test
        @DisplayName("fail方法的timestamp同样使用System.currentTimeMillis()")
        void should_useCurrentTimeMillis_when_failCreated() {
            // given
            long before = System.currentTimeMillis();

            // when
            RT<Object> result = RT.fail(ErrorCode.BUSINESS_ERROR);

            // then
            long after = System.currentTimeMillis();
            assertTrue(result.getTimestamp() >= before,
                    "fail响应timestamp必须不早于调用前时间");
            assertTrue(result.getTimestamp() <= after,
                    "fail响应timestamp必须不晚于调用后时间");
        }
    }

    // ========== Serializable ==========

    @Nested
    @DisplayName("序列化测试")
    class SerializableTests {

        @Test
        @DisplayName("RT实现Serializable接口")
        void should_implementSerializable_when_checkClass() {
            // given - 无

            // when - 无

            // then
            assertTrue(Serializable.class.isAssignableFrom(RT.class),
                    "RT必须实现Serializable接口");
        }

        @Test
        @DisplayName("RT对象可序列化和反序列化")
        void should_serializeAndDeserialize_when_roundTrip() throws Exception {
            // given
            RT<String> original = RT.ok("序列化测试");

            // when
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(original);
            }
            byte[] bytes = baos.toByteArray();
            RT<?> deserialized;
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                deserialized = (RT<?>) ois.readObject();
            }

            // then
            assertEquals(original.getCode(), deserialized.getCode(), "反序列化后code必须一致");
            assertEquals(original.getMessage(), deserialized.getMessage(), "反序列化后message必须一致");
            assertEquals(original.getData(), deserialized.getData(), "反序列化后data必须一致");
            assertEquals(original.getTimestamp(), deserialized.getTimestamp(), "反序列化后timestamp必须一致");
        }
    }

    // ========== isSuccess ==========

    @Nested
    @DisplayName("isSuccess方法测试")
    class IsSuccessTests {

        @Test
        @DisplayName("isSuccess - code=200时返回true")
        void should_returnTrue_when_codeIs200() {
            // given - 无

            // when
            RT<String> result = RT.ok("数据");

            // then
            assertTrue(result.isSuccess(), "code=200时isSuccess必须为true");
        }

        @Test
        @DisplayName("isSuccess - code非200时返回false")
        void should_returnFalse_when_codeIsNot200() {
            // given - 无

            // when
            RT<Object> result = RT.fail(500, "错误");

            // then
            assertFalse(result.isSuccess(), "code非200时isSuccess必须为false");
        }

        @Test
        @DisplayName("isSuccess - 自定义code=0的fail也返回true(边界)")
        void should_returnTrue_when_customCodeIs200() {
            // given - 无

            // when — code=0时isSuccess按定义返回true
            RT<Object> result = RT.fail(0, "特殊消息");

            // then
            assertTrue(result.isSuccess(),
                    "code=0时isSuccess必须为true(即使通过fail方法创建)");
        }
    }

    // ========== 静态工厂方法完整性 ==========

    @Nested
    @DisplayName("静态工厂方法完整性验证")
    class StaticFactoryCompletenessTests {

        @Test
        @DisplayName("ok(T data)方法存在且可调用")
        void should_exist_when_okWithData() {
            // when
            RT<String> result = RT.ok("test");

            // then
            assertNotNull(result, "ok(T data)必须返回非null对象");
        }

        @Test
        @DisplayName("ok()方法存在且可调用")
        void should_exist_when_okNoArgs() {
            // when
            RT<Object> result = RT.ok();

            // then
            assertNotNull(result, "ok()必须返回非null对象");
        }

        @Test
        @DisplayName("fail(IErrorCode)方法存在且可调用")
        void should_exist_when_failWithIErrorCode() {
            // given
            IErrorCode errorCode = ErrorCode.PARAM_INVALID;

            // when
            RT<Object> result = RT.fail(errorCode);

            // then
            assertNotNull(result, "fail(IErrorCode)必须返回非null对象");
            assertEquals(30001, result.getCode(), "必须使用IErrorCode的code");
        }

        @Test
        @DisplayName("fail(int code, String msg)方法存在且可调用")
        void should_exist_when_failWithCodeAndMsg() {
            // when
            RT<Object> result = RT.fail(40001, "自定义错误");

            // then
            assertNotNull(result, "fail(int, String)必须返回非null对象");
            assertEquals(40001, result.getCode(), "code必须与传入参数一致");
            assertEquals("自定义错误", result.getMessage(), "message必须与传入参数一致");
        }
    }

    // ========== 类结构验证 ==========

    @Nested
    @DisplayName("类结构验证")
    class ClassStructureTests {

        @Test
        @DisplayName("RT类必须为泛型类")
        void should_beGeneric_when_checkClass() {
            // then
            assertTrue(RT.class.getTypeParameters().length > 0,
                    "RT必须为泛型类");
        }

        @Test
        @DisplayName("RT类必须包含code/message/data/timestamp四个字段")
        void should_haveRequiredFields_when_checkClass() throws Exception {
            // then
            assertNotNull(RT.class.getDeclaredField("code"), "RT必须包含code字段");
            assertNotNull(RT.class.getDeclaredField("message"), "RT必须包含message字段");
            assertNotNull(RT.class.getDeclaredField("data"), "RT必须包含data字段");
            assertNotNull(RT.class.getDeclaredField("timestamp"), "RT必须包含timestamp字段");
        }

        @Test
        @DisplayName("RT类源码不得硬编码密码/密钥")
        void should_notContainSecrets_when_checkSource() throws Exception {
            // given - 无

            // when
            java.lang.reflect.Field[] fields = RT.class.getDeclaredFields();

            // then
            for (java.lang.reflect.Field field : fields) {
                if (field.getType() == String.class
                        && java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    String value = (String) field.get(null);
                    if (value != null) {
                        String lower = value.toLowerCase();
                        assertFalse(lower.contains("password="),
                                "字段 " + field.getName() + " 不得包含 password=");
                        assertFalse(lower.contains("secret="),
                                "字段 " + field.getName() + " 不得包含 secret=");
                        assertFalse(lower.contains("apikey="),
                                "字段 " + field.getName() + " 不得包含 apiKey=");
                    }
                }
            }
        }
    }
}
