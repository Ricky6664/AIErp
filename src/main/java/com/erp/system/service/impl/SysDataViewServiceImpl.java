package com.erp.system.service.impl;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.system.entity.SysDataView;
import com.erp.system.entity.SysDataViewField;
import com.erp.system.mapper.SysDataViewMapper;
import com.erp.system.service.SysDataViewService;
import com.erp.system.vo.SysDataViewFieldVO;
import com.erp.system.vo.SysDataViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 数据视图 Service 实现.
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@Service
public class SysDataViewServiceImpl extends SysDataViewService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SysDataViewMapper sysDataViewMapper;

    private static final int MAX_PAGE_SIZE = 100;
    private static final String DEFAULT_SORT_FIELD = "create_time";
    private static final String DEFAULT_SORT_ORDER = "DESC";

    // ========== getViewMeta ==========

    @Override
    public SysDataViewVO.DetailVO getViewMeta(String viewCode) {
        SysDataView entity = sysDataViewMapper.selectByViewCode(viewCode);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "视图不存在: " + viewCode);
        }
        SysDataViewVO.DetailVO vo = toVO(entity);
        List<SysDataViewField> fields = fieldMapper.selectVisibleFields(entity.getId());
        List<SysDataViewFieldVO.DetailVO> fieldVOs;
        if (fields == null || fields.isEmpty()) {
            fieldVOs = new ArrayList<>();
        } else {
            fieldVOs = fields.stream().map(f -> {
                SysDataViewFieldVO.DetailVO fvo = new SysDataViewFieldVO.DetailVO();
                BeanUtils.copyProperties(f, fvo);
                fvo.setFieldTypeName(f.getFieldType());
                return fvo;
            }).collect(Collectors.toList());
        }
        vo.setFields(fieldVOs);
        return vo;
    }

    // ========== executeView ==========

    @Override
    public PageResult<Map<String, Object>> executeView(String viewCode, Map<String, Object> queryParams) {
        SysDataView view = sysDataViewMapper.selectByViewCode(viewCode);
        if (view == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "视图不存在: " + viewCode);
        }

        List<SysDataViewField> fields = fieldMapper.selectVisibleFields(view.getId());
        if (fields == null || fields.isEmpty()) {
            return PageResult.empty();
        }

        int page = getPageNum(queryParams);
        int size = Math.min(getPageSize(queryParams), MAX_PAGE_SIZE);
        String sortField = getSortField(queryParams);
        String sortOrder = getSortOrder(queryParams);

        String escapedSortField = findAndEscapeField(fields, sortField);
        if (escapedSortField == null) {
            escapedSortField = escapeFieldName(DEFAULT_SORT_FIELD);
        }

        String selectClause = buildSelectClause(fields);
        String fromClause = buildFromClause(view);
        String whereClause = buildWhereClause(fields, queryParams, view.getSourceType());

        String dataSql = "SELECT " + selectClause + " FROM " + fromClause
                + whereClause
                + " ORDER BY " + escapedSortField + " " + sortOrder
                + " LIMIT " + size + " OFFSET " + ((page - 1) * size);

        String countSql = "SELECT COUNT(*) FROM " + fromClause + whereClause;

        log.debug("executeView SQL: {}", dataSql);
        log.debug("executeView Count SQL: {}", countSql);

        Long total = jdbcTemplate.queryForObject(countSql, Long.class);
        List<Map<String, Object>> list;
        if (total != null && total > 0) {
            list = jdbcTemplate.queryForList(dataSql);
        } else {
            list = Collections.emptyList();
        }

        return PageResult.of(list, total != null ? total : 0L, page, size);
    }

    // ========== SQL 构建辅助方法 ==========

    private String buildSelectClause(List<SysDataViewField> fields) {
        StringJoiner joiner = new StringJoiner(", ");
        for (SysDataViewField f : fields) {
            joiner.add(escapeFieldName(f.getFieldCode()) + " AS " + escapeFieldName(f.getFieldCode()));
        }
        return joiner.toString();
    }

    private String buildFromClause(SysDataView view) {
        if (view.getSourceType() != null && view.getSourceType() == 2
                && view.getSourceSql() != null && !view.getSourceSql().isBlank()) {
            validateSourceSql(view.getSourceType(), view.getSourceSql());
            return "(" + view.getSourceSql() + ") AS view_src";
        }
        String tableName = view.getSourceTable();
        if (tableName == null || tableName.isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "来源表不能为空");
        }
        return escapeFieldName(tableName);
    }

    private String buildWhereClause(List<SysDataViewField> fields, Map<String, Object> queryParams, Integer sourceType) {
        boolean isTableBased = sourceType == null || sourceType != 2;
        List<String> conditions = new ArrayList<>();

        if (isTableBased) {
            conditions.add("is_deleted = FALSE");
        }

        if (queryParams != null && !queryParams.isEmpty()) {
            for (SysDataViewField f : fields) {
                if (Boolean.TRUE.equals(f.getIsSearchable()) && queryParams.containsKey(f.getFieldCode())) {
                    Object value = queryParams.get(f.getFieldCode());
                    if (value != null && !value.toString().isBlank()) {
                        conditions.add(escapeFieldName(f.getFieldCode()) + " = '" + escapeValue(value.toString()) + "'");
                    }
                }
            }
        }

        if (conditions.isEmpty()) {
            return "";
        }
        return " WHERE " + String.join(" AND ", conditions);
    }

    private String escapeValue(String value) {
        return value.replace("'", "''");
    }

    private String findAndEscapeField(List<SysDataViewField> fields, String sortField) {
        for (SysDataViewField f : fields) {
            if (f.getFieldCode().equalsIgnoreCase(sortField)) {
                return escapeFieldName(f.getFieldCode());
            }
        }
        if (DEFAULT_SORT_FIELD.equals(sortField)) {
            return escapeFieldName(DEFAULT_SORT_FIELD);
        }
        return null;
    }

    // ========== 参数提取 ==========

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

    private String getSortField(Map<String, Object> params) {
        if (params == null) {
            return DEFAULT_SORT_FIELD;
        }
        Object val = params.get("_sort");
        if (val != null && !val.toString().isBlank()) {
            return val.toString();
        }
        return DEFAULT_SORT_FIELD;
    }

    private String getSortOrder(Map<String, Object> params) {
        if (params == null) {
            return DEFAULT_SORT_ORDER;
        }
        Object val = params.get("_order");
        if (val != null && "ASC".equalsIgnoreCase(val.toString())) {
            return "ASC";
        }
        return DEFAULT_SORT_ORDER;
    }
}
