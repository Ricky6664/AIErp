package com.erp.system.dataview;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 查询条件解析器 - 解析前端JSON查询参数为SQL WHERE条件.
 *
 * <p>核心功能:
 * <ul>
 *   <li>支持操作符: eq / ne / gt / gte / lt / lte / like / between / in</li>
 *   <li>LIKE自动转义 % 和 _ 特殊字符</li>
 *   <li>BETWEEN校验两端值类型一致</li>
 *   <li>IN限制最大100个元素</li>
 *   <li>字段名PostgreSQL双引号转义防注入</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@Component
public class DataViewQueryParser {

    private static final int MAX_IN_ELEMENTS = 100;

    private static final Set<String> VALID_OPERATORS = Set.of(
            "eq", "ne", "gt", "gte", "lt", "lte", "like", "between", "in"
    );

    // ==================== 查询条件模型 ====================

    /**
     * 前端传入的单条查询条件.
     *
     * <p>JSON格式: {"field": "name", "operator": "like", "value": "test"}</p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryCondition {
        private String field;
        private String operator;
        private Object value;
    }

    // ==================== 主方法 ====================

    /**
     * 将查询条件列表解析为SQL WHERE子句(不含WHERE关键字).
     *
     * <p>所有条件以AND连接. 若conditions为null或空则返回 "1=1".</p>
     *
     * @param conditions 查询条件列表
     * @return SQL条件表达式
     */
    public String parseConditions(List<QueryCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return "1=1";
        }
        List<String> parts = new ArrayList<>();
        for (QueryCondition cond : conditions) {
            String part = parseOne(cond);
            if (part != null) {
                parts.add(part);
            }
        }
        if (parts.isEmpty()) {
            return "1=1";
        }
        return String.join(" AND ", parts);
    }

    /**
     * 解析单条查询条件.
     *
     * @param condition 查询条件
     * @return SQL条件表达式, 若value为null则返回null
     */
    public String parseOne(QueryCondition condition) {
        if (condition == null) {
            return null;
        }
        String field = condition.getField();
        String operator = condition.getOperator();
        Object value = condition.getValue();

        if (field == null || field.isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "查询字段名不能为空");
        }
        if (value == null) {
            return null;
        }
        if (operator == null || operator.isBlank()) {
            operator = "eq";
        }
        operator = operator.toLowerCase();
        if (!VALID_OPERATORS.contains(operator)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID,
                    "不支持的操作符: " + operator + ", 有效值: " + VALID_OPERATORS);
        }

        String escapedField = escapeFieldName(field.trim());

        switch (operator) {
            case "eq":
                return buildEq(escapedField, value);
            case "ne":
                return buildNe(escapedField, value);
            case "gt":
                return buildGt(escapedField, value);
            case "gte":
                return buildGte(escapedField, value);
            case "lt":
                return buildLt(escapedField, value);
            case "lte":
                return buildLte(escapedField, value);
            case "like":
                return buildLike(escapedField, value);
            case "between":
                return buildBetween(escapedField, value);
            case "in":
                return buildIn(escapedField, value);
            default:
                throw new BusinessException(ErrorCode.PARAM_INVALID,
                        "不支持的操作符: " + operator);
        }
    }

    // ==================== 操作符实现 ====================

    private String buildEq(String field, Object value) {
        return field + " = " + toSqlLiteral(value);
    }

    private String buildNe(String field, Object value) {
        return field + " != " + toSqlLiteral(value);
    }

    private String buildGt(String field, Object value) {
        return field + " > " + toSqlLiteral(value);
    }

    private String buildGte(String field, Object value) {
        return field + " >= " + toSqlLiteral(value);
    }

    private String buildLt(String field, Object value) {
        return field + " < " + toSqlLiteral(value);
    }

    private String buildLte(String field, Object value) {
        return field + " <= " + toSqlLiteral(value);
    }

    /**
     * 构建LIKE条件.
     *
     * <p>自动转义输入值中的 % 和 _ 通配符, 然后在两端追加 % 实现模糊匹配.</p>
     */
    private String buildLike(String field, Object value) {
        String strVal = value.toString();
        if (strVal.isBlank()) {
            return null;
        }
        String escaped = escapeLikeChars(strVal);
        return field + " LIKE '%" + escaped + "%'";
    }

    /**
     * 构建BETWEEN条件.
     *
     * <p>value必须包含两个元素, 类型必须一致(同为Number或同为String).
     * 支持List/Collection和逗号分隔的字符串两种格式.</p>
     */
    private String buildBetween(String field, Object value) {
        Object[] pair = extractPair(value);
        if (pair == null) {
            throw new BusinessException(ErrorCode.PARAM_FORMAT_ERROR,
                    "BETWEEN操作符需要两个值: [v1, v2] 或 \"v1,v2\"");
        }
        validateBetweenType(pair[0], pair[1]);
        return field + " BETWEEN " + toSqlLiteral(pair[0]) + " AND " + toSqlLiteral(pair[1]);
    }

    /**
     * 构建IN条件.
     *
     * <p>限制最多100个元素. 支持List/Collection和数组两种格式.</p>
     */
    private String buildIn(String field, Object value) {
        List<?> elements = extractList(value);
        if (elements == null || elements.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "IN操作符的元素列表不能为空");
        }
        if (elements.size() > MAX_IN_ELEMENTS) {
            throw new BusinessException(ErrorCode.PARAM_RANGE_ERROR,
                    "IN操作符最多支持" + MAX_IN_ELEMENTS + "个元素, 当前: " + elements.size());
        }
        StringBuilder sb = new StringBuilder();
        sb.append(field).append(" IN (");
        boolean first = true;
        for (Object elem : elements) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(toSqlLiteral(elem));
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }

    // ==================== 值提取与校验 ====================

    /**
     * 提取两个值用于BETWEEN.
     *
     * <p>支持格式:
     * <ul>
     *   <li>Collection (size=2)</li>
     *   <li>数组 (length=2)</li>
     *   <li>逗号分隔字符串 "v1,v2"</li>
     * </ul>
     * </p>
     */
    private Object[] extractPair(Object value) {
        if (value instanceof Collection<?>) {
            Collection<?> coll = (Collection<?>) value;
            if (coll.size() != 2) {
                return null;
            }
            return coll.toArray();
        }
        if (value.getClass().isArray()) {
            Object[] arr = (Object[]) value;
            if (arr.length != 2) {
                return null;
            }
            return arr;
        }
        String strVal = value.toString().trim();
        if (strVal.isBlank()) {
            return null;
        }
        String[] parts = strVal.split(",", 2);
        if (parts.length < 2) {
            return null;
        }
        return new Object[]{parts[0].trim(), parts[1].trim()};
    }

    /**
     * 提取列表用于IN.
     *
     * <p>支持格式: Collection, 数组, 逗号分隔字符串.</p>
     */
    private List<?> extractList(Object value) {
        if (value instanceof Collection<?>) {
            return new ArrayList<>((Collection<?>) value);
        }
        if (value.getClass().isArray()) {
            return List.of((Object[]) value);
        }
        String strVal = value.toString().trim();
        if (strVal.isBlank()) {
            return List.of();
        }
        String[] parts = strVal.split(",");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * 校验BETWEEN两端值类型一致.
     */
    private void validateBetweenType(Object v1, Object v2) {
        if (v1 == null || v2 == null) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "BETWEEN两端值不能为空");
        }
        boolean v1IsNum = v1 instanceof Number;
        boolean v2IsNum = v2 instanceof Number;
        if (v1IsNum != v2IsNum) {
            throw new BusinessException(ErrorCode.PARAM_TYPE_ERROR,
                    "BETWEEN两端值类型不一致: " + v1.getClass().getSimpleName()
                            + " vs " + v2.getClass().getSimpleName());
        }
    }

    // ==================== 转义工具 ====================

    /**
     * PostgreSQL双引号转义字段名, 防止SQL注入.
     */
    private String escapeFieldName(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }
        return "\"" + name.replace("\"", "\"\"") + "\"";
    }

    /**
     * 转义LIKE通配符 % 和 _.
     *
     * <p>用户输入中的 % 和 _ 应被视为普通字符而非LIKE通配符.
     * 使用PostgreSQL默认转义符 \ 进行转义.</p>
     */
    private String escapeLikeChars(String value) {
        if (value == null) {
            return null;
        }
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    /**
     * 将Java值转为SQL字面量.
     *
     * <p>Number → 直接toString; String/其他 → 单引号包裹并转义单引号.</p>
     */
    private String toSqlLiteral(Object value) {
        if (value instanceof Number) {
            return value.toString();
        }
        String escaped = value.toString().replace("'", "''");
        return "'" + escaped + "'";
    }
}
