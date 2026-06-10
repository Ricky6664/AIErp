package com.erp.system.dataview;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DataViewQueryParser 验证测试")
class DataViewQueryParserVerificationTest {

    private final DataViewQueryParser parser = new DataViewQueryParser();

    // ==================== 操作符测试 ====================

    @Nested
    @DisplayName("操作符覆盖")
    class OperatorTests {

        @Test
        @DisplayName("eq - 精确匹配")
        void eqOperator() {
            DataViewQueryParser.QueryCondition cond = cond("name", "eq", "test");
            String result = parser.parseOne(cond);
            assertEquals("\"name\" = 'test'", result);
        }

        @Test
        @DisplayName("eq - 数字值不加引号")
        void eqWithNumber() {
            DataViewQueryParser.QueryCondition cond = cond("age", "eq", 25);
            String result = parser.parseOne(cond);
            assertEquals("\"age\" = 25", result);
        }

        @Test
        @DisplayName("ne - 不等于")
        void neOperator() {
            DataViewQueryParser.QueryCondition cond = cond("status", "ne", "deleted");
            String result = parser.parseOne(cond);
            assertEquals("\"status\" != 'deleted'", result);
        }

        @Test
        @DisplayName("gt - 大于")
        void gtOperator() {
            DataViewQueryParser.QueryCondition cond = cond("amount", "gt", 100);
            String result = parser.parseOne(cond);
            assertEquals("\"amount\" > 100", result);
        }

        @Test
        @DisplayName("gte - 大于等于")
        void gteOperator() {
            DataViewQueryParser.QueryCondition cond = cond("amount", "gte", 100);
            String result = parser.parseOne(cond);
            assertEquals("\"amount\" >= 100", result);
        }

        @Test
        @DisplayName("lt - 小于")
        void ltOperator() {
            DataViewQueryParser.QueryCondition cond = cond("amount", "lt", 500);
            String result = parser.parseOne(cond);
            assertEquals("\"amount\" < 500", result);
        }

        @Test
        @DisplayName("lte - 小于等于")
        void lteOperator() {
            DataViewQueryParser.QueryCondition cond = cond("amount", "lte", 500);
            String result = parser.parseOne(cond);
            assertEquals("\"amount\" <= 500", result);
        }

        @Test
        @DisplayName("like - 模糊匹配并自动追加%")
        void likeOperator() {
            DataViewQueryParser.QueryCondition cond = cond("name", "like", "test");
            String result = parser.parseOne(cond);
            assertEquals("\"name\" LIKE '%test%'", result);
        }

        @Test
        @DisplayName("between - 范围匹配")
        void betweenOperator() {
            DataViewQueryParser.QueryCondition cond = cond("price", "between", "10,50");
            String result = parser.parseOne(cond);
            assertEquals("\"price\" BETWEEN '10' AND '50'", result);
        }

        @Test
        @DisplayName("between - List格式")
        void betweenWithList() {
            DataViewQueryParser.QueryCondition cond = cond("price", "between", List.of(10, 50));
            String result = parser.parseOne(cond);
            assertEquals("\"price\" BETWEEN 10 AND 50", result);
        }

        @Test
        @DisplayName("in - 多值匹配")
        void inOperator() {
            DataViewQueryParser.QueryCondition cond = cond("status", "in", "A,B,C");
            String result = parser.parseOne(cond);
            assertEquals("\"status\" IN ('A', 'B', 'C')", result);
        }

        @Test
        @DisplayName("in - List格式")
        void inWithList() {
            DataViewQueryParser.QueryCondition cond = cond("id", "in", List.of(1, 2, 3));
            String result = parser.parseOne(cond);
            assertEquals("\"id\" IN (1, 2, 3)", result);
        }

        @Test
        @DisplayName("默认操作符为eq")
        void defaultOperator() {
            DataViewQueryParser.QueryCondition cond = cond("name", null, "test");
            String result = parser.parseOne(cond);
            assertEquals("\"name\" = 'test'", result);
        }
    }

    // ==================== LIKE转义测试 ====================

    @Nested
    @DisplayName("LIKE特殊字符转义")
    class LikeEscapeTests {

        @Test
        @DisplayName("转义百分号%")
        void escapePercent() {
            DataViewQueryParser.QueryCondition cond = cond("name", "like", "100%");
            String result = parser.parseOne(cond);
            assertTrue(result.contains("\\%"));
            assertFalse(result.contains("%100%") && result.contains("'100%'"));
        }

        @Test
        @DisplayName("转义下划线_")
        void escapeUnderscore() {
            DataViewQueryParser.QueryCondition cond = cond("code", "like", "A_B");
            String result = parser.parseOne(cond);
            assertTrue(result.contains("\\_"));
        }

        @Test
        @DisplayName("转义反斜杠自身")
        void escapeBackslash() {
            DataViewQueryParser.QueryCondition cond = cond("path", "like", "C:\\dir");
            String result = parser.parseOne(cond);
            assertTrue(result.contains("\\\\dir"));
        }
    }

    // ==================== BETWEEN类型校验测试 ====================

    @Nested
    @DisplayName("BETWEEN类型校验")
    class BetweenTypeTests {

        @Test
        @DisplayName("两端类型一致(同为数字) - 通过")
        void betweenBothNumber() {
            DataViewQueryParser.QueryCondition cond = cond("price", "between", List.of(10, 50));
            String result = parser.parseOne(cond);
            assertNotNull(result);
        }

        @Test
        @DisplayName("两端类型一致(同为字符串) - 通过")
        void betweenBothString() {
            DataViewQueryParser.QueryCondition cond = cond("date", "between", "2024-01-01,2024-12-31");
            String result = parser.parseOne(cond);
            assertNotNull(result);
        }

        @Test
        @DisplayName("两端类型不一致 - 拒绝")
        void betweenTypeMismatch() {
            DataViewQueryParser.QueryCondition cond = cond("price", "between", List.of(10, "fifty"));
            BusinessException ex = assertThrows(BusinessException.class, () -> parser.parseOne(cond));
            assertEquals(ErrorCode.PARAM_TYPE_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("BETWEEN值不足2个 - 拒绝")
        void betweenInsufficientValues() {
            DataViewQueryParser.QueryCondition cond = cond("price", "between", "10");
            BusinessException ex = assertThrows(BusinessException.class, () -> parser.parseOne(cond));
            assertEquals(ErrorCode.PARAM_FORMAT_ERROR.getCode(), ex.getCode());
        }
    }

    // ==================== IN限制测试 ====================

    @Nested
    @DisplayName("IN元素数量限制")
    class InLimitTests {

        @Test
        @DisplayName("≤100个元素 - 通过")
        void inWithinLimit() {
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                ids.add(i + 1);
            }
            DataViewQueryParser.QueryCondition cond = cond("id", "in", ids);
            String result = parser.parseOne(cond);
            assertNotNull(result);
            assertTrue(result.startsWith("\"id\" IN ("));
        }

        @Test
        @DisplayName(">100个元素 - 拒绝")
        void inExceedsLimit() {
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < 101; i++) {
                ids.add(i + 1);
            }
            DataViewQueryParser.QueryCondition cond = cond("id", "in", ids);
            BusinessException ex = assertThrows(BusinessException.class, () -> parser.parseOne(cond));
            assertEquals(ErrorCode.PARAM_RANGE_ERROR.getCode(), ex.getCode());
        }
    }

    // ==================== 字段名转义测试 ====================

    @Nested
    @DisplayName("字段名双引号转义防注入")
    class FieldNameEscapingTests {

        @Test
        @DisplayName("正常字段名使用双引号包裹")
        void normalFieldEscaped() {
            DataViewQueryParser.QueryCondition cond = cond("username", "eq", "admin");
            String result = parser.parseOne(cond);
            assertTrue(result.startsWith("\"username\""));
        }

        @Test
        @DisplayName("字段名包含双引号 - 双双引号转义")
        void fieldWithDoubleQuote() {
            DataViewQueryParser.QueryCondition cond = cond("some\"field", "eq", "value");
            String result = parser.parseOne(cond);
            assertTrue(result.contains("\"\""));
        }

        @Test
        @DisplayName("单引号在值中 - 双单引号转义")
        void valueWithSingleQuote() {
            DataViewQueryParser.QueryCondition cond = cond("name", "eq", "O'Brien");
            String result = parser.parseOne(cond);
            assertTrue(result.contains("O''Brien"));
        }
    }

    // ==================== 多条件组合测试 ====================

    @Nested
    @DisplayName("多条件组合解析")
    class MultiConditionTests {

        @Test
        @DisplayName("多个条件以AND连接")
        void multipleConditions() {
            List<DataViewQueryParser.QueryCondition> conditions = new ArrayList<>();
            conditions.add(cond("name", "eq", "test"));
            conditions.add(cond("status", "eq", "active"));
            conditions.add(cond("age", "gt", 18));

            String result = parser.parseConditions(conditions);
            assertTrue(result.contains(" AND "));
            assertTrue(result.contains("\"name\" = 'test'"));
            assertTrue(result.contains("\"status\" = 'active'"));
            assertTrue(result.contains("\"age\" > 18"));
        }

        @Test
        @DisplayName("空条件列表返回1=1")
        void emptyConditions() {
            assertEquals("1=1", parser.parseConditions(null));
            assertEquals("1=1", parser.parseConditions(List.of()));
        }

        @Test
        @DisplayName("value为null的条件跳过")
        void nullValueSkipped() {
            List<DataViewQueryParser.QueryCondition> conditions = new ArrayList<>();
            conditions.add(cond("name", "eq", "test"));
            conditions.add(cond("email", "eq", null));
            conditions.add(cond("status", "eq", "active"));

            String result = parser.parseConditions(conditions);
            assertTrue(result.contains("\"name\" = 'test'"));
            assertTrue(result.contains("\"status\" = 'active'"));
            assertFalse(result.contains("email"));
        }
    }

    // ==================== 异常处理测试 ====================

    @Nested
    @DisplayName("异常处理")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("无效操作符 - 拒绝")
        void invalidOperator() {
            DataViewQueryParser.QueryCondition cond = cond("name", "invalid_op", "test");
            BusinessException ex = assertThrows(BusinessException.class, () -> parser.parseOne(cond));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("字段名为空 - 拒绝")
        void emptyField() {
            DataViewQueryParser.QueryCondition cond = cond("", "eq", "test");
            BusinessException ex = assertThrows(BusinessException.class, () -> parser.parseOne(cond));
            assertEquals(ErrorCode.PARAM_MISSING.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("字段名为null - 拒绝")
        void nullField() {
            DataViewQueryParser.QueryCondition cond = cond(null, "eq", "test");
            BusinessException ex = assertThrows(BusinessException.class, () -> parser.parseOne(cond));
            assertEquals(ErrorCode.PARAM_MISSING.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("边界情况")
    class EdgeCaseTests {

        @Test
        @DisplayName("操作符大小写不敏感")
        void operatorCaseInsensitive() {
            DataViewQueryParser.QueryCondition condUpper = cond("name", "LIKE", "test");
            assertEquals(parser.parseOne(cond("name", "like", "test")), parser.parseOne(condUpper));
        }

        @Test
        @DisplayName("in空列表 - 拒绝")
        void inWithEmptyList() {
            DataViewQueryParser.QueryCondition cond = cond("id", "in", List.of());
            BusinessException ex = assertThrows(BusinessException.class, () -> parser.parseOne(cond));
            assertEquals(ErrorCode.PARAM_MISSING.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("like空值返回null")
        void likeWithBlankValue() {
            DataViewQueryParser.QueryCondition cond = cond("name", "like", "   ");
            assertNull(parser.parseOne(cond));
        }
    }

    // ==================== 辅助方法 ====================

    private static DataViewQueryParser.QueryCondition cond(String field, String operator, Object value) {
        DataViewQueryParser.QueryCondition c = new DataViewQueryParser.QueryCondition();
        c.setField(field);
        c.setOperator(operator);
        c.setValue(value);
        return c;
    }
}
