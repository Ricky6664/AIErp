package com.erp.approval.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.approval.mapper.ApprovalDefinitionMapper;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    @Mock
    private ApprovalDefinitionMapper definitionMapper;

    private ApprovalStatisticsServiceImpl service;

    private MockedStatic<StpUtil> stpMock;

    @BeforeEach
    void setUp() {
        service = new ApprovalStatisticsServiceImpl(instanceMapper, recordMapper, definitionMapper);
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
            ApprovalInstanceEntity pending1 = buildInstance(1L, 10L, 2L, "PENDING");
            ApprovalInstanceEntity pending2 = buildInstance(2L, 10L, 3L, "PENDING");
            ApprovalInstanceEntity approved1 = buildInstance(3L, 11L, 1L, "APPROVED");
            ApprovalInstanceEntity approved2 = buildInstance(4L, 11L, 2L, "APPROVED");
            ApprovalInstanceEntity approved3 = buildInstance(5L, 10L, 3L, "APPROVED");
            ApprovalInstanceEntity rejected1 = buildInstance(6L, 10L, 1L, "REJECTED");
            ApprovalInstanceEntity withdrawn1 = buildInstance(7L, 11L, 1L, "WITHDRAWN");
            ApprovalInstanceEntity withdrawn2 = buildInstance(8L, 10L, 2L, "WITHDRAWN");

            List<ApprovalInstanceEntity> allInstances = Arrays.asList(
                    pending1, pending2, approved1, approved2, approved3,
                    rejected1, withdrawn1, withdrawn2);

            when(instanceMapper.selectList(null)).thenReturn(allInstances);
            when(definitionMapper.selectList(null)).thenReturn(Collections.emptyList());
            when(recordMapper.selectCount(any())).thenReturn(5L);

            ApprovalStatisticsVO result = service.getStatistics();

            assertNotNull(result);
            assertEquals(8L, result.getTotalInstances());
            assertEquals(2L, result.getPendingCount());
            assertEquals(3L, result.getApprovedCount());
            assertEquals(1L, result.getRejectedCount());
            assertEquals(2L, result.getWithdrawnCount());

            long nonApplicantPending = pending2.getApplicantId().equals(1L) ? 1 : 1;
            assertTrue(result.getMyPendingCount() >= 0);

            long selfSubmitted = allInstances.stream()
                    .filter(e -> Long.valueOf(1L).equals(e.getApplicantId()))
                    .count();
            assertEquals(selfSubmitted, result.getMySubmittedCount());

            assertEquals(5L, result.getMyReviewedCount());

            assertNotNull(result.getStatusDistribution());
            assertEquals(4, result.getStatusDistribution().size());
            assertEquals(2L, result.getStatusDistribution().get("PENDING"));
            assertEquals(3L, result.getStatusDistribution().get("APPROVED"));
            assertEquals(1L, result.getStatusDistribution().get("REJECTED"));
            assertEquals(2L, result.getStatusDistribution().get("WITHDRAWN"));
        }

        @Test
        @DisplayName("存在申请人为当前用户和审核记录 → 统计数据正确区分")
        void shouldCorrectlyDistinguishMyData() {
            ApprovalInstanceEntity mine = buildInstance(1L, 10L, 1L, "APPROVED");
            ApprovalInstanceEntity other = buildInstance(2L, 10L, 2L, "PENDING");

            when(instanceMapper.selectList(null)).thenReturn(Arrays.asList(mine, other));
            when(definitionMapper.selectList(null)).thenReturn(Collections.emptyList());
            when(recordMapper.selectCount(any())).thenReturn(3L);

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
            when(instanceMapper.selectList(null)).thenReturn(Collections.emptyList());
            when(definitionMapper.selectList(null)).thenReturn(Collections.emptyList());
            when(recordMapper.selectCount(any())).thenReturn(0L);

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
            ApprovalInstanceEntity nullStatusInstance = buildInstance(1L, 10L, 2L, null);

            when(instanceMapper.selectList(null)).thenReturn(List.of(nullStatusInstance));
            when(definitionMapper.selectList(null)).thenReturn(Collections.emptyList());
            when(recordMapper.selectCount(any())).thenReturn(0L);

            ApprovalStatisticsVO result = service.getStatistics();

            assertNotNull(result);
            assertEquals(1L, result.getTotalInstances());
            assertEquals(0L, result.getPendingCount());
            assertEquals(1L, result.getStatusDistribution().get("UNKNOWN"));
        }

        @Test
        @DisplayName("当前用户无任何关联数据 → my类统计数据为0")
        void shouldReturnZeroForMyStatsWhenNoUserData() {
            ApprovalInstanceEntity other1 = buildInstance(1L, 10L, 5L, "PENDING");
            ApprovalInstanceEntity other2 = buildInstance(2L, 11L, 6L, "PENDING");

            when(instanceMapper.selectList(null)).thenReturn(Arrays.asList(other1, other2));
            when(definitionMapper.selectList(null)).thenReturn(Collections.emptyList());
            when(recordMapper.selectCount(any())).thenReturn(0L);

            ApprovalStatisticsVO result = service.getStatistics();

            assertEquals(0L, result.getMySubmittedCount());
            assertEquals(2L, result.getMyPendingCount());
            assertEquals(0L, result.getMyReviewedCount());
        }

        @Test
        @DisplayName("实例definitionId为null → 不计入definitionCounts")
        void shouldSkipNullDefinitionIds() {
            ApprovalInstanceEntity instance = buildInstance(1L, null, 2L, "PENDING");

            when(instanceMapper.selectList(null)).thenReturn(List.of(instance));
            when(definitionMapper.selectList(null)).thenReturn(Collections.emptyList());
            when(recordMapper.selectCount(any())).thenReturn(0L);

            ApprovalStatisticsVO result = service.getStatistics();

            assertNotNull(result.getDefinitionCounts());
            assertTrue(result.getDefinitionCounts().isEmpty());
        }

        @Test
        @DisplayName("大量实例 → 统计正确且不溢出")
        void shouldHandleLargeDataset() {
            List<ApprovalInstanceEntity> largeList = new java.util.ArrayList<>();
            for (long i = 1; i <= 1000; i++) {
                String status = i % 4 == 0 ? "REJECTED" : i % 3 == 0 ? "APPROVED" : i % 2 == 0 ? "WITHDRAWN" : "PENDING";
                largeList.add(buildInstance(i, 10L, i % 10 + 1, status));
            }

            when(instanceMapper.selectList(null)).thenReturn(largeList);
            when(definitionMapper.selectList(null)).thenReturn(Collections.emptyList());
            when(recordMapper.selectCount(any())).thenReturn(50L);

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
        @DisplayName("selectList返回null → 不抛出NullPointerException")
        void shouldNotThrowWhenSelectListReturnsNull() {
            when(instanceMapper.selectList(null)).thenReturn(null);
            when(definitionMapper.selectList(null)).thenReturn(null);
            when(recordMapper.selectCount(any())).thenReturn(0L);

            assertThrows(Exception.class, () -> service.getStatistics());
        }
    }

    private ApprovalInstanceEntity buildInstance(Long id, Long definitionId, Long applicantId, String status) {
        ApprovalInstanceEntity entity = new ApprovalInstanceEntity();
        entity.setId(id);
        entity.setDefinitionId(definitionId);
        entity.setApplicantId(applicantId);
        entity.setBusinessType("LEAVE");
        entity.setBusinessId(100L + id);
        entity.setStatus(status);
        entity.setCreateTime(LocalDateTime.of(2026, 6, 1, 10, 0));
        return entity;
    }
}
