package com.erp.module.warehouse.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.erp.module.warehouse.mapper.WorkbenchAggregateAggregateMapper;
import com.erp.module.warehouse.service.impl.WorkbenchAggregateServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkbenchAggregateService 单元测试")
class WorkbenchAggregateServiceTest {

    @Mock
    private WorkbenchAggregateAggregateMapper workbenchAggregateMapper;

    @Mock
    private SaSession saSession;

    private MockedStatic<StpUtil> stpUtilMock;

    private WorkbenchAggregateServiceImpl workbenchAggregateService;

    @BeforeEach
    void setUp() {
        stpUtilMock = mockStatic(StpUtil.class);
        workbenchAggregateService = new WorkbenchAggregateServiceImpl(workbenchAggregateMapper);
    }

    @AfterEach
    void tearDown() {
        if (stpUtilMock != null) {
            stpUtilMock.close();
        }
    }

    private void mockTenantId(Object tenantId) {
        stpUtilMock.when(StpUtil::getSession).thenReturn(saSession);
        when(saSession.get("tenantId")).thenReturn(tenantId);
    }

    // ==================== getKpiStats ====================

    @Nested
    @DisplayName("getKpiStats KPI统计查询")
    class GetKpiStatsTests {

        @Test
        @DisplayName("正常返回KPI数据 -> 返回Map包含四个指标")
        void shouldReturnKpiStatsSuccessfully() {
            mockTenantId(1L);

            Map<String, Object> mockResult = new HashMap<>();
            mockResult.put("warehouseTotal", 10L);
            mockResult.put("warehouseActive", 8L);
            mockResult.put("locationTotal", 100L);
            mockResult.put("locationActive", 85L);

            when(workbenchAggregateMapper.selectKpiStats(1L)).thenReturn(mockResult);

            Map<String, Object> result = workbenchAggregateService.getKpiStats();

            assertNotNull(result);
            assertEquals(10L, result.get("warehouseTotal"));
            assertEquals(8L, result.get("warehouseActive"));
            assertEquals(100L, result.get("locationTotal"));
            assertEquals(85L, result.get("locationActive"));

            verify(workbenchAggregateMapper).selectKpiStats(1L);
        }

        @Test
        @DisplayName("mapper返回null -> 返回空Map(非null)")
        void shouldReturnEmptyMapWhenMapperReturnsNull() {
            mockTenantId(1L);
            when(workbenchAggregateMapper.selectKpiStats(1L)).thenReturn(null);

            Map<String, Object> result = workbenchAggregateService.getKpiStats();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("使用租户ID=0当session无tenantId -> 传递0L到mapper")
        void shouldUseZeroTenantIdWhenSessionHasNoTenantId() {
            mockTenantId(null);

            when(workbenchAggregateMapper.selectKpiStats(0L)).thenReturn(Collections.emptyMap());

            Map<String, Object> result = workbenchAggregateService.getKpiStats();

            assertNotNull(result);
            verify(workbenchAggregateMapper).selectKpiStats(0L);
        }

        @Test
        @DisplayName("session中tenantId为String类型 -> 正确转换为Long")
        void shouldConvertStringTenantIdToLong() {
            mockTenantId("100");

            Map<String, Object> mockResult = new HashMap<>();
            mockResult.put("warehouseTotal", 5L);
            when(workbenchAggregateMapper.selectKpiStats(100L)).thenReturn(mockResult);

            Map<String, Object> result = workbenchAggregateService.getKpiStats();

            assertNotNull(result);
            assertEquals(5L, result.get("warehouseTotal"));
            verify(workbenchAggregateMapper).selectKpiStats(100L);
        }
    }

    // ==================== getWarehouseTrend ====================

    @Nested
    @DisplayName("getWarehouseTrend 仓库创建趋势查询")
    class GetWarehouseTrendTests {

        @Test
        @DisplayName("正常返回趋势数据 -> 返回List<Map>")
        void shouldReturnWarehouseTrendSuccessfully() {
            mockTenantId(1L);

            Map<String, Object> day1 = new HashMap<>();
            day1.put("createDate", "2026-06-01");
            day1.put("count", 3L);

            Map<String, Object> day2 = new HashMap<>();
            day2.put("createDate", "2026-06-02");
            day2.put("count", 5L);

            List<Map<String, Object>> mockList = Arrays.asList(day1, day2);

            when(workbenchAggregateMapper.selectWarehouseTrendByDay(1L, "2026-06-01", "2026-06-07"))
                    .thenReturn(mockList);

            List<Map<String, Object>> result = workbenchAggregateService.getWarehouseTrend("2026-06-01", "2026-06-07");

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("2026-06-01", result.get(0).get("createDate"));
            assertEquals(3L, result.get(0).get("count"));

            verify(workbenchAggregateMapper).selectWarehouseTrendByDay(1L, "2026-06-01", "2026-06-07");
        }

        @Test
        @DisplayName("mapper返回null -> 返回空List(非null)")
        void shouldReturnEmptyListWhenMapperReturnsNull() {
            mockTenantId(1L);
            when(workbenchAggregateMapper.selectWarehouseTrendByDay(1L, "2026-01-01", "2026-12-31"))
                    .thenReturn(null);

            List<Map<String, Object>> result = workbenchAggregateService.getWarehouseTrend("2026-01-01", "2026-12-31");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("无session时使用tenantId=0 -> 正确传递参数")
        void shouldUseZeroTenantIdForTrendWhenNoSession() {
            mockTenantId(null);
            when(workbenchAggregateMapper.selectWarehouseTrendByDay(0L, "2026-06-01", "2026-06-07"))
                    .thenReturn(Collections.emptyList());

            List<Map<String, Object>> result = workbenchAggregateService.getWarehouseTrend("2026-06-01", "2026-06-07");

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(workbenchAggregateMapper).selectWarehouseTrendByDay(0L, "2026-06-01", "2026-06-07");
        }
    }

    // ==================== getLocationTrend ====================

    @Nested
    @DisplayName("getLocationTrend 库位创建趋势查询")
    class GetLocationTrendTests {

        @Test
        @DisplayName("正常返回趋势数据 -> 返回List<Map>")
        void shouldReturnLocationTrendSuccessfully() {
            mockTenantId(1L);

            Map<String, Object> day1 = new HashMap<>();
            day1.put("createDate", "2026-06-01");
            day1.put("count", 10L);

            List<Map<String, Object>> mockList = Collections.singletonList(day1);

            when(workbenchAggregateMapper.selectLocationTrendByDay(1L, "2026-06-01", "2026-06-07"))
                    .thenReturn(mockList);

            List<Map<String, Object>> result = workbenchAggregateService.getLocationTrend("2026-06-01", "2026-06-07");

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(10L, result.get(0).get("count"));

            verify(workbenchAggregateMapper).selectLocationTrendByDay(1L, "2026-06-01", "2026-06-07");
        }

        @Test
        @DisplayName("mapper返回null -> 返回空List(非null)")
        void shouldReturnEmptyListWhenLocationTrendNull() {
            mockTenantId(1L);
            when(workbenchAggregateMapper.selectLocationTrendByDay(1L, "2026-01-01", "2026-12-31"))
                    .thenReturn(null);

            List<Map<String, Object>> result = workbenchAggregateService.getLocationTrend("2026-01-01", "2026-12-31");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ==================== getCurrentTenantId ====================

    @Nested
    @DisplayName("getCurrentTenantId 租户ID获取")
    class GetCurrentTenantIdTests {

        @Test
        @DisplayName("session有tenantId(Long类型) -> 返回该租户ID")
        void shouldReturnTenantIdWhenSessionHasLong() {
            mockTenantId(100L);

            Long tenantId = workbenchAggregateService.getCurrentTenantId();

            assertEquals(100L, tenantId);
            verify(saSession).get("tenantId");
        }

        @Test
        @DisplayName("session有tenantId(String类型) -> 转换为Long返回")
        void shouldConvertStringTenantId() {
            mockTenantId("200");

            Long tenantId = workbenchAggregateService.getCurrentTenantId();

            assertEquals(200L, tenantId);
        }

        @Test
        @DisplayName("session无tenantId -> 返回0L")
        void shouldReturnZeroWhenNoTenantId() {
            mockTenantId(null);

            Long tenantId = workbenchAggregateService.getCurrentTenantId();

            assertEquals(0L, tenantId);
        }

        @Test
        @DisplayName("StpUtil抛异常 -> 返回0L不抛异常")
        void shouldReturnZeroWhenStpUtilThrowsException() {
            stpUtilMock.when(StpUtil::getSession).thenThrow(new RuntimeException("session error"));

            Long tenantId = workbenchAggregateService.getCurrentTenantId();

            assertEquals(0L, tenantId);
        }
    }

    // ==================== 缓存注解验证 ====================

    @Nested
    @DisplayName("@Cacheable 缓存注解验证")
    class CacheableAnnotationTests {

        @Test
        @DisplayName("getKpiStats方法标注@Cacheable")
        void shouldHaveCacheableOnGetKpiStats() throws NoSuchMethodException {
            var method = WorkbenchAggregateServiceImpl.class.getMethod("getKpiStats");
            var annotation = method.getAnnotation(org.springframework.cache.annotation.Cacheable.class);

            assertNotNull(annotation, "getKpiStats应标注@Cacheable");
            assertTrue(
                    Arrays.asList(annotation.value()).contains("workbench:kpi"),
                    "缓存名称应为workbench:kpi"
            );
        }
    }

    // ==================== 参数传递验证 ====================

    @Nested
    @DisplayName("Mapper调用参数验证")
    class MapperParameterTests {

        @Test
        @DisplayName("getWarehouseTrend正确传递日期范围到mapper")
        void shouldPassCorrectDateRangeToWarehouseTrendMapper() {
            mockTenantId(1L);
            when(workbenchAggregateMapper.selectWarehouseTrendByDay(eq(1L), eq("2026-01-01"), eq("2026-06-30")))
                    .thenReturn(Collections.emptyList());

            workbenchAggregateService.getWarehouseTrend("2026-01-01", "2026-06-30");

            verify(workbenchAggregateMapper).selectWarehouseTrendByDay(1L, "2026-01-01", "2026-06-30");
        }

        @Test
        @DisplayName("getLocationTrend正确传递日期范围到mapper")
        void shouldPassCorrectDateRangeToLocationTrendMapper() {
            mockTenantId(1L);
            when(workbenchAggregateMapper.selectLocationTrendByDay(eq(1L), eq("2026-03-01"), eq("2026-03-31")))
                    .thenReturn(Collections.emptyList());

            workbenchAggregateService.getLocationTrend("2026-03-01", "2026-03-31");

            verify(workbenchAggregateMapper).selectLocationTrendByDay(1L, "2026-03-01", "2026-03-31");
        }
    }
}
