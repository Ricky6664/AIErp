package com.erp.system.dataview;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysDataView;
import com.erp.system.entity.SysDataViewField;
import com.erp.system.mapper.SysDataViewFieldMapper;
import com.erp.system.mapper.SysDataViewMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * SQL动态构建器 - 根据数据视图配置动态生成SELECT查询语句.
 *
 * <p>核心功能:
 * <ul>
 *   <li>SQL白名单校验: 只允许SELECT, 禁止INSERT/UPDATE/DELETE/DROP/ALTER/TRUNCATE等</li>
 *   <li>动态WHERE: 根据字段search_type生成 =、LIKE、BETWEEN 条件</li>
 *   <li>字段名防注入: 使用PostgreSQL双引号转义</li>
 *   <li>表名元数据校验: 表名必须存在于sys_data_view配置中</li>
 *   <li>分页排序: pageSize最大100, 默认按create_time DESC</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@Component
public class DataViewSqlBuilder {

    private final SysDataViewMapper sysDataViewMapper;
    private final SysDataViewFieldMapper fieldMapper;

    private static final int MAX_PAGE_SIZE = 100;
    private static final String DEFAULT_SORT_FIELD = "create_time";
    private static final String DEFAULT_SORT_ORDER = "DESC";

    /** 禁止在动态SQL中出现的危险关键字 */
    private static final Set<String> FORBIDDEN_SQL_KEYWORDS = Set.of(
            "DROP", "DELETE", "UPDATE", "INSERT", "ALTER", "TRUNCATE", "CREATE",
            "EXEC", "EXECUTE", "MERGE", "GRANT", "REVOKE"
    );

    @Autowired
    public DataViewSqlBuilder(SysDataViewMapper sysDataViewMapper, SysDataViewFieldMapper fieldMapper) {
        this.sysDataViewMapper = sysDataViewMapper;
        this.fieldMapper = fieldMapper;
    }

    // ==================== SQL构建结果 ====================

    /**
     * SQL构建结果.
     */
    @Data
    public static class SqlBuildResult {
        private final String dataSql;
        private final String countSql;
        private final int pageNum;
        private final int pageSize;

        public SqlBuildResult(String dataSql, String countSql, int pageNum, int pageSize) {
            this.dataSql = dataSql;
            this.countSql = countSql;
            this.pageNum = pageNum;
            this.pageSize = pageSize;
        }
    }

    // ==================== 主方法 ====================

    /**
     * 根据视图ID和查询参数构建SELECT SQL.
     *
     * @param viewId      视图主键ID
     * @param queryParams 查询参数（含分页/排序/搜索条件）
     * @return SQL构建结果（dataSql + countSql + 分页信息）
     */
    public SqlBuildResult buildSelectSql(Long viewId, Map<String, Object> queryParams) {
        SysDataView view = sysDataViewMapper.selectById(viewId);
        if (view == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "视图不存在: " + viewId);
        }

        List<SysDataViewField> fields = fieldMapper.selectVisibleFields(viewId);
        if (fields == null || fields.isEmpty()) {
            fields = Collections.emptyList();
        }

        int page = getPageNum(queryParams);
        int size = Math.min(getPageSize(queryParams), MAX_PAGE_SIZE);
        String sortField = resolveSortField(queryParams, fields);
        String sortOrder = getSortOrder(queryParams);

        String selectClause = buildSelectClause(fields);
        String fromClause = buildFromClause(view);
        String whereClause = buildWhereClause(fields, queryParams, view.getSourceType());

        String dataSql = "SELECT " + selectClause
                + " FROM " + fromClause
                + whereClause
                + " ORDER BY " + escapeFieldName(sortField) + " " + sortOrder
                + " LIMIT " + size + " OFFSET " + ((page - 1) * size);

        validateSqlWhitelist(dataSql);

        String countSql = "SELECT COUNT(*) FROM " + fromClause + whereClause;

        log.debug("buildSelectSql dataSql: {}", dataSql);
        log.debug("buildSelectSql countSql: {}", countSql);

        return new SqlBuildResult(dataSql, countSql, page, size);
    }

    // ==================== SQL子句构建 ====================

    /**
     * 构建SELECT字段列表.
     * <p>每个字段使用双引号转义, 别名为转义后的字段名.</p>
     */
    private String buildSelectClause(List<SysDataViewField> fields) {
        if (fields.isEmpty()) {
            return "*";
        }
        StringJoiner joiner = new StringJoiner(", ");
        for (SysDataViewField f : fields) {
            joiner.add(escapeFieldName(f.getFieldCode()) + " AS " + escapeFieldName(f.getFieldCode()));
        }
        return joiner.toString();
    }

    /**
     * 构建FROM子句.
     * <p>表来源类型(sourceType): 1=物理表 2=子查询SQL.
     * 物理表名通过元数据校验(已在view中), 不使用用户输入的表名.</p>
     */
    private String buildFromClause(SysDataView view) {
        if (view.getSourceType() != null && view.getSourceType() == 2) {
            String sourceSql = view.getSourceSql();
            if (sourceSql == null || sourceSql.isBlank()) {
                throw new BusinessException(ErrorCode.PARAM_MISSING, "SQL来源视图的source_sql不能为空");
            }
            validateSourceSql(sourceSql);
            return "(" + sourceSql + ") AS view_src";
        }
        String tableName = view.getSourceTable();
        if (tableName == null || tableName.isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "来源表不能为空");
        }
        return escapeFieldName(tableName);
    }

    /**
     * 构建动态WHERE子句.
     * <p>根据字段的search_type生成不同的匹配条件:
     * <ul>
     *   <li>eq / = — 精确匹配: field = 'value'</li>
     *   <li>like — 模糊匹配: field LIKE '%value%'</li>
     *   <li>between — 范围匹配: field BETWEEN 'v1' AND 'v2'</li>
     *   <li>默认 — 精确匹配</li>
     * </ul>
     * 基于物理表的视图自动添加 is_deleted = FALSE 条件.</p>
     */
    private String buildWhereClause(List<SysDataViewField> fields, Map<String, Object> queryParams, Integer sourceType) {
        boolean isTableBased = sourceType == null || sourceType != 2;
        List<String> conditions = new ArrayList<>();

        if (isTableBased) {
            conditions.add("is_deleted = FALSE");
        }

        if (queryParams != null && !queryParams.isEmpty()) {
            for (SysDataViewField f : fields) {
                if (Boolean.TRUE.equals(f.getIsSearchable()) && queryParams.containsKey(f.getFieldCode())) {
                    Object rawValue = queryParams.get(f.getFieldCode());
                    String searchType = f.getSearchType();
                    String condition = buildFieldCondition(f.getFieldCode(), searchType, rawValue);
                    if (condition != null) {
                        conditions.add(condition);
                    }
                }
            }
        }

        if (conditions.isEmpty()) {
            return "";
        }
        return " WHERE " + String.join(" AND ", conditions);
    }

    /**
     * 根据search_type构建单个字段的WHERE条件.
     */
    private String buildFieldCondition(String fieldCode, String searchType, Object rawValue) {
        if (rawValue == null) {
            return null;
        }
        String escapedField = escapeFieldName(fieldCode);
        String type = searchType != null ? searchType.toLowerCase() : "eq";

        switch (type) {
            case "like":
                String likeVal = rawValue.toString().replace("'", "''");
                if (likeVal.isBlank()) {
                    return null;
                }
                return escapedField + " LIKE '%" + likeVal + "%'";

            case "between":
                return buildBetweenCondition(escapedField, rawValue);

            case "eq":
            case "=":
            default:
                String eqVal = rawValue.toString().replace("'", "''");
                if (eqVal.isBlank()) {
                    return null;
                }
                return escapedField + " = '" + eqVal + "'";
        }
    }

    /**
     * 构建BETWEEN条件.
     * <p>支持格式: "v1,v2" 字符串、List集合.
     * BETWEEN要求左闭右闭, 等价于 field >= v1 AND field <= v2.</p>
     */
    private String buildBetweenCondition(String escapedField, Object rawValue) {
        if (rawValue instanceof List<?>) {
            List<?> list = (List<?>) rawValue;
            if (list.size() < 2) {
                return null;
            }
            return escapedField + " BETWEEN '"
                    + list.get(0).toString().replace("'", "''") + "' AND '"
                    + list.get(1).toString().replace("'", "''") + "'";
        }
        String strVal = rawValue.toString().trim();
        if (strVal.isBlank()) {
            return null;
        }
        String[] parts = strVal.split(",");
        if (parts.length < 2) {
            return null;
        }
        return escapedField + " BETWEEN '"
                + parts[0].trim().replace("'", "''") + "' AND '"
                + parts[1].trim().replace("'", "''") + "'";
    }

    // ==================== SQL白名单校验 ====================

    /**
     * 校验动态SQL仅包含SELECT语句, 禁止写操作关键字.
     * <p>对生成的SQL做终态校验, 双重保险确保安全.</p>
     */
    private void validateSqlWhitelist(String sql) {
        if (sql == null || sql.isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
        String upperSql = sql.toUpperCase().trim();
        if (!upperSql.startsWith("SELECT")) {
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
        for (String keyword : FORBIDDEN_SQL_KEYWORDS) {
            if (upperSql.matches(".*\\b" + keyword + "\\b.*")) {
                throw new BusinessException(ErrorCode.PARAM_INVALID);
            }
        }
    }

    /**
     * 校验来源SQL不包含写操作关键字.
     */
    private void validateSourceSql(String sourceSql) {
        String upperSql = sourceSql.toUpperCase().trim();
        for (String keyword : FORBIDDEN_SQL_KEYWORDS) {
            if (upperSql.matches(".*\\b" + keyword + "\\b.*")) {
                throw new BusinessException(ErrorCode.PARAM_INVALID);
            }
        }
        if (!upperSql.contains("SELECT") || !upperSql.contains("FROM")) {
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
    }

    // ==================== 字段转义（防注入） ====================

    /**
     * PostgreSQL双引号转义字段名/表名, 防止SQL注入.
     * <p>字段名中的双引号本身使用双双引号转义(PostgreSQL标准).</p>
     */
    private String escapeFieldName(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }
        return "\"" + name.replace("\"", "\"\"") + "\"";
    }

    // ==================== 排序字段解析 ====================

    /**
     * 解析排序字段名.
     * <p>优先使用请求参数 _sort, 若该字段不在视图可见字段中或不可排序则回退到默认值.</p>
     */
    private String resolveSortField(Map<String, Object> params, List<SysDataViewField> fields) {
        String sortField = DEFAULT_SORT_FIELD;
        if (params != null && params.containsKey("_sort")) {
            Object val = params.get("_sort");
            if (val != null && !val.toString().isBlank()) {
                sortField = val.toString();
            }
        }
        // 校验排序字段是否为可见且可排序的字段
        for (SysDataViewField f : fields) {
            if (f.getFieldCode().equalsIgnoreCase(sortField)) {
                if (Boolean.TRUE.equals(f.getIsSortable()) || f.getIsSortable() == null) {
                    return f.getFieldCode();
                }
                break;
            }
        }
        // 默认排序字段不要求必须在可见字段列表中
        return DEFAULT_SORT_FIELD;
    }

    private String getSortOrder(Map<String, Object> params) {
        if (params != null && params.containsKey("_order")) {
            Object val = params.get("_order");
            if (val != null && "ASC".equalsIgnoreCase(val.toString())) {
                return "ASC";
            }
        }
        return DEFAULT_SORT_ORDER;
    }

    // ==================== 分页参数提取 ====================

    private int getPageNum(Map<String, Object> params) {
        if (params == null) {
            return 1;
        }
        Object val = params.get("_page");
        if (val instanceof Number) {
            int p = ((Number) val).intValue();
            return p > 0 ? p : 1;
        }
        return 1;
    }

    private int getPageSize(Map<String, Object> params) {
        if (params == null) {
            return 10;
        }
        Object val = params.get("_size");
        if (val instanceof Number) {
            int s = ((Number) val).intValue();
            return s > 0 ? s : 10;
        }
        return 10;
    }
}
