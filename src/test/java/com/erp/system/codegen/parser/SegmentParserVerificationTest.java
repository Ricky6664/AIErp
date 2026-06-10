package com.erp.system.codegen.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("段解析器验证测试")
class SegmentParserVerificationTest {

    @Nested
    @DisplayName("FixedSegmentParser - 固定段解析器")
    class FixedSegmentParserTest {

        private final FixedSegmentParser parser = new FixedSegmentParser();

        @Test
        @DisplayName("返回配置的固定字符串")
        void shouldReturnConfiguredValue() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .segmentValue("PO")
                    .build();

            String result = parser.parse(ctx);

            assertEquals("PO", result);
        }

        @Test
        @DisplayName("segmentValue为null时返回空字符串")
        void shouldReturnEmptyWhenNull() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .build();

            String result = parser.parse(ctx);

            assertEquals("", result);
        }

        @Test
        @DisplayName("getSegmentType返回1")
        void shouldReturnType1() {
            assertEquals(1, parser.getSegmentType());
        }
    }

    @Nested
    @DisplayName("DateSegmentParser - 日期段解析器")
    class DateSegmentParserTest {

        private final DateSegmentParser parser = new DateSegmentParser();

        @Test
        @DisplayName("默认格式yyyyMMdd")
        void shouldUseDefaultFormat() {
            SegmentParseContext ctx = SegmentParseContext.builder().build();

            String result = parser.parse(ctx);

            String expected = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("yyyyMMdd格式正确输出8位日期")
        void shouldFormatAsyyyyMMdd() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .segmentFormat("yyyyMMdd")
                    .build();

            String result = parser.parse(ctx);

            assertEquals(8, result.length());
            assertTrue(result.matches("\\d{8}"));
        }

        @Test
        @DisplayName("yyMM格式正确输出4位")
        void shouldFormatAsyyMM() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .segmentFormat("yyMM")
                    .build();

            String result = parser.parse(ctx);

            assertEquals(4, result.length());
            assertTrue(result.matches("\\d{4}"));
        }

        @Test
        @DisplayName("yyyy-MM-dd格式输出带分隔符")
        void shouldFormatWithSeparator() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .segmentFormat("yyyy-MM-dd")
                    .build();

            String result = parser.parse(ctx);

            assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}"));
        }

        @Test
        @DisplayName("MMdd格式正确输出4位月日")
        void shouldFormatAsMMdd() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .segmentFormat("MMdd")
                    .build();

            String result = parser.parse(ctx);

            assertEquals(4, result.length());
        }

        @Test
        @DisplayName("format为空白时使用默认格式")
        void shouldFallbackToDefaultWhenBlank() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .segmentFormat("   ")
                    .build();

            String result = parser.parse(ctx);

            String expected = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("formatter缓存复用")
        void shouldCacheFormatters() {
            SegmentParseContext ctx1 = SegmentParseContext.builder().segmentFormat("yyyyMMdd").build();
            SegmentParseContext ctx2 = SegmentParseContext.builder().segmentFormat("yyyyMMdd").build();

            String r1 = parser.parse(ctx1);
            String r2 = parser.parse(ctx2);

            assertEquals(r1, r2);
        }

        @Test
        @DisplayName("getSegmentType返回2")
        void shouldReturnType2() {
            assertEquals(2, parser.getSegmentType());
        }
    }

    @Nested
    @DisplayName("VariableSegmentParser - 自定义变量段解析器")
    class VariableSegmentParserTest {

        private final VariableSegmentParser parser = new VariableSegmentParser();

        @Test
        @DisplayName("从变量Map中读取对应key的值")
        void shouldReadFromVariables() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .segmentValue("deptCode")
                    .variables(Map.of("deptCode", "D001"))
                    .build();

            String result = parser.parse(ctx);

            assertEquals("D001", result);
        }

        @Test
        @DisplayName("key不存在时返回空字符串")
        void shouldReturnEmptyWhenKeyNotFound() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .segmentValue("unknownKey")
                    .variables(Map.of("deptCode", "D001"))
                    .build();

            String result = parser.parse(ctx);

            assertEquals("", result);
        }

        @Test
        @DisplayName("segmentValue为null时返回空字符串")
        void shouldReturnEmptyWhenSegmentValueNull() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .variables(Map.of("deptCode", "D001"))
                    .build();

            String result = parser.parse(ctx);

            assertEquals("", result);
        }

        @Test
        @DisplayName("segmentValue为空字符串时返回空字符串")
        void shouldReturnEmptyWhenSegmentValueEmpty() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .segmentValue("")
                    .variables(Map.of("deptCode", "D001"))
                    .build();

            String result = parser.parse(ctx);

            assertEquals("", result);
        }

        @Test
        @DisplayName("variables为null时返回空字符串")
        void shouldReturnEmptyWhenVariablesNull() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .segmentValue("deptCode")
                    .build();

            String result = parser.parse(ctx);

            assertEquals("", result);
        }

        @Test
        @DisplayName("多变量场景正确读取")
        void shouldReadCorrectVariableFromMultiple() {
            SegmentParseContext ctx = SegmentParseContext.builder()
                    .segmentValue("whCode")
                    .variables(Map.of("deptCode", "D001", "whCode", "WH01", "supplierCode", "S001"))
                    .build();

            String result = parser.parse(ctx);

            assertEquals("WH01", result);
        }

        @Test
        @DisplayName("getSegmentType返回4")
        void shouldReturnType4() {
            assertEquals(4, parser.getSegmentType());
        }
    }

    @Nested
    @DisplayName("SegmentParserFactory - 工厂路由")
    class SegmentParserFactoryTest {

        @Test
        @DisplayName("根据segmentType=1路由到FixedSegmentParser")
        void shouldRouteToFixedParser() {
            SegmentParserFactory factory = new SegmentParserFactory(
                    java.util.List.of(new FixedSegmentParser(), new DateSegmentParser(), new VariableSegmentParser()));

            SegmentParser parser = factory.getParser(1);

            assertInstanceOf(FixedSegmentParser.class, parser);
        }

        @Test
        @DisplayName("根据segmentType=2路由到DateSegmentParser")
        void shouldRouteToDateParser() {
            SegmentParserFactory factory = new SegmentParserFactory(
                    java.util.List.of(new FixedSegmentParser(), new DateSegmentParser(), new VariableSegmentParser()));

            SegmentParser parser = factory.getParser(2);

            assertInstanceOf(DateSegmentParser.class, parser);
        }

        @Test
        @DisplayName("根据segmentType=4路由到VariableSegmentParser")
        void shouldRouteToVariableParser() {
            SegmentParserFactory factory = new SegmentParserFactory(
                    java.util.List.of(new FixedSegmentParser(), new DateSegmentParser(), new VariableSegmentParser()));

            SegmentParser parser = factory.getParser(4);

            assertInstanceOf(VariableSegmentParser.class, parser);
        }

        @Test
        @DisplayName("未知segmentType抛出BusinessException")
        void shouldThrowWhenUnknownType() {
            SegmentParserFactory factory = new SegmentParserFactory(
                    java.util.List.of(new FixedSegmentParser()));

            assertThrows(com.erp.common.exception.BusinessException.class, () -> factory.getParser(99));
        }
    }
}
