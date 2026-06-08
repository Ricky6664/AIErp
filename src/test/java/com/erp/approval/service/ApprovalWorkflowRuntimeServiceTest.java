package com.erp.approval.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.approval.mapper.ApprovalDefinitionMapper;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.mapper.ApprovalRecordMapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ApprovalWorkflowRuntimeService 单元测试")
class ApprovalWorkflowRuntimeServiceTest {

    @Mock
    private ApprovalDefinitionMapper definitionMapper;
    @Mock
    private ApprovalInstanceMapper instanceMapper;
    @Mock
    private ApprovalRecordMapper recordMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ApprovalWorkflowRuntimeService service;
    private MockedStatic<StpUtil> stpUtilMock;

    private ApprovalDefinitionEntity definition;
    private ApprovalInstanceEntity instance;

    @BeforeEach
    void setUp() {
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

        service = new ApprovalWorkflowRuntimeService(
                definitionMapper, instanceMapper, recordMapper, objectMapper);

        definition = new ApprovalDefinitionEntity();
        definition.setId(10L);
        definition.setDefinitionName("测试流程");
        definition.setDefinitionCode("TEST_FLOW");
        definition.setBusinessType("LEAVE");
        definition.setFlowConfig("{\"nodes\":[{\"order\":1,\"name\":\"部门经理\"},{\"order\":2,\"name\":\"总监\"}]}");
        definition.setEnableFlag(true);

        instance = new ApprovalInstanceEntity();
        instance.setId(100L);
        instance.setDefinitionId(10L);
        instance.setBusinessType("LEAVE");
        instance.setBusinessId(200L);
        instance.setApplicantId(1L);
        instance.setCurrentNodeId(1L);
        instance.setStatus("PENDING");
    }

    @AfterEach
    void tearDown() {
        stpUtilMock.close();
    }

    // ==================== startWorkflow ====================

    @Nested
    @DisplayName("startWorkflow - 启动工作流")
    class StartWorkflowTests {

        @Test
        @DisplayName("定义存在且启用 → 返回实例ID")
        void shouldStartWorkflowWhenDefinitionValid() {
            when(definitionMapper.selectById(10L)).thenReturn(definition);
            doAnswer(inv -> {
                ApprovalInstanceEntity e = inv.getArgument(0);
                e.setId(100L);
                return 1;
            }).when(instanceMapper).insert(any(ApprovalInstanceEntity.class));

            Long instanceId = service.startWorkflow(10L, 200L, "LEAVE");

            assertNotNull(instanceId);
            assertEquals(100L, instanceId);
            verify(instanceMapper).insert(argThat(e ->
                    "PENDING".equals(e.getStatus()) && e.getApplicantId().equals(1L)));
        }

        @Test
        @DisplayName("定义不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenDefinitionNotFound() {
            when(definitionMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.startWorkflow(999L, 200L, "LEAVE"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(instanceMapper, never()).insert(any());
        }

        @Test
        @DisplayName("定义已禁用 → 抛出BusinessException(DATA_STATUS_INVALID)")
        void shouldThrowWhenDefinitionDisabled() {
            definition.setEnableFlag(false);
            when(definitionMapper.selectById(10L)).thenReturn(definition);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.startWorkflow(10L, 200L, "LEAVE"));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(instanceMapper, never()).insert(any());
        }
    }

    // ==================== advanceWorkflow ====================

    @Nested
    @DisplayName("advanceWorkflow - 推进工作流")
    class AdvanceWorkflowTests {

        @Test
        @DisplayName("审批通过(非最后节点) → 推进到下一节点")
        void shouldAdvanceToNextNodeOnApprove() {
            when(instanceMapper.selectById(100L)).thenReturn(instance);
            when(definitionMapper.selectById(10L)).thenReturn(definition);
            when(recordMapper.insert(any(ApprovalRecordEntity.class))).thenReturn(1);
            when(instanceMapper.updateById(any())).thenReturn(1);

            service.advanceWorkflow(100L, "APPROVE", "同意");

            verify(instanceMapper).updateById(argThat(e ->
                    e.getCurrentNodeId() != null && e.getCurrentNodeId().equals(2L)));
            verify(recordMapper).insert(any(ApprovalRecordEntity.class));
        }

        @Test
        @DisplayName("审批通过(最后节点) → 状态变为APPROVED")
        void shouldSetApprovedOnLastNode() {
            instance.setCurrentNodeId(2L);
            when(instanceMapper.selectById(100L)).thenReturn(instance);
            when(definitionMapper.selectById(10L)).thenReturn(definition);
            when(recordMapper.insert(any(ApprovalRecordEntity.class))).thenReturn(1);
            when(instanceMapper.updateById(any())).thenReturn(1);

            service.advanceWorkflow(100L, "APPROVE", "同意");

            verify(instanceMapper).updateById(argThat(e ->
                    "APPROVED".equals(e.getStatus())));
        }

        @Test
        @DisplayName("驳回 → 状态变为REJECTED")
        void shouldSetRejectedOnRejectAction() {
            when(instanceMapper.selectById(100L)).thenReturn(instance);
            when(definitionMapper.selectById(10L)).thenReturn(definition);
            when(recordMapper.insert(any(ApprovalRecordEntity.class))).thenReturn(1);
            when(instanceMapper.updateById(any())).thenReturn(1);

            service.advanceWorkflow(100L, "REJECT", "不同意");

            verify(instanceMapper).updateById(argThat(e ->
                    "REJECTED".equals(e.getStatus())));
        }

        @Test
        @DisplayName("实例不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenInstanceNotFound() {
            when(instanceMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.advanceWorkflow(999L, "APPROVE", "同意"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("实例状态非PENDING → 抛出BusinessException(DATA_STATUS_INVALID)")
        void shouldThrowWhenInstanceNotPending() {
            instance.setStatus("APPROVED");
            when(instanceMapper.selectById(100L)).thenReturn(instance);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.advanceWorkflow(100L, "APPROVE", "同意"));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
        }
    }

    // ==================== completeWorkflow ====================

    @Nested
    @DisplayName("completeWorkflow - 完成工作流")
    class CompleteWorkflowTests {

        @Test
        @DisplayName("正常完成 → 状态变为APPROVED并记录")
        void shouldCompleteWorkflow() {
            when(instanceMapper.selectById(100L)).thenReturn(instance);
            when(recordMapper.insert(any(ApprovalRecordEntity.class))).thenReturn(1);
            when(instanceMapper.updateById(any())).thenReturn(1);

            service.completeWorkflow(100L);

            verify(instanceMapper).updateById(argThat(e ->
                    "APPROVED".equals(e.getStatus())));
            verify(recordMapper).insert(argThat(r ->
                    "COMPLETE".equals(r.getAction())));
        }
    }

    // ==================== rejectWorkflow ====================

    @Nested
    @DisplayName("rejectWorkflow - 驳回工作流")
    class RejectWorkflowTests {

        @Test
        @DisplayName("正常驳回 → 状态变为REJECTED")
        void shouldRejectWorkflow() {
            when(instanceMapper.selectById(100L)).thenReturn(instance);
            when(recordMapper.insert(any(ApprovalRecordEntity.class))).thenReturn(1);
            when(instanceMapper.updateById(any())).thenReturn(1);

            service.rejectWorkflow(100L, "不符合要求");

            verify(instanceMapper).updateById(argThat(e ->
                    "REJECTED".equals(e.getStatus())));
            verify(recordMapper).insert(argThat(r ->
                    "REJECT".equals(r.getAction()) && "不符合要求".equals(r.getComment())));
        }
    }

    // ==================== withdrawWorkflow ====================

    @Nested
    @DisplayName("withdrawWorkflow - 撤回工作流")
    class WithdrawWorkflowTests {

        @Test
        @DisplayName("申请人撤回PENDING实例 → 状态变为WITHDRAWN")
        void shouldWithdrawByApplicant() {
            when(instanceMapper.selectById(100L)).thenReturn(instance);
            when(recordMapper.insert(any(ApprovalRecordEntity.class))).thenReturn(1);
            when(instanceMapper.updateById(any())).thenReturn(1);

            service.withdrawWorkflow(100L);

            verify(instanceMapper).updateById(argThat(e ->
                    "WITHDRAWN".equals(e.getStatus())));
        }

        @Test
        @DisplayName("非申请人撤回 → 抛出BusinessException(BUSINESS_ERROR)")
        void shouldThrowWhenNotApplicant() {
            instance.setApplicantId(2L);
            when(instanceMapper.selectById(100L)).thenReturn(instance);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.withdrawWorkflow(100L));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("非PENDING状态撤回 → 抛出BusinessException(DATA_STATUS_INVALID)")
        void shouldThrowWhenNotPending() {
            instance.setStatus("APPROVED");
            when(instanceMapper.selectById(100L)).thenReturn(instance);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.withdrawWorkflow(100L));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("实例不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenInstanceNotFound() {
            when(instanceMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.withdrawWorkflow(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    // ==================== getWorkflowState ====================

    @Nested
    @DisplayName("getWorkflowState - 查询工作流状态")
    class GetWorkflowStateTests {

        @Test
        @DisplayName("实例存在 → 返回实例对象")
        void shouldReturnInstanceWhenExists() {
            when(instanceMapper.selectById(100L)).thenReturn(instance);

            ApprovalInstanceEntity result = service.getWorkflowState(100L);

            assertNotNull(result);
            assertEquals(100L, result.getId());
            assertEquals("PENDING", result.getStatus());
        }

        @Test
        @DisplayName("实例不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenNotFound() {
            when(instanceMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.getWorkflowState(999L));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    // ==================== 边界条件 ====================

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("空flowConfig → 节点列表为空，正常推进")
        void shouldHandleNullFlowConfig() {
            definition.setFlowConfig(null);
            instance.setCurrentNodeId(null);
            when(instanceMapper.selectById(100L)).thenReturn(instance);
            when(definitionMapper.selectById(10L)).thenReturn(definition);
            when(recordMapper.insert(any(ApprovalRecordEntity.class))).thenReturn(1);
            when(instanceMapper.updateById(any())).thenReturn(1);

            assertDoesNotThrow(() -> service.advanceWorkflow(100L, "APPROVE", "同意"));
        }

        @Test
        @DisplayName("空字符串flowConfig → 正常处理不抛异常")
        void shouldHandleEmptyFlowConfig() {
            definition.setFlowConfig("");
            instance.setCurrentNodeId(null);
            when(instanceMapper.selectById(100L)).thenReturn(instance);
            when(definitionMapper.selectById(10L)).thenReturn(definition);
            when(recordMapper.insert(any(ApprovalRecordEntity.class))).thenReturn(1);
            when(instanceMapper.updateById(any())).thenReturn(1);

            assertDoesNotThrow(() -> service.advanceWorkflow(100L, "APPROVE", "同意"));
        }

        @Test
        @DisplayName("非法flowConfig JSON → 返回空列表不抛异常")
        void shouldHandleInvalidFlowConfigJson() {
            definition.setFlowConfig("{invalid json");
            instance.setCurrentNodeId(null);
            when(instanceMapper.selectById(100L)).thenReturn(instance);
            when(definitionMapper.selectById(10L)).thenReturn(definition);
            when(recordMapper.insert(any(ApprovalRecordEntity.class))).thenReturn(1);
            when(instanceMapper.updateById(any())).thenReturn(1);

            assertDoesNotThrow(() -> service.advanceWorkflow(100L, "APPROVE", "同意"));
        }

        @Test
        @DisplayName("无nodes字段的flowConfig → 返回空列表")
        void shouldHandleFlowConfigWithoutNodes() {
            definition.setFlowConfig("{\"otherField\":\"value\"}");
            instance.setCurrentNodeId(null);
            when(instanceMapper.selectById(100L)).thenReturn(instance);
            when(definitionMapper.selectById(10L)).thenReturn(definition);
            when(recordMapper.insert(any(ApprovalRecordEntity.class))).thenReturn(1);
            when(instanceMapper.updateById(any())).thenReturn(1);

            assertDoesNotThrow(() -> service.advanceWorkflow(100L, "APPROVE", "同意"));
        }
    }

    // ==================== 并发场景 ====================

    @Nested
    @DisplayName("并发场景测试")
    class ConcurrencyTests {

        @Test
        @DisplayName("并发修改同一实例 → 乐观锁拦截传播异常")
        void shouldPropagateOptimisticLockOnConcurrentUpdate() {
            when(instanceMapper.selectById(100L)).thenReturn(instance);
            when(definitionMapper.selectById(10L)).thenReturn(definition);
            when(recordMapper.insert(any(ApprovalRecordEntity.class))).thenReturn(1);
            when(instanceMapper.updateById(any()))
                    .thenThrow(new RuntimeException("Optimistic lock conflict: version mismatch"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.advanceWorkflow(100L, "APPROVE", "同意"));
            assertTrue(ex.getMessage().contains("Optimistic lock"));
        }
    }
}
