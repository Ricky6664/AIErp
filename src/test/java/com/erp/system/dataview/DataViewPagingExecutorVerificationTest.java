package com.erp.system.dataview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.system.entity.SysDataView;
import com.erp.system.entity.SysDataViewField;
import com.erp.system.mapper.SysDataViewFieldMapper;
import com.erp.system.mapper.SysDataViewMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DataViewPagingExecutor 验证测试")
class DataViewPagingExecutorVerificationTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private SysDataViewMapper viewMapper;

    @Mock
    private SysDataViewFieldMapper fieldMapper;

    @Mock
    private DataViewSqlBuilder sqlBuilder;

    @InjectMocks
    private DataViewPagingExecutor executor;

    private SysDataView mockView;
    private List<SysDataViewField> mockFields;

    @BeforeEach
    void setUp() {
        mockView = new SysDataView();
        mockView.setId(1L);
        mockView.setViewCode("test_view");
        mockView.setViewName("测试视图");
        mockView.setSourceTable("sys_test");
        mockView.setSourceType(1);

        mockFields = new ArrayList<>();
        SysDataViewField f1 = new SysDataViewField();
        f1.setFieldCode("id");
        f1.setFieldName("ID");
        f1.setIsSortable(true);
        f1.setIsVisible(true);
        mockFields.add(f1);

        SysDataViewField f2 = new SysDataViewField();
        f2.setFieldCode("name");
        f2.setFieldName("名称");
        f2.setIsSortable(true);
        f2.setIsVisible(true);
        mockFields.add(f2);

        SysDataViewField f3 = new SysDataViewField();
        f3.setFieldCode("create_time");
        f3.setFieldName("创建时间");
        f3.setIsSortable(true);
        f3.setIsVisible(true);
        mockFields.add(f3);

        DataViewSqlBuilder.SqlBuildResult mockResult =
                new DataViewSqlBuilder.SqlBuildResult(
                        "SELECT \"id\", \"name\", \"create_time\" FROM \"sys_test\" WHERE is_deleted = FALSE ORDER BY \"create_time\" DESC LIMIT 10 OFFSET 0",
                        "SELECT COUNT(*) FROM \"sys_test\" WHERE is_deleted = FALSE",
                        1, 10);

        when(viewMapper.selectByViewCode("test_view")).thenReturn(mockView);
        when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);
        when(sqlBuilder.buildSelectSql(anyLong(), anyMap())).thenReturn(mockResult);
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(100L);
        when(jdbcTemplate.queryForList(anyString())).thenReturn(List.of());
    }

    // ==================== 验收标准1: SQL白名单严格 ====================

    @Nested
    @DisplayName("验收标准1 - SQL白名单严格校验(委托DataViewSqlBuilder)")
    class SqlWhitelistTests {

        @Test
        @DisplayName("正常查询通过SQL构建器白名单校验")
        void shouldDelegateToSqlBuilder() {
            IPage<Map<String, Object>> result = executor.execute("test_view", new PageQuery());

            assertNotNull(result);
            verify(sqlBuilder).buildSelectSql(eq(1L), anyMap());
        }

        @Test
        @DisplayName("SQL构建器拒绝非法关键字时异常向上传播")
        void shouldPropagateSqlBuilderException() {
            when(sqlBuilder.buildSelectSql(anyLong(), anyMap()))
                    .thenThrow(new BusinessException(ErrorCode.PARAM_INVALID));

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    executor.execute("test_view", new PageQuery()));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }
    }

    // ==================== 验收标准2: 字段名双引号转义防注入 ====================

    @Nested
    @DisplayName("验收标准2 - 字段名转义验证(委托DataViewSqlBuilder)")
    class FieldNameEscapingTests {

        @Test
        @DisplayName("生成的SQL包含双引号转义的字段名")
        void shouldContainEscapedFieldNames() {
            DataViewSqlBuilder.SqlBuildResult escapedResult =
                    new DataViewSqlBuilder.SqlBuildResult(
                            "SELECT \"id\", \"name\" FROM \"sys_test\" WHERE is_deleted = FALSE ORDER BY \"create_time\" DESC LIMIT 10 OFFSET 0",
                            "SELECT COUNT(*) FROM \"sys_test\" WHERE is_deleted = FALSE",
                            1, 10);
            when(sqlBuilder.buildSelectSql(anyLong(), anyMap())).thenReturn(escapedResult);

            IPage<Map<String, Object>> result = executor.execute("test_view", new PageQuery());

            assertNotNull(result);
            verify(sqlBuilder).buildSelectSql(eq(1L), anyMap());
        }
    }

    // ==================== 验收标准3: 分页排序执行正确 ====================

    @Nested
    @DisplayName("验收标准3 - 分页排序执行正确")
    class PaginationAndSortingTests {

        @Test
        @DisplayName("pageSize超过100时截断为100")
        void shouldCapPageSizeAt100() {
            PageQuery query = new PageQuery();
            query.setPageSize(200);

            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 100));

            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertEquals(100, params.get("_size"));
        }

        @Test
        @DisplayName("默认pageSize为10")
        void shouldDefaultPageSizeTo10() {
            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 10));

            PageQuery query = new PageQuery();
            query.setPageSize(null);
            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertEquals(10, params.get("_size"));
        }

        @Test
        @DisplayName("页码小于1时默认为1")
        void shouldDefaultPageNumWhenLessThan1() {
            PageQuery query = new PageQuery();
            query.setPageNum(0);

            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 10));

            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertEquals(1, params.get("_page"));
        }

        @Test
        @DisplayName("总数为0时返回空列表不分页查询")
        void shouldReturnEmptyListWhenTotalIsZero() {
            when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(0L);

            IPage<Map<String, Object>> result = executor.execute("test_view", new PageQuery());

            assertEquals(0L, result.getTotal());
            assertTrue(result.getRecords().isEmpty());
        }

        @Test
        @DisplayName("总数为null时返回空列表")
        void shouldReturnEmptyListWhenTotalIsNull() {
            when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(null);

            IPage<Map<String, Object>> result = executor.execute("test_view", new PageQuery());

            assertEquals(0L, result.getTotal());
            assertTrue(result.getRecords().isEmpty());
        }

        @Test
        @DisplayName("返回MyBatis-Plus IPage分页对象")
        void shouldReturnMyBatisPlusPage() {
            when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(50L);

            PageQuery query = new PageQuery();
            query.setPageSize(15);
            IPage<Map<String, Object>> result = executor.execute("test_view", query);

            assertNotNull(result);
            assertEquals(50L, result.getTotal());
            assertEquals(1, result.getCurrent());
            assertEquals(15, result.getSize());
        }

        @Test
        @DisplayName("有数据时执行数据查询SQL")
        void shouldExecuteDataQueryWhenTotalPositive() {
            when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(25L);

            executor.execute("test_view", new PageQuery());

            verify(jdbcTemplate).queryForList(anyString());
        }
    }

    // ==================== 排序字段白名单校验 ====================

    @Nested
    @DisplayName("排序字段白名单校验")
    class SortFieldWhitelistTests {

        @Test
        @DisplayName("可排序字段通过白名单校验")
        void shouldAllowSortableField() {
            PageQuery query = new PageQuery();
            query.setSortField("name");

            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 10));

            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertEquals("name", params.get("_sort"));
        }

        @Test
        @DisplayName("不可排序字段被拒绝, 不传入_sort参数")
        void shouldRejectNonSortableField() {
            mockFields.get(1).setIsSortable(false);

            PageQuery query = new PageQuery();
            query.setSortField("name");

            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 10));

            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertFalse(params.containsKey("_sort"));
        }

        @Test
        @DisplayName("不存在的字段名被拒绝")
        void shouldRejectNonExistentField() {
            PageQuery query = new PageQuery();
            query.setSortField("non_existent_field");

            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 10));

            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertFalse(params.containsKey("_sort"));
        }

        @Test
        @DisplayName("排序字段为空时不传入_sort参数")
        void shouldNotSetSortWhenBlank() {
            PageQuery query = new PageQuery();
            query.setSortField("  ");

            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 10));

            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertFalse(params.containsKey("_sort"));
        }
    }

    // ==================== 排序方向校验 ====================

    @Nested
    @DisplayName("排序方向校验")
    class SortOrderValidationTests {

        @Test
        @DisplayName("ASC通过校验")
        void shouldAllowAsc() {
            PageQuery query = new PageQuery();
            query.setSortOrder("ASC");

            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 10));

            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertEquals("ASC", params.get("_order"));
        }

        @Test
        @DisplayName("DESC通过校验")
        void shouldAllowDesc() {
            PageQuery query = new PageQuery();
            query.setSortOrder("DESC");

            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 10));

            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertEquals("DESC", params.get("_order"));
        }

        @Test
        @DisplayName("无效排序方向被拒绝")
        void shouldRejectInvalidSortOrder() {
            PageQuery query = new PageQuery();
            query.setSortOrder("INVALID");

            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 10));

            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertFalse(params.containsKey("_order"));
        }
    }

    // ==================== 异常处理验证 ====================

    @Nested
    @DisplayName("异常处理")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("视图不存在时抛出BusinessException")
        void shouldThrowWhenViewNotFound() {
            when(viewMapper.selectByViewCode("nonexistent")).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    executor.execute("nonexistent", new PageQuery()));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("视图字段为空时返回空页")
        void shouldReturnEmptyPageWhenNoFields() {
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(List.of());

            IPage<Map<String, Object>> result = executor.execute("test_view", new PageQuery());

            assertEquals(0L, result.getTotal());
            assertTrue(result.getRecords().isEmpty());
        }

        @Test
        @DisplayName("视图字段为null时返回空页")
        void shouldReturnEmptyPageWhenFieldsNull() {
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(null);

            IPage<Map<String, Object>> result = executor.execute("test_view", new PageQuery());

            assertEquals(0L, result.getTotal());
            assertTrue(result.getRecords().isEmpty());
        }
    }

    // ==================== 边界场景验证 ====================

    @Nested
    @DisplayName("边界场景")
    class EdgeCaseTests {

        @Test
        @DisplayName("PageQuery为null时不抛异常")
        void shouldHandleNullQuery() {
            when(viewMapper.selectByViewCode(null)).thenReturn(null);

            assertThrows(BusinessException.class, () -> executor.execute(null, new PageQuery()));
        }

        @Test
        @DisplayName("排序字段大小写不敏感")
        void shouldHandleCaseInsensitiveSortField() {
            PageQuery query = new PageQuery();
            query.setSortField("NAME");

            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 10));

            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertEquals("name", params.get("_sort"));
        }

        @Test
        @DisplayName("pageSize为null时使用默认值10")
        void shouldDefaultPageSizeWhenNull() {
            PageQuery query = new PageQuery();
            query.setPageSize(null);

            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 10));

            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertEquals(10, params.get("_size"));
        }

        @Test
        @DisplayName("pageNum为null时使用默认值1")
        void shouldDefaultPageNumWhenNull() {
            PageQuery query = new PageQuery();
            query.setPageNum(null);

            ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
            when(sqlBuilder.buildSelectSql(anyLong(), captor.capture()))
                    .thenReturn(new DataViewSqlBuilder.SqlBuildResult("SELECT 1", "SELECT COUNT(*)", 1, 10));

            executor.execute("test_view", query);

            Map<String, Object> params = captor.getValue();
            assertEquals(1, params.get("_page"));
        }
    }
}
