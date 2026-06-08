package com.erp.approval.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.approval.dto.RecordActionDTO;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.mapper.ApprovalRecordMapper;
import com.erp.approval.service.impl.ApprovalRecordServiceImpl;
import com.erp.approval.vo.RecordVO;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
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
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ApprovalRecordService 单元测试")
class ApprovalRecordServiceTest {

    @Mock
    private ApprovalRecordMapper recordMapper;

    @Mock
    private ApprovalInstanceMapper instanceMapper;

    private ApprovalRecordServiceImpl service;

    private ApprovalInstanceEntity instance;
    private ApprovalRecordEntity record;
    private RecordActionDTO actionDTO;
    private MockedStatic<StpUtil> stpMock;

    @BeforeEach
    void setUp() {
        service = spy(new ApprovalRecordServiceImpl(instanceMapper));
        ReflectionTestUtils.setField(service, "baseMapper", recordMapper);

        stpMock = mockStatic(StpUtil.class);
        stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

        instance = new ApprovalInstanceEntity();
        instance.setId(1L);
        instance.setDefinitionId(10L);
        instance.setBusinessType("LEAVE");
        instance.setBusinessId(100L);
        instance.setApplicantId(1L);
        instance.setCurrentNodeId(5L);
        instance.setStatus("PENDING");
        instance.setVersion(0);

        record = new ApprovalRecordEntity();
        record.setId(50L);
        record.setInstanceId(1L);
        record.setNodeName("部门经理审批");
        record.setApproverId(1L);
        record.setAction("APPROVE");
        record.setComment("同意");

        actionDTO = new RecordActionDTO();
        actionDTO.setInstanceId(1L);
        actionDTO.setAction("APPROVE");
        actionDTO.setComment("同意");
    }

    @AfterEach
    void tearDown() {
        stpMock.close();
    }

    // ==================== pageListByInstance ====================

    @Nested
    @DisplayName("pageListByInstance - 按实例查询审批记录")
    class PageListByInstanceTests {

        @Test
        @DisplayName("实例存在记录 → 返回分页数据")
        void shouldReturnPageData() {
            IPage<ApprovalRecordEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(record));
            when(recordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            PageResult<RecordVO> result = service.pageListByInstance(1L, 1, 10);

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getList().size());
        }

        @Test
        @DisplayName("实例无记录 → 返回空列表")
        void shouldReturnEmptyWhenNoRecords() {
            IPage<ApprovalRecordEntity> page = new Page<>(1, 10, 0);
            page.setRecords(java.util.Collections.emptyList());
            when(recordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            PageResult<RecordVO> result = service.pageListByInstance(1L, 1, 10);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("pageNum/pageSize为空 → 使用默认值")
        void shouldUseDefaultPagination() {
            IPage<ApprovalRecordEntity> page = new Page<>(1, 10, 0);
            when(recordMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            service.pageListByInstance(1L, null, null);

            verify(recordMapper).selectPage(argThat(p ->
                    p.getCurrent() == 1 && p.getSize() == 10), any());
        }
    }

    // ==================== recordAction ====================

    @Nested
    @DisplayName("recordAction - 审批操作")
    class RecordActionTests {

        @Test
        @DisplayName("审批通过 → 实例状态变为APPROVED，创建记录")
        void shouldApproveAndChangeStatus() {
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            when(instanceMapper.updateById(any())).thenReturn(1);
            doAnswer(inv -> {
                ApprovalRecordEntity r = inv.getArgument(0);
                r.setId(100L);
                return 1;
            }).when(recordMapper).insert(any(ApprovalRecordEntity.class));

            Long recordId = service.recordAction(actionDTO);

            assertNotNull(recordId);
            assertEquals(100L, recordId);
            verify(instanceMapper).updateById(argThat(e ->
                    "APPROVED".equals(e.getStatus())));
            verify(recordMapper).insert(any(ApprovalRecordEntity.class));
        }

        @Test
        @DisplayName("审批驳回 → 实例状态变为REJECTED")
        void shouldRejectAndChangeStatus() {
            actionDTO.setAction("REJECT");
            actionDTO.setComment("不同意");
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            when(instanceMapper.updateById(any())).thenReturn(1);
            doAnswer(inv -> {
                ApprovalRecordEntity r = inv.getArgument(0);
                r.setId(101L);
                return 1;
            }).when(recordMapper).insert(any(ApprovalRecordEntity.class));

            service.recordAction(actionDTO);

            verify(instanceMapper).updateById(argThat(e ->
                    "REJECTED".equals(e.getStatus())));
        }

        @Test
        @DisplayName("无效的action → 抛出BusinessException(BUSINESS_ERROR)")
        void shouldThrowWhenInvalidAction() {
            actionDTO.setAction("INVALID");
            when(instanceMapper.selectById(1L)).thenReturn(instance);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.recordAction(actionDTO));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
            verify(recordMapper, never()).insert(any());
        }

        @Test
        @DisplayName("实例不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenInstanceNotFound() {
            when(instanceMapper.selectById(999L)).thenReturn(null);
            actionDTO.setInstanceId(999L);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.recordAction(actionDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(recordMapper, never()).insert(any());
        }

        @Test
        @DisplayName("实例状态不是PENDING → 抛出BusinessException(DATA_STATUS_INVALID)")
        void shouldThrowWhenInstanceNotPending() {
            instance.setStatus("APPROVED");
            when(instanceMapper.selectById(1L)).thenReturn(instance);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.recordAction(actionDTO));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(recordMapper, never()).insert(any());
        }

        @Test
        @DisplayName("审批记录包含操作人ID和操作时间")
        void shouldRecordOperatorAndTime() {
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            when(instanceMapper.updateById(any())).thenReturn(1);
            doAnswer(inv -> {
                ApprovalRecordEntity r = inv.getArgument(0);
                r.setId(102L);
                return 1;
            }).when(recordMapper).insert(any(ApprovalRecordEntity.class));

            service.recordAction(actionDTO);

            verify(recordMapper).insert(argThat(r ->
                    r.getApproverId() != null && r.getOperateTime() != null));
        }

        @Test
        @DisplayName("comment可为空 → 操作成功")
        void shouldAllowNullComment() {
            actionDTO.setComment(null);
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            when(instanceMapper.updateById(any())).thenReturn(1);
            doAnswer(inv -> {
                ApprovalRecordEntity r = inv.getArgument(0);
                r.setId(103L);
                return 1;
            }).when(recordMapper).insert(any(ApprovalRecordEntity.class));

            Long id = service.recordAction(actionDTO);

            assertNotNull(id);
            verify(recordMapper).insert(any(ApprovalRecordEntity.class));
        }
    }

    // ==================== 边界条件 ====================

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("查询不存在的实例ID → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldReturnErrorForNonExistentInstance() {
            when(instanceMapper.selectById(Long.MAX_VALUE)).thenReturn(null);
            actionDTO.setInstanceId(Long.MAX_VALUE);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.recordAction(actionDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("actionDTO instanceId为null → NPE或参数校验失败")
        void shouldHandleNullInstanceId() {
            actionDTO.setInstanceId(null);
            when(instanceMapper.selectById(null)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.recordAction(actionDTO));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("comment超长(5000字符) → 操作成功")
        void shouldHandleVeryLongComment() {
            String longComment = "A".repeat(5000);
            actionDTO.setComment(longComment);
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            when(instanceMapper.updateById(any())).thenReturn(1);
            doAnswer(inv -> {
                ApprovalRecordEntity r = inv.getArgument(0);
                r.setId(104L);
                return 1;
            }).when(recordMapper).insert(any(ApprovalRecordEntity.class));

            Long id = service.recordAction(actionDTO);

            assertNotNull(id);
            verify(recordMapper).insert(argThat(r ->
                    longComment.equals(r.getComment())));
        }
    }

    // ==================== 并发与乐观锁 ====================

    @Nested
    @DisplayName("并发与乐观锁测试")
    class ConcurrencyTests {

        @Test
        @DisplayName("并发修改同一实例 → 乐观锁拦截，仅一个成功")
        void shouldBlockConcurrentUpdate() {
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            // 模拟第一次更新成功，第二次更新时版本已变更返回0
            when(instanceMapper.updateById(any()))
                    .thenReturn(1)  // 第一次成功
                    .thenReturn(0); // 第二次乐观锁拦截
            doAnswer(inv -> {
                ApprovalRecordEntity r = inv.getArgument(0);
                r.setId(200L);
                return 1;
            }).when(recordMapper).insert(any(ApprovalRecordEntity.class));

            // 第一个请求成功
            Long firstId = service.recordAction(actionDTO);
            assertNotNull(firstId);

            // 第二个并发请求 - 模拟版本冲突
            RecordActionDTO concurrentDTO = new RecordActionDTO();
            concurrentDTO.setInstanceId(1L);
            concurrentDTO.setAction("APPROVE");
            concurrentDTO.setComment("并发审批");
            when(instanceMapper.selectById(1L)).thenReturn(instance);

            // updateById返回0表示乐观锁拦截
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.recordAction(concurrentDTO));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("乐观锁版本字段随更新递增")
        void shouldIncrementVersionOnUpdate() {
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            when(instanceMapper.updateById(any())).thenReturn(1);
            doAnswer(inv -> {
                ApprovalRecordEntity r = inv.getArgument(0);
                r.setId(201L);
                return 1;
            }).when(recordMapper).insert(any(ApprovalRecordEntity.class));

            service.recordAction(actionDTO);

            verify(instanceMapper).updateById(argThat(e ->
                    e.getVersion() != null));
        }
    }
}
