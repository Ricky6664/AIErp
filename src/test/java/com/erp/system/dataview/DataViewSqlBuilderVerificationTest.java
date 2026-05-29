package com.erp.system.dataview;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysDataView;
import com.erp.system.entity.SysDataViewField;
import com.erp.system.mapper.SysDataViewFieldMapper;
import com.erp.system.mapper.SysDataViewMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DataViewSqlBuilder 验证测试")
class DataViewSqlBuilderVerificationTest {

    @Mock
    private SysDataViewMapper sysDataViewMapper;

    @Mock
    private SysDataViewFieldMapper fieldMapper;

    @InjectMocks
    private DataViewSqlBuilder builder;

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
        f1.setFieldType("Long");
        f1.setFieldOrder(1);
        f1.setIsSearchable(true);
        f1.setIsSortable(true);
        f1.setIsVisible(true);
        f1.setSearchType("eq");
        mockFields.add(f1);

        SysDataViewField f2 = new SysDataViewField();
        f2.setFieldCode("name");
        f2.setFieldName("名称");
        f2.setFieldType("String");
        f2.setFieldOrder(2);
        f2.setIsSearchable(true);
        f2.setIsSortable(true);
        f2.setIsVisible(true);
        f2.setSearchType("like");
        mockFields.add(f2);

        SysDataViewField f3 = new SysDataViewField();
        f3.setFieldCode("create_time");
        f3.setFieldName("创建时间");
        f3.setFieldType("LocalDateTime");
        f3.setFieldOrder(3);
        f3.setIsSearchable(true);
        f3.setIsSortable(true);
        f3.setIsVisible(true);
        f3.setSearchType("between");
        mockFields.add(f3);

        when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
        when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);
    }

    // ==================== 验收标准1: SQL白名单严格 ====================

    @Nested
    @DisplayName("验收标准1 - SQL白名单严格校验")
    class SqlWhitelistTests {

        @Test
        @DisplayName("正常SELECT语句通过白名单校验")
        void shouldAllowValidSelect() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result = builder.buildSelectSql(1L, Map.of());

            assertNotNull(result);
            assertTrue(result.getDataSql().startsWith("SELECT"));
            assertTrue(result.getCountSql().startsWith("SELECT COUNT(*)"));
        }

        @Test
        @DisplayName("来源SQL包含DROP关键字时拒绝")
        void shouldRejectDropKeyword() {
            mockView.setSourceType(2);
            mockView.setSourceSql("SELECT * FROM t; DROP TABLE t;");

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    builder.buildSelectSql(1L, Map.of()));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("来源SQL包含DELETE关键字时拒绝")
        void shouldRejectDeleteKeyword() {
            mockView.setSourceType(2);
            mockView.setSourceSql("DELETE FROM t WHERE id=1");

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    builder.buildSelectSql(1L, Map.of()));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("来源SQL包含UPDATE关键字时拒绝")
        void shouldRejectUpdateKeyword() {
            mockView.setSourceType(2);
            mockView.setSourceSql("UPDATE t SET name='x' WHERE id=1");

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    builder.buildSelectSql(1L, Map.of()));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("来源SQL包含INSERT关键字时拒绝")
        void shouldRejectInsertKeyword() {
            mockView.setSourceType(2);
            mockView.setSourceSql("INSERT INTO t VALUES (1, 'x')");

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    builder.buildSelectSql(1L, Map.of()));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("来源SQL包含ALTER关键字时拒绝")
        void shouldRejectAlterKeyword() {
            mockView.setSourceType(2);
            mockView.setSourceSql("ALTER TABLE t ADD COLUMN x INT");

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    builder.buildSelectSql(1L, Map.of()));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("来源SQL包含TRUNCATE关键字时拒绝")
        void shouldRejectTruncateKeyword() {
            mockView.setSourceType(2);
            mockView.setSourceSql("TRUNCATE TABLE t");

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    builder.buildSelectSql(1L, Map.of()));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("来源SQL包含CREATE关键字时拒绝")
        void shouldRejectCreateKeyword() {
            mockView.setSourceType(2);
            mockView.setSourceSql("CREATE TABLE t (id INT)");

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    builder.buildSelectSql(1L, Map.of()));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("来源SQL包含EXEC关键字时拒绝")
        void shouldRejectExecKeyword() {
            mockView.setSourceType(2);
            mockView.setSourceSql("EXEC sp_dangerous");

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    builder.buildSelectSql(1L, Map.of()));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("来源SQL不含SELECT+FROM时拒绝")
        void shouldRejectNonSelectSourceSql() {
            mockView.setSourceType(2);
            mockView.setSourceSql("1=1");

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    builder.buildSelectSql(1L, Map.of()));
            assertEquals(ErrorCode.PARAM_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("合法的子查询SQL通过校验")
        void shouldAllowValidSubquerySource() {
            mockView.setSourceType(2);
            mockView.setSourceSql("SELECT id, name FROM sys_test WHERE is_deleted = FALSE");

            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result = builder.buildSelectSql(1L, Map.of());

            assertNotNull(result);
            assertTrue(result.getDataSql().startsWith("SELECT"));
        }
    }

    // ==================== 验收标准2: 字段名双引号转义防注入 ====================

    @Nested
    @DisplayName("验收标准2 - 字段名双引号转义防注入")
    class FieldNameEscapingTests {

        @Test
        @DisplayName("字段名使用双引号包裹")
        void shouldEscapeFieldNamesWithDoubleQuotes() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result = builder.buildSelectSql(1L, Map.of());

            String dataSql = result.getDataSql();
            assertTrue(dataSql.contains("\"id\""));
            assertTrue(dataSql.contains("\"name\""));
            assertTrue(dataSql.contains("\"create_time\""));
        }

        @Test
        @DisplayName("表名使用双引号包裹")
        void shouldEscapeTableNameWithDoubleQuotes() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result = builder.buildSelectSql(1L, Map.of());

            assertTrue(result.getDataSql().contains("\"sys_test\""));
        }

        @Test
        @DisplayName("搜索值中的单引号被转义为两个单引号")
        void shouldEscapeSingleQuoteInSearchValue() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result =
                    builder.buildSelectSql(1L, Map.of("name", "test'value"));

            assertTrue(result.getDataSql().contains("test''value"));
            assertFalse(result.getDataSql().contains("'test'value'"));
        }

        @Test
        @DisplayName("排序字段名使用双引号转义")
        void shouldEscapeSortFieldName() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result =
                    builder.buildSelectSql(1L, Map.of("_sort", "name", "_order", "ASC"));

            assertTrue(result.getDataSql().contains("ORDER BY \"name\" ASC"));
        }

        @Test
        @DisplayName("不可排序字段回退到默认排序字段")
        void shouldFallbackToDefaultSortWhenFieldNotSortable() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            mockFields.get(0).setIsSortable(false);

            DataViewSqlBuilder.SqlBuildResult result =
                    builder.buildSelectSql(1L, Map.of("_sort", "id"));

            assertTrue(result.getDataSql().contains("ORDER BY \"create_time\" DESC"));
        }
    }

    // ==================== 验收标准3: 分页排序执行正确 ====================

    @Nested
    @DisplayName("验收标准3 - 分页排序执行正确")
    class PaginationAndSortingTests {

        @Test
        @DisplayName("pageSize超过100时截断为100")
        void shouldCapPageSizeAt100() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result =
                    builder.buildSelectSql(1L, Map.of("_page", 1, "_size", 200));

            assertEquals(100, result.getPageSize());
            assertTrue(result.getDataSql().contains("LIMIT 100"));
        }

        @Test
        @DisplayName("默认排序为create_time DESC")
        void shouldDefaultSortToCreateTimeDesc() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result = builder.buildSelectSql(1L, Map.of());

            assertTrue(result.getDataSql().contains("ORDER BY \"create_time\" DESC"));
        }

        @Test
        @DisplayName("自定义排序字段生效")
        void shouldApplyCustomSortField() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result =
                    builder.buildSelectSql(1L, Map.of("_sort", "name"));

            assertTrue(result.getDataSql().contains("ORDER BY \"name\" DESC"));
        }

        @Test
        @DisplayName("自定义排序方向ASC生效")
        void shouldApplyCustomSortOrderAsc() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result =
                    builder.buildSelectSql(1L, Map.of("_sort", "id", "_order", "ASC"));

            assertTrue(result.getDataSql().contains("ORDER BY \"id\" ASC"));
        }

        @Test
        @DisplayName("LIMIT和OFFSET计算正确")
        void shouldCalculateLimitOffsetCorrectly() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result =
                    builder.buildSelectSql(1L, Map.of("_page", 3, "_size", 20));

            assertEquals(3, result.getPageNum());
            assertEquals(20, result.getPageSize());
            assertTrue(result.getDataSql().contains("LIMIT 20 OFFSET 40"));
        }

        @Test
        @DisplayName("页码小于1时默认为第1页")
        void shouldDefaultPageNumWhenInvalid() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result =
                    builder.buildSelectSql(1L, Map.of("_page", 0));

            assertEquals(1, result.getPageNum());
            assertTrue(result.getDataSql().contains("OFFSET 0"));
        }

        @Test
        @DisplayName("默认pageSize为10")
        void shouldDefaultPageSizeTo10() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result = builder.buildSelectSql(1L, Map.of());

            assertEquals(10, result.getPageSize());
            assertTrue(result.getDataSql().contains("LIMIT 10"));
        }
    }

    // ==================== 易错警示验证 ====================

    @Nested
    @DisplayName("易错警示 - is_deleted逻辑删除")
    class WarningVerificationTests {

        @Test
        @DisplayName("基于物理表的视图自动添加is_deleted = FALSE条件")
        void shouldAddIsDeletedConditionForTableBasedView() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result = builder.buildSelectSql(1L, Map.of());

            assertTrue(result.getDataSql().contains("is_deleted = FALSE"));
            assertTrue(result.getCountSql().contains("is_deleted = FALSE"));
        }

        @Test
        @DisplayName("子查询视图不自动添加is_deleted条件")
        void shouldNotAddIsDeletedForSubqueryView() {
            mockView.setSourceType(2);
            mockView.setSourceSql("SELECT id, name FROM sys_test WHERE is_deleted = FALSE");

            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result = builder.buildSelectSql(1L, Map.of());

            assertFalse(result.getDataSql().contains("is_deleted = FALSE AND"));
        }
    }

    // ==================== 搜索类型验证 ====================

    @Nested
    @DisplayName("搜索类型 - eq/like/between")
    class SearchTypeTests {

        @Test
        @DisplayName("eq类型生成精确匹配条件")
        void shouldGenerateEqCondition() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result =
                    builder.buildSelectSql(1L, Map.of("id", "123"));

            assertTrue(result.getDataSql().contains("\"id\" = '123'"));
        }

        @Test
        @DisplayName("like类型生成模糊匹配条件")
        void shouldGenerateLikeCondition() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result =
                    builder.buildSelectSql(1L, Map.of("name", "测试"));

            assertTrue(result.getDataSql().contains("\"name\" LIKE '%测试%'"));
        }

        @Test
        @DisplayName("between类型生成范围匹配条件(字符串格式)")
        void shouldGenerateBetweenConditionFromString() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result =
                    builder.buildSelectSql(1L, Map.of("create_time", "2026-01-01,2026-12-31"));

            assertTrue(result.getDataSql().contains("BETWEEN '2026-01-01' AND '2026-12-31'"));
        }

        @Test
        @DisplayName("between类型生成范围匹配条件(List格式)")
        void shouldGenerateBetweenConditionFromList() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result =
                    builder.buildSelectSql(1L, Map.of("create_time", List.of("2026-01-01", "2026-12-31")));

            assertTrue(result.getDataSql().contains("BETWEEN '2026-01-01' AND '2026-12-31'"));
        }
    }

    // ==================== 异常处理验证 ====================

    @Nested
    @DisplayName("异常处理")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("视图不存在时抛出BusinessException")
        void shouldThrowWhenViewNotFound() {
            when(sysDataViewMapper.selectById(anyLong())).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    builder.buildSelectSql(999L, Map.of()));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("物理表sourceTable为空时抛出BusinessException")
        void shouldThrowWhenSourceTableEmpty() {
            mockView.setSourceTable(null);

            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    builder.buildSelectSql(1L, Map.of()));
            assertEquals(ErrorCode.PARAM_MISSING.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("子查询视图sourceSql为空时抛出BusinessException")
        void shouldThrowWhenSourceSqlEmpty() {
            mockView.setSourceType(2);
            mockView.setSourceSql(null);

            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            BusinessException ex = assertThrows(BusinessException.class, () ->
                    builder.buildSelectSql(1L, Map.of()));
            assertEquals(ErrorCode.PARAM_MISSING.getCode(), ex.getCode());
        }
    }

    // ==================== 边界场景验证 ====================

    @Nested
    @DisplayName("边界场景")
    class EdgeCaseTests {

        @Test
        @DisplayName("字段列表为空时SELECT使用*")
        void shouldUseStarWhenNoFields() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(null);

            DataViewSqlBuilder.SqlBuildResult result = builder.buildSelectSql(1L, Map.of());

            assertTrue(result.getDataSql().startsWith("SELECT * FROM"));
        }

        @Test
        @DisplayName("queryParams为null时不抛异常")
        void shouldHandleNullQueryParams() {
            when(sysDataViewMapper.selectById(1L)).thenReturn(mockView);
            when(fieldMapper.selectVisibleFields(1L)).thenReturn(mockFields);

            DataViewSqlBuilder.SqlBuildResult result = builder.buildSelectSql(1L, null);

            assertNotNull(result);
            assertEquals(1, result.getPageNum());
        }
    }
}
