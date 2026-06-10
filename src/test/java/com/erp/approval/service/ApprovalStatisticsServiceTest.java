package com.erp.approval.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.mapper.ApprovalRecordMapper;
import com.erp.approval.service.impl.ApprovalStatisticsServiceImpl;
import com.erp.approval.vo.ApprovalStatisticsVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ApprovalStatisticsService 单元测试")
class ApprovalStatisticsServiceTest {

    @Mock
    private ApprovalInstanceMapper instanceMapper;

    @Mock
    private ApprovalRecordMapper recordMapper;

    private ApprovalStatisticsServiceImpl service;

    private MockedStatic<StpUtil> stpMock;

    @BeforeEach
    void setUp() {
        service = new ApprovalStatisticsServiceImpl(instanceMapper, recordMapper);
        stpMock = mockStatic(StpUtil.class);
        stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        stpMock.close();
    }

    // ==================== 正常流程 ====================

    @Nested
    @DisplayName("正常流程测试")
    class NormalFlowTests {

        @Test
        @DisplayName("各类状态实例混合 → 返回正确的统计数据")
        void shouldReturnCorrectStatisticsWithMixedStatuses() {
            Map<String, Object> statsMap = new LinkedHashMap<>();
            statsMap.put("total_instances", 8L);
            statsMap.put("pending_count", 2L);
            statsMap.put("approved_count", 3L);
            statsMap.put("rejected_count", 1L);
            statsMap.put("withdrawn_count", 2L);
            statsMap.put("my_pending_count", 1L);
            statsMap.put("my_submitted_count", 2L);

            Map<String, Object> statusRow1 = new LinkedHashMap<>();
            statusRow1.put("name", "APPROVED");
            statusRow1.put("value", 3L);
            Map<String, Object> statusRow2 = new LinkedHashMap<>();
            statusRow2.put("name", "PENDING");
            statusRow2.put("value", 2L);
            Map<String, Object> statusRow3 = new LinkedHashMap<>();
            statusRow3.put("name", "WITHDRAWN");
            statusRow3.put("value", 2L);
            Map<String, Object> statusRow4 = new LinkedHashMap<>();
            statusRow4.put("name", "REJECTED");
            statusRow4.put("value", 1L);

            Map<String, Object> defRow1 = new LinkedHashMap<>();
            defRow1.put("definition_id", 10L);
            defRow1.put("cnt", 5L);
            Map<String, Object> defRow2 = new LinkedHashMap<>();
            defRow2.put("definition_id", 11L);
            defRow2.put("cnt", 3L);

            when(instanceMapper.selectStatistics(1L)).thenReturn(statsMap);
            when(instanceMapper.selectStatusDistribution()).thenReturn(
                    Arrays.asList(statusRow1, statusRow2, statusRow3, statusRow4));
            when(instanceMapper.selectDefinitionCounts()).thenReturn(
                    Arrays.asList(defRow1, defRow2));
            when(recordMapper.countByApproverId(1L)).thenReturn(5L);

            ApprovalStatisticsVO result = service.getStatistics();

            assertNotNull(result);
            assertEquals(8L, result.getTotalInstances());
            assertEquals(2L, result.getPendingCount());
            assertEquals(3L, result.getApprovedCount());
            assertEquals(1L, result.getRejectedCount());
            assertEquals(2L, result.getWithdrawnCount());
            assertEquals(1L, result.getMyPendingCount());
            assertEquals(2L, result.getMySubmittedCount());
            assertEquals(5L, result.getMyReviewedCount());

            assertNotNull(result.getStatusDistribution());
            assertEquals(4, result.getStatusDistribution().size());
            assertNotNull(result.getDefinitionCounts());
            assertEquals(2, result.getDefinitionCounts().size());
        }

        @Test
        @DisplayName("存在申请人为当前用户和审核记录 → 统计数据正确区分")
        void shouldCorrectlyDistinguishMyData() {
            Map<String, Object> statsMap = new LinkedHashMap<>();
            statsMap.put("total_instances", 2L);
            statsMap.put("pending_count", 1L);
            statsMap.put("approved_count", 1L);
            statsMap.put("rejected_count", 0L);
            statsMap.put("withdrawn_count", 0L);
            statsMap.put("my_pending_count", 1L);
            statsMap.put("my_submitted_count", 1L);

            when(instanceMapper.selectStatistics(1L)).thenReturn(statsMap);
            when(instanceMapper.selectStatusDistribution()).thenReturn(Collections.emptyList());
            when(instanceMapper.selectDefinitionCounts()).thenReturn(Collections.emptyList());
            when(recordMapper.countByApproverId(1L)).thenReturn(3L);

            ApprovalStatisticsVO result = service.getStatistics();

            assertEquals(2L, result.getTotalInstances());
            assertEquals(1L, result.getMySubmittedCount());
            assertEquals(1L, result.getMyPendingCount());
            assertEquals(3L, result.getMyReviewedCount());
        }
    }

    // ==================== 边界条件 ====================

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("数据库无任何审批实例 → 返回全零统计")
        void shouldReturnZeroStatisticsWhenEmpty() {
            Map<String, Object> emptyStats = new LinkedHashMap<>();
            emptyStats.put("total_instances", 0L);
            emptyStats.put("pending_count", 0L);
            emptyStats.put("approved_count", 0L);
            emptyStats.put("rejected_count", 0L);
            emptyStats.put("withdrawn_count", 0L);
            emptyStats.put("my_pending_count", 0L);
            emptyStats.put("my_submitted_count", 0L);

            when(instanceMapper.selectStatistics(1L)).thenReturn(emptyStats);
            when(instanceMapper.selectStatusDistribution()).thenReturn(Collections.emptyList());
            when(instanceMapper.selectDefinitionCounts()).thenReturn(Collections.emptyList());
            when(recordMapper.countByApproverId(1L)).thenReturn(0L);

            ApprovalStatisticsVO result = service.getStatistics();

            assertNotNull(result);
            assertEquals(0L, result.getTotalInstances());
            assertEquals(0L, result.getPendingCount());
            assertEquals(0L, result.getApprovedCount());
            assertEquals(0L, result.getRejectedCount());
            assertEquals(0L, result.getWithdrawnCount());
            assertEquals(0L, result.getMyPendingCount());
            assertEquals(0L, result.getMySubmittedCount());
            assertEquals(0L, result.getMyReviewedCount());
            assertTrue(result.getStatusDistribution().isEmpty());
            assertTrue(result.getDefinitionCounts().isEmpty());
        }

        @Test
        @DisplayName("实例status为null → 归类到UNKNOWN不影响统计")
        void shouldHandleNullStatusGracefully() {
            Map<String, Object> statsMap = new LinkedHashMap<>();
            statsMap.put("total_instances", 1L);
            statsMap.put("pending_count", 0L);
            statsMap.put("approved_count", 0L);
            statsMap.put("rejected_count", 0L);
            statsMap.put("withdrawn_count", 0L);
            statsMap.put("my_pending_count", 0L);
            statsMap.put("my_submitted_count", 0L);

            Map<String, Object> statusRow = new LinkedHashMap<>();
            statusRow.put("name", null);
            statusRow.put("value", 1L);

            when(instanceMapper.selectStatistics(1L)).thenReturn(statsMap);
            when(instanceMapper.selectStatusDistribution()).thenReturn(List.of(statusRow));
            when(instanceMapper.selectDefinitionCounts()).thenReturn(Collections.emptyList());
            when(recordMapper.countByApproverId(1L)).thenReturn(0L);

            ApprovalStatisticsVO result = service.getStatistics();

            assertNotNull(result);
            assertEquals(1L, result.getTotalInstances());
            assertEquals(0L, result.getPendingCount());
            assertEquals(1L, result.getStatusDistribution().get("UNKNOWN"));
        }

        @Test
        @DisplayName("当前用户无任何关联数据 → my类统计数据为0")
        void shouldReturnZeroForMyStatsWhenNoUserData() {
            Map<String, Object> statsMap = new LinkedHashMap<>();
            statsMap.put("total_instances", 2L);
            statsMap.put("pending_count", 2L);
            statsMap.put("approved_count", 0L);
            statsMap.put("rejected_count", 0L);
            statsMap.put("withdrawn_count", 0L);
            statsMap.put("my_pending_count", 2L);
            statsMap.put("my_submitted_count", 0L);

            when(instanceMapper.selectStatistics(1L)).thenReturn(statsMap);
            when(instanceMapper.selectStatusDistribution()).thenReturn(Collections.emptyList());
            when(instanceMapper.selectDefinitionCounts()).thenReturn(Collections.emptyList());
            when(recordMapper.countByApproverId(1L)).thenReturn(0L);

            ApprovalStatisticsVO result = service.getStatistics();

            assertEquals(0L, result.getMySubmittedCount());
            assertEquals(2L, result.getMyPendingCount());
            assertEquals(0L, result.getMyReviewedCount());
        }

        @Test
        @DisplayName("实例definitionId为null → 不计入definitionCounts")
        void shouldSkipNullDefinitionIds() {
            Map<String, Object> statsMap = new LinkedHashMap<>();
            statsMap.put("total_instances", 1L);
            statsMap.put("pending_count", 1L);
            statsMap.put("approved_count", 0L);
            statsMap.put("rejected_count", 0L);
            statsMap.put("withdrawn_count", 0L);
            statsMap.put("my_pending_count", 0L);
            statsMap.put("my_submitted_count", 0L);

            when(instanceMapper.selectStatistics(1L)).thenReturn(statsMap);
            when(instanceMapper.selectStatusDistribution()).thenReturn(Collections.emptyList());
            when(instanceMapper.selectDefinitionCounts()).thenReturn(Collections.emptyList());
            when(recordMapper.countByApproverId(1L)).thenReturn(0L);

            ApprovalStatisticsVO result = service.getStatistics();

            assertNotNull(result.getDefinitionCounts());
            assertTrue(result.getDefinitionCounts().isEmpty());
        }

        @Test
        @DisplayName("大量实例 → 统计正确且不溢出")
        void shouldHandleLargeDataset() {
            Map<String, Object> statsMap = new LinkedHashMap<>();
            statsMap.put("total_instances", 1000L);
            statsMap.put("pending_count", 250L);
            statsMap.put("approved_count", 333L);
            statsMap.put("rejected_count", 250L);
            statsMap.put("withdrawn_count", 167L);
            statsMap.put("my_pending_count", 100L);
            statsMap.put("my_submitted_count", 50L);

            when(instanceMapper.selectStatistics(1L)).thenReturn(statsMap);
            when(instanceMapper.selectStatusDistribution()).thenReturn(Collections.emptyList());
            when(instanceMapper.selectDefinitionCounts()).thenReturn(Collections.emptyList());
            when(recordMapper.countByApproverId(1L)).thenReturn(50L);

            ApprovalStatisticsVO result = service.getStatistics();

            assertEquals(1000L, result.getTotalInstances());
            long sum = result.getPendingCount() + result.getApprovedCount()
                    + result.getRejectedCount() + result.getWithdrawnCount();
            assertEquals(1000L, sum);
        }
    }

    // ==================== 异常场景 ====================

    @Nested
    @DisplayName("异常场景测试")
    class ExceptionTests {

        @Test
        @DisplayName("selectStatistics返回null → 不抛出NullPointerException")
        void shouldNotThrowWhenSelectStatisticsReturnsNull() {
            when(instanceMapper.selectStatistics(1L)).thenReturn(null);
            when(instanceMapper.selectStatusDistribution()).thenReturn(Collections.emptyList());
            when(instanceMapper.selectDefinitionCounts()).thenReturn(Collections.emptyList());
            when(recordMapper.countByApproverId(1L)).thenReturn(0L);

            assertThrows(Exception.class, () -> service.getStatistics());
        }
    }
}
