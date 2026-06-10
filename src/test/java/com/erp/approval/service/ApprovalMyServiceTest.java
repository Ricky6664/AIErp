package com.erp.approval.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.approval.dto.MyApprovalQueryDTO;
import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.approval.mapper.ApprovalDefinitionMapper;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.mapper.ApprovalRecordMapper;
import com.erp.approval.service.impl.ApprovalMyServiceImpl;
import com.erp.approval.vo.MyApprovalVO;
import com.erp.common.result.PageResult;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ApprovalMyService 单元测试")
class ApprovalMyServiceTest {

    @Mock
    private ApprovalInstanceMapper instanceMapper;

    @Mock
    private ApprovalRecordMapper recordMapper;

    @Mock
    private ApprovalDefinitionMapper definitionMapper;

    private ApprovalMyServiceImpl service;

    private MockedStatic<StpUtil> stpMock;

    private ApprovalInstanceEntity pendingInstance;
    private ApprovalRecordEntity recordEntity;
    private ApprovalDefinitionEntity definitionEntity;

    @BeforeEach
    void setUp() {
        service = new ApprovalMyServiceImpl(instanceMapper, recordMapper, definitionMapper);

        stpMock = mockStatic(StpUtil.class);
        stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

        pendingInstance = new ApprovalInstanceEntity();
        pendingInstance.setId(100L);
        pendingInstance.setDefinitionId(10L);
        pendingInstance.setBusinessType("LEAVE");
        pendingInstance.setBusinessId(200L);
        pendingInstance.setApplicantId(2L);
        pendingInstance.setCurrentNodeId(5L);
        pendingInstance.setStatus("PENDING");
        pendingInstance.setCreateTime(LocalDateTime.of(2026, 6, 1, 10, 0));

        recordEntity = new ApprovalRecordEntity();
        recordEntity.setId(1L);
        recordEntity.setInstanceId(100L);
        recordEntity.setNodeName("部门经理审批");
        recordEntity.setApproverId(1L);
        recordEntity.setAction("APPROVE");
        recordEntity.setComment("同意");
        recordEntity.setOperateTime("2026-06-01 14:00:00");

        definitionEntity = new ApprovalDefinitionEntity();
        definitionEntity.setId(10L);
        definitionEntity.setDefinitionName("请假审批流程");
        definitionEntity.setBusinessType("LEAVE");
        definitionEntity.setEnableFlag(true);
    }

    @AfterEach
    void tearDown() {
        stpMock.close();
    }

    // ==================== pageList - 待审(pending) ====================

    @Nested
    @DisplayName("pageList tab=pending - 待审批查询")
    class PendingTabTests {

        @Test
        @DisplayName("有待审批实例且当前用户未操作 → 返回分页数据")
        void shouldReturnPendingInstancesNotActedByUser() {
            IPage<ApprovalInstanceEntity> instancePage = new Page<>(1, 10, 1);
            instancePage.setRecords(List.of(pendingInstance));
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(instancePage);
            when(recordMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(definitionMapper.selectById(10L)).thenReturn(definitionEntity);

            MyApprovalQueryDTO query = new MyApprovalQueryDTO();
            query.setTab("pending");
            PageResult<MyApprovalVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(100L, result.getList().get(0).getInstanceId());
            assertEquals("请假审批流程", result.getList().get(0).getDefinitionName());
        }

        @Test
        @DisplayName("当前用户已操作过该实例 → 过滤掉不返回")
        void shouldFilterOutActedInstances() {
            IPage<ApprovalInstanceEntity> instancePage = new Page<>(1, 10, 2);
            ApprovalInstanceEntity another = new ApprovalInstanceEntity();
            another.setId(101L);
            another.setDefinitionId(10L);
            another.setBusinessType("LEAVE");
            another.setBusinessId(201L);
            another.setApplicantId(3L);
            another.setStatus("PENDING");
            another.setCreateTime(LocalDateTime.of(2026, 6, 2, 10, 0));
            instancePage.setRecords(List.of(pendingInstance, another));

            ApprovalRecordEntity actedRecord = new ApprovalRecordEntity();
            actedRecord.setInstanceId(100L);
            actedRecord.setApproverId(1L);

            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(instancePage);
            when(recordMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(List.of(actedRecord));
            when(definitionMapper.selectById(10L)).thenReturn(definitionEntity);

            MyApprovalQueryDTO query = new MyApprovalQueryDTO();
            query.setTab("pending");
            PageResult<MyApprovalVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(101L, result.getList().get(0).getInstanceId());
        }

        @Test
        @DisplayName("无待审批实例 → 返回空分页")
        void shouldReturnEmptyWhenNoPending() {
            IPage<ApprovalInstanceEntity> instancePage = new Page<>(1, 10, 0);
            instancePage.setRecords(Collections.emptyList());
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(instancePage);

            MyApprovalQueryDTO query = new MyApprovalQueryDTO();
            query.setTab("pending");
            PageResult<MyApprovalVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }
    }

    // ==================== pageList - 已审(reviewed) ====================

    @Nested
    @DisplayName("pageList tab=reviewed - 已审批查询")
    class ReviewedTabTests {

        @Test
        @DisplayName("有已审批记录 → 返回分页数据含操作信息")
        void shouldReturnReviewedRecords() {
            IPage<ApprovalRecordEntity> recordPage = new Page<>(1, 10, 1);
            recordPage.setRecords(List.of(recordEntity));
            when(recordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(recordPage);
            when(instanceMapper.selectBatchIds(anyList())).thenReturn(List.of(pendingInstance));
            when(definitionMapper.selectById(10L)).thenReturn(definitionEntity);

            MyApprovalQueryDTO query = new MyApprovalQueryDTO();
            query.setTab("reviewed");
            PageResult<MyApprovalVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals("APPROVE", result.getList().get(0).getMyAction());
            assertEquals("同意", result.getList().get(0).getMyComment());
            assertEquals("部门经理审批", result.getList().get(0).getCurrentNodeName());
        }

        @Test
        @DisplayName("无已审批记录 → 返回空分页")
        void shouldReturnEmptyWhenNoReviewed() {
            IPage<ApprovalRecordEntity> recordPage = new Page<>(1, 10, 0);
            recordPage.setRecords(Collections.emptyList());
            when(recordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(recordPage);

            MyApprovalQueryDTO query = new MyApprovalQueryDTO();
            query.setTab("reviewed");
            PageResult<MyApprovalVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
        }
    }

    // ==================== pageList - 我的申请(submitted) ====================

    @Nested
    @DisplayName("pageList tab=submitted - 我的申请查询")
    class SubmittedTabTests {

        @Test
        @DisplayName("有本人提交的实例 → 返回分页数据")
        void shouldReturnSubmittedInstances() {
            pendingInstance.setApplicantId(1L);
            IPage<ApprovalInstanceEntity> instancePage = new Page<>(1, 10, 1);
            instancePage.setRecords(List.of(pendingInstance));
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(instancePage);
            when(definitionMapper.selectById(10L)).thenReturn(definitionEntity);

            MyApprovalQueryDTO query = new MyApprovalQueryDTO();
            query.setTab("submitted");
            PageResult<MyApprovalVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getList().get(0).getApplicantId());
        }

        @Test
        @DisplayName("无本人提交的实例 → 返回空分页")
        void shouldReturnEmptyWhenNoSubmitted() {
            IPage<ApprovalInstanceEntity> instancePage = new Page<>(1, 10, 0);
            instancePage.setRecords(Collections.emptyList());
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(instancePage);

            MyApprovalQueryDTO query = new MyApprovalQueryDTO();
            query.setTab("submitted");
            PageResult<MyApprovalVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
        }
    }

    // ==================== 边界条件 ====================

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("tab为null → 默认使用pending")
        void shouldDefaultToPendingWhenTabNull() {
            IPage<ApprovalInstanceEntity> instancePage = new Page<>(1, 10, 1);
            instancePage.setRecords(List.of(pendingInstance));
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(instancePage);
            when(recordMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(definitionMapper.selectById(10L)).thenReturn(definitionEntity);

            MyApprovalQueryDTO query = new MyApprovalQueryDTO();
            PageResult<MyApprovalVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getList().size());
            verify(instanceMapper).selectPage(any(IPage.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("pageNum/pageSize为null → 使用默认值(1/10)")
        void shouldUseDefaultPagination() {
            IPage<ApprovalInstanceEntity> instancePage = new Page<>(1, 10, 1);
            instancePage.setRecords(List.of(pendingInstance));
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(instancePage);
            when(recordMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(definitionMapper.selectById(10L)).thenReturn(definitionEntity);

            MyApprovalQueryDTO query = new MyApprovalQueryDTO();
            query.setTab("pending");
            PageResult<MyApprovalVO> result = service.pageList(query);

            assertNotNull(result);
            verify(instanceMapper).selectPage(argThat(p ->
                    p.getCurrent() == 1 && p.getSize() == 10), any());
        }

        @Test
        @DisplayName("未识别的tab值 → 默认走pending逻辑")
        void shouldDefaultToPendingWhenTabUnknown() {
            IPage<ApprovalInstanceEntity> instancePage = new Page<>(1, 10, 1);
            instancePage.setRecords(List.of(pendingInstance));
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(instancePage);
            when(recordMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(definitionMapper.selectById(10L)).thenReturn(definitionEntity);

            MyApprovalQueryDTO query = new MyApprovalQueryDTO();
            query.setTab("unknown_value");
            PageResult<MyApprovalVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getList().size());
        }

        @Test
        @DisplayName("超大页码 → 返回空列表不抛异常")
        void shouldReturnEmptyForLargePageNum() {
            IPage<ApprovalInstanceEntity> instancePage = new Page<>(999, 10, 0);
            instancePage.setRecords(Collections.emptyList());
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(instancePage);

            MyApprovalQueryDTO query = new MyApprovalQueryDTO();
            query.setTab("submitted");
            query.setPageNum(999);
            PageResult<MyApprovalVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("定义不存在时definitionName为null但不报错")
        void shouldNotThrowWhenDefinitionMissing() {
            pendingInstance.setApplicantId(1L);
            IPage<ApprovalInstanceEntity> instancePage = new Page<>(1, 10, 1);
            instancePage.setRecords(List.of(pendingInstance));
            when(instanceMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(instancePage);
            when(definitionMapper.selectById(10L)).thenReturn(null);

            MyApprovalQueryDTO query = new MyApprovalQueryDTO();
            query.setTab("submitted");
            PageResult<MyApprovalVO> result = service.pageList(query);

            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertNull(result.getList().get(0).getDefinitionName());
        }
    }
}
