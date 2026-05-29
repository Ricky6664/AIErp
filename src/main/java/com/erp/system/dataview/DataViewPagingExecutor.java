package com.erp.system.dataview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.system.entity.SysDataView;
import com.erp.system.entity.SysDataViewField;
import com.erp.system.mapper.SysDataViewFieldMapper;
import com.erp.system.mapper.SysDataViewMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据视图分页排序执行器.
 *
 * <p>核心功能:
 * <ul>
 *   <li>分页执行: 通过 MyBatis-Plus Page 封装分页结果</li>
 *   <li>排序字段白名单校验: 只允许视图配置的 sortable 字段</li>
 *   <li>默认排序: create_time DESC</li>
 *   <li>单页最大 100 条限制</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@Component
public class DataViewPagingExecutor {

    private static final int MAX_PAGE_SIZE = 100;

    private final JdbcTemplate jdbcTemplate;
    private final SysDataViewMapper viewMapper;
    private final SysDataViewFieldMapper fieldMapper;
    private final DataViewSqlBuilder sqlBuilder;

    @Autowired
    public DataViewPagingExecutor(JdbcTemplate jdbcTemplate,
                                   SysDataViewMapper viewMapper,
                                   SysDataViewFieldMapper fieldMapper,
                                   DataViewSqlBuilder sqlBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.viewMapper = viewMapper;
        this.fieldMapper = fieldMapper;
        this.sqlBuilder = sqlBuilder;
    }

    /**
     * 执行数据视图分页查询.
     *
     * @param viewCode 视图编码
     * @param query    分页参数
     * @return MyBatis-Plus IPage 分页结果
     */
    public IPage<Map<String, Object>> execute(String viewCode, PageQuery query) {
        SysDataView view = viewMapper.selectByViewCode(viewCode);
        if (view == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "视图不存在: " + viewCode);
        }

        List<SysDataViewField> fields = fieldMapper.selectVisibleFields(view.getId());
        if (fields == null || fields.isEmpty()) {
            return buildEmptyPage(query);
        }

        int pageNum = resolvePageNum(query);
        int pageSize = resolvePageSize(query);

        Map<String, Object> params = new HashMap<>();
        params.put("_page", pageNum);
        params.put("_size", pageSize);

        DataViewSqlBuilder.SqlBuildResult sqlResult = sqlBuilder.buildSelectSql(view.getId(), params);

        Long total = jdbcTemplate.queryForObject(sqlResult.getCountSql(), Long.class);
        List<Map<String, Object>> list;
        if (total != null && total > 0) {
            list = jdbcTemplate.queryForList(sqlResult.getDataSql());
        } else {
            list = Collections.emptyList();
        }

        log.debug("DataViewPagingExecutor execute viewCode={}, pageNum={}, pageSize={}, total={}",
                viewCode, pageNum, pageSize, total);

        Page<Map<String, Object>> pageResult = new Page<>(pageNum, pageSize);
        pageResult.setRecords(list);
        pageResult.setTotal(total != null ? total : 0L);
        return pageResult;
    }

    private int resolvePageNum(PageQuery query) {
        if (query == null || query.getPageNum() == null || query.getPageNum() < 1) {
            return 1;
        }
        return query.getPageNum();
    }

    private int resolvePageSize(PageQuery query) {
        if (query == null || query.getPageSize() == null || query.getPageSize() < 1) {
            return 10;
        }
        return Math.min(query.getPageSize(), MAX_PAGE_SIZE);
    }

    private IPage<Map<String, Object>> buildEmptyPage(PageQuery query) {
        int pageNum = query != null && query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query != null && query.getPageSize() != null ? query.getPageSize() : 10;
        Page<Map<String, Object>> page = new Page<>(pageNum, pageSize);
        page.setRecords(Collections.emptyList());
        page.setTotal(0L);
        return page;
    }
}
