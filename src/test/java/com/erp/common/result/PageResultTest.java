package com.erp.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageResult 分页响应包装类单元测试.
 *
 * <p>任务: P0-001-002-002-002-003 编写单元测试</p>
 *
 * <p>验证范围:
 * <ul>
 *   <li>静态工厂方法: of(IPage), of(List, Long, Integer, Integer), empty()</li>
 *   <li>字段正确性: list/total/pageNum/pageSize/pages</li>
 *   <li>总页数计算: pages自动计算逻辑</li>
 *   <li>JSON序列化: @JsonInclude(NON_NULL) null字段忽略</li>
 *   <li>Serializable 序列化/反序列化</li>
 *   <li>类结构: 泛型/字段/注解</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
class PageResultTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ========== of(IPage<T>) 静态工厂方法 ==========

    @Nested
    @DisplayName("of(IPage) 静态工厂方法测试")
    class OfIPageTests {

        @Test
        @DisplayName("of(IPage) - 标准分页数据 - 所有字段正确转换")
        void should_convertAllFieldsCorrectly_when_fromIPage() {
            // given
            IPage<String> page = new Page<>(1, 20);
            page.setTotal(150);
            page.setRecords(Arrays.asList("a", "b", "c"));

            // when
            PageResult<String> result = PageResult.of(page);

            // then
            assertEquals(3, result.getList().size(), "list大小必须与IPage records一致");
            assertEquals("a", result.getList().get(0), "list元素必须与IPage records一致");
            assertEquals(150L, result.getTotal(), "total必须与IPage total一致");
            assertEquals(1, result.getPageNum(), "pageNum必须与IPage current一致");
            assertEquals(20, result.getPageSize(), "pageSize必须与IPage size一致");
            assertEquals(8, result.getPages(), "pages必须正确计算(150/20=8页向上取整)");
        }

        @Test
        @DisplayName("of(IPage) - 第3页数据 - pageNum正确")
        void should_setCorrectPageNum_when_pageIs3() {
            // given
            IPage<Integer> page = new Page<>(3, 10);
            page.setTotal(100);
            page.setRecords(Arrays.asList(21, 22, 23));

            // when
            PageResult<Integer> result = PageResult.of(page);

            // then
            assertEquals(3, result.getPageNum(), "pageNum必须为3");
            assertEquals(10, result.getPageSize(), "pageSize必须为10");
            assertEquals(100L, result.getTotal(), "total必须为100");
        }

        @Test
        @DisplayName("of(IPage) - 空记录列表 - list为空集合")
        void should_returnEmptyList_when_iPageRecordsEmpty() {
            // given
            IPage<String> page = new Page<>(1, 20);
            page.setTotal(0);
            page.setRecords(Collections.emptyList());

            // when
            PageResult<String> result = PageResult.of(page);

            // then
            assertNotNull(result.getList(), "list不得为null");
            assertTrue(result.getList().isEmpty(), "list必须为空集合");
            assertEquals(0L, result.getTotal(), "total必须为0");
            assertEquals(0, result.getPages(), "pages必须为0");
        }

        @Test
        @DisplayName("of(IPage) - 泛型为复杂对象 - 正确转换")
        void should_convertComplexObjects_when_iPageContainsDTOs() {
            // given
            record UserDTO(String name, Integer age) {}
            IPage<UserDTO> page = new Page<>(1, 10);
            page.setTotal(1);
            page.setRecords(List.of(new UserDTO("张三", 25)));

            // when
            PageResult<UserDTO> result = PageResult.of(page);

            // then
            assertEquals(1, result.getList().size(), "list大小必须正确");
            assertEquals("张三", result.getList().get(0).name(), "复杂对象属性必须正确");
            assertEquals(25, result.getList().get(0).age(), "复杂对象属性必须正确");
        }

        @Test
        @DisplayName("of(IPage) - pages字段来自IPage.getPages()")
        void should_useIPagePages_when_convert() {
            // given
            IPage<String> page = new Page<>(2, 10);
            page.setTotal(25);
            page.setRecords(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j"));

            // when
            PageResult<String> result = PageResult.of(page);

            // then
            assertEquals((int) page.getPages(), result.getPages(),
                    "pages必须与IPage.getPages()一致");
            assertEquals(3, result.getPages(), "25条/10每页=3页(向上取整)");
        }
    }

    // ========== of(List, Long, Integer, Integer) 手动构建 ==========

    @Nested
    @DisplayName("of(List, Long, Integer, Integer) 手动构建测试")
    class OfManualTests {

        @Test
        @DisplayName("of(list, total, pageNum, pageSize) - 标准数据 - 所有字段正确")
        void should_setAllFieldsCorrectly_when_manualBuild() {
            // given
            List<String> data = Arrays.asList("x", "y", "z");

            // when
            PageResult<String> result = PageResult.of(data, 100L, 2, 10);

            // then
            assertEquals(3, result.getList().size(), "list大小必须与传入数据一致");
            assertEquals("x", result.getList().get(0), "list元素必须与传入数据一致");
            assertEquals(100L, result.getTotal(), "total必须与传入参数一致");
            assertEquals(2, result.getPageNum(), "pageNum必须与传入参数一致");
            assertEquals(10, result.getPageSize(), "pageSize必须与传入参数一致");
            assertEquals(10, result.getPages(), "pages必须正确计算(100/10=10)");
        }

        @Test
        @DisplayName("of - pages计算 - 不能整除时向上取整")
        void should_roundUpPages_when_notDivisible() {
            // given
            List<String> data = Arrays.asList("a");

            // when
            PageResult<String> result = PageResult.of(data, 11L, 1, 10);

            // then
            assertEquals(2, result.getPages(), "11条/10每页=2页(向上取整)");
        }

        @Test
        @DisplayName("of - pages计算 - 恰好整除")
        void should_calculateExactPages_when_divisible() {
            // given
            List<String> data = Arrays.asList("a");

            // when
            PageResult<String> result = PageResult.of(data, 20L, 1, 10);

            // then
            assertEquals(2, result.getPages(), "20条/10每页=2页(恰好整除)");
        }

        @Test
        @DisplayName("of - pages计算 - total为0时pages为0")
        void should_returnZeroPages_when_totalIsZero() {
            // given
            List<String> data = Collections.emptyList();

            // when
            PageResult<String> result = PageResult.of(data, 0L, 1, 10);

            // then
            assertEquals(0, result.getPages(), "total为0时pages必须为0");
        }

        @Test
        @DisplayName("of - pages计算 - pageSize为null时pages为0")
        void should_returnZeroPages_when_pageSizeIsNull() {
            // given
            List<String> data = Collections.emptyList();

            // when
            PageResult<String> result = PageResult.of(data, 10L, 1, null);

            // then
            assertEquals(0, result.getPages(), "pageSize为null时pages必须为0");
        }

        @Test
        @DisplayName("of - total为null时按0处理")
        void should_treatNullTotalAsZero_when_totalIsNull() {
            // given
            List<String> data = Collections.emptyList();

            // when
            PageResult<String> result = PageResult.of(data, null, 1, 10);

            // then
            assertNull(result.getTotal(), "total字段保留null值");
            assertEquals(0, result.getPages(), "total为null时pages按0计算");
        }

        @Test
        @DisplayName("of - 1条记录/10每页 = 1页")
        void should_returnOnePage_when_singleRecord() {
            // given
            List<String> data = List.of("only-one");

            // when
            PageResult<String> result = PageResult.of(data, 1L, 1, 10);

            // then
            assertEquals(1, result.getPages(), "1条/10每页=1页");
        }
    }

    // ========== empty() 空分页 ==========

    @Nested
    @DisplayName("empty() 空分页测试")
    class EmptyTests {

        @Test
        @DisplayName("empty() - 返回空列表且total=0")
        void should_returnEmptyListAndZeroTotal_when_empty() {
            // given - 无

            // when
            PageResult<String> result = PageResult.empty();

            // then
            assertNotNull(result.getList(), "list不得为null");
            assertTrue(result.getList().isEmpty(), "list必须为空集合");
            assertEquals(0L, result.getTotal(), "total必须为0");
        }

        @Test
        @DisplayName("empty() - pageNum默认为1, pageSize默认为0")
        void should_returnDefaultPageNumAndZeroPageSize_when_empty() {
            // given - 无

            // when
            PageResult<Object> result = PageResult.empty();

            // then
            assertEquals(1, result.getPageNum(), "empty()的pageNum必须为1");
            assertEquals(0, result.getPageSize(), "empty()的pageSize必须为0");
        }

        @Test
        @DisplayName("empty() - pages为0")
        void should_returnZeroPages_when_empty() {
            // given - 无

            // when
            PageResult<Object> result = PageResult.empty();

            // then
            assertEquals(0, result.getPages(), "empty()的pages必须为0");
        }

        @Test
        @DisplayName("empty() - 多次调用返回独立实例")
        void should_returnIndependentInstances_when_calledMultipleTimes() {
            // when
            PageResult<String> first = PageResult.empty();
            PageResult<String> second = PageResult.empty();

            // then
            assertNotSame(first, second, "每次调用empty()必须返回不同实例");
            assertEquals(first.getList(), second.getList(), "两个空分页的list必须相等");
        }
    }

    // ========== 字段Getter/Setter ==========

    @Nested
    @DisplayName("字段Getter/Setter测试")
    class FieldTests {

        @Test
        @DisplayName("无参构造 - 所有字段默认为null")
        void should_allFieldsBeNull_when_noArgConstructor() {
            // given - 无

            // when
            PageResult<String> result = new PageResult<>();

            // then
            assertNull(result.getList(), "无参构造list必须为null");
            assertNull(result.getTotal(), "无参构造total必须为null");
            assertNull(result.getPageNum(), "无参构造pageNum必须为null");
            assertNull(result.getPageSize(), "无参构造pageSize必须为null");
            assertNull(result.getPages(), "无参构造pages必须为null");
        }

        @Test
        @DisplayName("全参构造 - 所有字段正确赋值")
        void should_setAllFields_when_allArgConstructor() {
            // given
            List<String> data = Arrays.asList("a", "b");

            // when
            PageResult<String> result = new PageResult<>(data, 50L, 3, 20, 3);

            // then
            assertEquals(2, result.getList().size(), "list大小必须正确");
            assertEquals(50L, result.getTotal(), "total必须正确");
            assertEquals(3, result.getPageNum(), "pageNum必须正确");
            assertEquals(20, result.getPageSize(), "pageSize必须正确");
            assertEquals(3, result.getPages(), "pages必须正确");
        }

        @Test
        @DisplayName("Setter/Getter - 可修改和读取所有字段")
        void should_setAndGetAllFields_when_usingSetterGetter() {
            // given
            PageResult<String> result = new PageResult<>();

            // when
            result.setList(Arrays.asList("x"));
            result.setTotal(99L);
            result.setPageNum(5);
            result.setPageSize(15);
            result.setPages(7);

            // then
            assertEquals(1, result.getList().size(), "list setter/getter必须正确");
            assertEquals(99L, result.getTotal(), "total setter/getter必须正确");
            assertEquals(5, result.getPageNum(), "pageNum setter/getter必须正确");
            assertEquals(15, result.getPageSize(), "pageSize setter/getter必须正确");
            assertEquals(7, result.getPages(), "pages setter/getter必须正确");
        }
    }

    // ========== JSON序列化 ==========

    @Nested
    @DisplayName("JSON序列化测试")
    class JsonSerializationTests {

        @Test
        @DisplayName("@JsonInclude(NON_NULL) - list为null时JSON不包含list字段")
        void should_excludeNullListField_when_serializedToJson() throws Exception {
            // given
            PageResult<String> result = new PageResult<>();
            result.setTotal(10L);
            result.setPageNum(1);
            result.setPageSize(20);
            result.setPages(1);

            // when
            String json = objectMapper.writeValueAsString(result);
            JsonNode node = objectMapper.readTree(json);

            // then
            assertFalse(node.has("list"), "list为null时JSON不得包含list字段(@JsonInclude(NON_NULL))");
            assertTrue(node.has("total"), "total非null时JSON必须包含total字段");
        }

        @Test
        @DisplayName("list非null时JSON包含list字段")
        void should_includeListField_when_listNotNull() throws Exception {
            // given
            PageResult<String> result = PageResult.of(
                    Arrays.asList("a", "b"), 2L, 1, 10);

            // when
            String json = objectMapper.writeValueAsString(result);
            JsonNode node = objectMapper.readTree(json);

            // then
            assertTrue(node.has("list"), "list非null时JSON必须包含list字段");
            assertEquals(2, node.get("list").size(), "list数组大小必须正确");
            assertEquals("a", node.get("list").get(0).asText(), "list元素值必须正确");
        }

        @Test
        @DisplayName("@JsonInclude 注解存在于PageResult类上")
        void should_haveJsonIncludeAnnotation_when_checkClass() {
            // given - 无

            // when
            JsonInclude annotation = PageResult.class.getAnnotation(JsonInclude.class);

            // then
            assertNotNull(annotation, "PageResult类必须标注@JsonInclude注解");
            assertEquals(JsonInclude.Include.NON_NULL, annotation.value(),
                    "@JsonInclude必须配置为NON_NULL");
        }

        @Test
        @DisplayName("JSON序列化完整性 - 所有非null字段均正确序列化")
        void should_serializeAllNonNullFields_when_toJson() throws Exception {
            // given
            PageResult<Integer> result = PageResult.of(
                    Arrays.asList(1, 2, 3), 30L, 2, 10);

            // when
            String json = objectMapper.writeValueAsString(result);
            JsonNode node = objectMapper.readTree(json);

            // then
            assertEquals(3, node.get("list").size(), "JSON中list大小必须正确");
            assertEquals(30, node.get("total").asLong(), "JSON中total必须正确");
            assertEquals(2, node.get("pageNum").asInt(), "JSON中pageNum必须正确");
            assertEquals(10, node.get("pageSize").asInt(), "JSON中pageSize必须正确");
            assertEquals(3, node.get("pages").asInt(), "JSON中pages必须正确");
        }

        @Test
        @DisplayName("empty()的JSON - 所有字段均包含(无null)")
        void should_includeAllFields_when_emptyResultToJson() throws Exception {
            // given
            PageResult<String> result = PageResult.empty();

            // when
            String json = objectMapper.writeValueAsString(result);
            JsonNode node = objectMapper.readTree(json);

            // then
            assertTrue(node.has("list"), "empty()的JSON必须包含list字段");
            assertTrue(node.has("total"), "empty()的JSON必须包含total字段");
            assertTrue(node.has("pageNum"), "empty()的JSON必须包含pageNum字段");
            assertTrue(node.has("pageSize"), "empty()的JSON必须包含pageSize字段");
            assertTrue(node.has("pages"), "empty()的JSON必须包含pages字段");
            assertEquals(0, node.get("list").size(), "empty()的list必须为空数组");
            assertEquals(0, node.get("total").asLong(), "empty()的total必须为0");
        }
    }

    // ========== Serializable ==========

    @Nested
    @DisplayName("序列化测试")
    class SerializableTests {

        @Test
        @DisplayName("PageResult实现Serializable接口")
        void should_implementSerializable_when_checkClass() {
            // then
            assertTrue(Serializable.class.isAssignableFrom(PageResult.class),
                    "PageResult必须实现Serializable接口");
        }

        @Test
        @DisplayName("PageResult对象可序列化和反序列化")
        void should_serializeAndDeserialize_when_roundTrip() throws Exception {
            // given
            PageResult<String> original = PageResult.of(
                    Arrays.asList("a", "b", "c"), 100L, 2, 10);

            // when
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(original);
            }
            byte[] bytes = baos.toByteArray();
            PageResult<?> deserialized;
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                deserialized = (PageResult<?>) ois.readObject();
            }

            // then
            assertEquals(original.getList(), deserialized.getList(),
                    "反序列化后list必须一致");
            assertEquals(original.getTotal(), deserialized.getTotal(),
                    "反序列化后total必须一致");
            assertEquals(original.getPageNum(), deserialized.getPageNum(),
                    "反序列化后pageNum必须一致");
            assertEquals(original.getPageSize(), deserialized.getPageSize(),
                    "反序列化后pageSize必须一致");
            assertEquals(original.getPages(), deserialized.getPages(),
                    "反序列化后pages必须一致");
        }

        @Test
        @DisplayName("空PageResult对象可序列化和反序列化")
        void should_serializeAndDeserialize_when_emptyResult() throws Exception {
            // given
            PageResult<Object> original = PageResult.empty();

            // when
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(original);
            }
            PageResult<?> deserialized;
            try (ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(baos.toByteArray()))) {
                deserialized = (PageResult<?>) ois.readObject();
            }

            // then
            assertEquals(original.getList(), deserialized.getList(),
                    "反序列化后list必须一致");
            assertEquals(original.getTotal(), deserialized.getTotal(),
                    "反序列化后total必须一致");
        }
    }

    // ========== 类结构验证 ==========

    @Nested
    @DisplayName("类结构验证")
    class ClassStructureTests {

        @Test
        @DisplayName("PageResult类必须为泛型类")
        void should_beGeneric_when_checkClass() {
            // then
            assertTrue(PageResult.class.getTypeParameters().length > 0,
                    "PageResult必须为泛型类");
        }

        @Test
        @DisplayName("PageResult必须包含list/total/pageNum/pageSize/pages五个字段")
        void should_haveRequiredFields_when_checkClass() throws Exception {
            // then
            assertNotNull(PageResult.class.getDeclaredField("list"),
                    "PageResult必须包含list字段");
            assertNotNull(PageResult.class.getDeclaredField("total"),
                    "PageResult必须包含total字段");
            assertNotNull(PageResult.class.getDeclaredField("pageNum"),
                    "PageResult必须包含pageNum字段");
            assertNotNull(PageResult.class.getDeclaredField("pageSize"),
                    "PageResult必须包含pageSize字段");
            assertNotNull(PageResult.class.getDeclaredField("pages"),
                    "PageResult必须包含pages字段");
        }

        @Test
        @DisplayName("PageResult必须包含of(IPage)静态工厂方法")
        void should_haveOfIPageMethod_when_checkClass() throws Exception {
            // then
            assertNotNull(PageResult.class.getDeclaredMethod("of", IPage.class),
                    "PageResult必须包含of(IPage)静态方法");
        }

        @Test
        @DisplayName("PageResult必须包含empty()静态工厂方法")
        void should_haveEmptyMethod_when_checkClass() throws Exception {
            // then
            assertNotNull(PageResult.class.getDeclaredMethod("empty"),
                    "PageResult必须包含empty()静态方法");
        }

        @Test
        @DisplayName("PageResult类源码不得硬编码密码/密钥")
        void should_notContainSecrets_when_checkFields() throws Exception {
            // given - 无

            // when
            java.lang.reflect.Field[] fields = PageResult.class.getDeclaredFields();

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

    // ========== 与RT组合使用 ==========

    @Nested
    @DisplayName("与RT组合使用测试")
    class RTIntegrationTests {

        @Test
        @DisplayName("RT<PageResult<T>> - 成功响应包装分页数据")
        void should_wrapPageResultInRT_when_okWithPageResult() {
            // given
            PageResult<String> pageResult = PageResult.of(
                    Arrays.asList("a", "b"), 50L, 1, 10);

            // when
            RT<PageResult<String>> response = RT.ok(pageResult);

            // then
            assertEquals(0, response.getCode(), "RT包装后code必须为200");
            assertNotNull(response.getData(), "RT data不得为null");
            assertEquals(50L, response.getData().getTotal(),
                    "通过RT获取的PageResult total必须正确");
            assertEquals(2, response.getData().getList().size(),
                    "通过RT获取的PageResult list大小必须正确");
        }

        @Test
        @DisplayName("RT<PageResult<T>> - 空分页包装")
        void should_wrapEmptyPageResultInRT_when_okWithEmpty() {
            // given
            PageResult<Object> emptyPage = PageResult.empty();

            // when
            RT<PageResult<Object>> response = RT.ok(emptyPage);

            // then
            assertEquals(0, response.getCode(), "RT包装空分页code必须为200");
            assertTrue(response.getData().getList().isEmpty(),
                    "通过RT获取的PageResult list必须为空");
        }
    }
}
