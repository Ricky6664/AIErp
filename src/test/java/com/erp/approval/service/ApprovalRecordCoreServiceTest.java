package com.erp.approval.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.approval.dto.RecordCountersignDTO;
import com.erp.approval.dto.RecordTransferDTO;
import com.erp.approval.dto.RecordUrgeDTO;
import com.erp.approval.entity.ApprovalInstanceEntity;
import com.erp.approval.entity.ApprovalRecordEntity;
import com.erp.approval.mapper.ApprovalInstanceMapper;
import com.erp.approval.mapper.ApprovalRecordMapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApprovalRecordCoreService 单元测试")
class ApprovalRecordCoreServiceTest {

    @Mock
    private ApprovalRecordMapper recordMapper;

    @Mock
    private ApprovalInstanceMapper instanceMapper;

    @InjectMocks
    private ApprovalRecordCoreService coreService;

    private ApprovalInstanceEntity instance;
    private ApprovalRecordEntity record;
    private MockedStatic<StpUtil> stpMock;

    @BeforeEach
    void setUp() {
        stpMock = mockStatic(StpUtil.class);
        stpMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);

        instance = new ApprovalInstanceEntity();
        instance.setId(1L);
        instance.setStatus("PENDING");

        record = new ApprovalRecordEntity();
        record.setId(50L);
        record.setInstanceId(1L);
        record.setNodeName("部门经理审批");
        record.setApproverId(2L);
        record.setAction("APPROVE");
    }

    @AfterEach
    void tearDown() {
        stpMock.close();
    }

    // ==================== transferAction ====================

    @Nested
    @DisplayName("transferAction - 审批转办")
    class TransferActionTests {

        @Test
        @DisplayName("正常转办 → 记录action变为TRANSFER，审批人变更")
        void shouldTransferSuccessfully() {
            RecordTransferDTO dto = new RecordTransferDTO();
            dto.setRecordId(50L);
            dto.setTargetApproverId(3L);
            dto.setComment("转给李四处理");

            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            when(recordMapper.updateById(any())).thenReturn(1);

            Long id = coreService.transferAction(dto);

            assertEquals(50L, id);
            verify(recordMapper).updateById(argThat(r ->
                    "TRANSFER".equals(r.getAction()) &&
                    Long.valueOf(3L).equals(r.getApproverId()) &&
                    "转给李四处理".equals(r.getComment()) &&
                    r.getOperateTime() != null
            ));
        }

        @Test
        @DisplayName("审批记录不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenRecordNotFound() {
            RecordTransferDTO dto = new RecordTransferDTO();
            dto.setRecordId(999L);
            dto.setTargetApproverId(3L);
            dto.setComment("转办原因");
            when(recordMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> coreService.transferAction(dto));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("实例状态不是PENDING → 抛出BusinessException(DATA_STATUS_INVALID)")
        void shouldThrowWhenInstanceNotPending() {
            instance.setStatus("APPROVED");
            RecordTransferDTO dto = new RecordTransferDTO();
            dto.setRecordId(50L);
            dto.setTargetApproverId(3L);
            dto.setComment("转办原因");
            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(instance);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> coreService.transferAction(dto));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("实例不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenInstanceNotFound() {
            RecordTransferDTO dto = new RecordTransferDTO();
            dto.setRecordId(50L);
            dto.setTargetApproverId(3L);
            dto.setComment("转办原因");
            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> coreService.transferAction(dto));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("转办comment超长(5000字符) → 正常保存")
        void shouldHandleVeryLongComment() {
            RecordTransferDTO dto = new RecordTransferDTO();
            dto.setRecordId(50L);
            dto.setTargetApproverId(3L);
            String longComment = "A".repeat(5000);
            dto.setComment(longComment);

            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            when(recordMapper.updateById(any())).thenReturn(1);

            coreService.transferAction(dto);

            verify(recordMapper).updateById(argThat(r ->
                    longComment.equals(r.getComment())
            ));
        }
    }

    // ==================== countersignAction ====================

    @Nested
    @DisplayName("countersignAction - 审批加签")
    class CountersignActionTests {

        @Test
        @DisplayName("正常加签 → 创建新记录，action为COUNTERSIGN")
        void shouldCountersignSuccessfully() {
            RecordCountersignDTO dto = new RecordCountersignDTO();
            dto.setRecordId(50L);
            dto.setCountersignApproverId(4L);
            dto.setComment("需要王五加签");

            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            doAnswer(inv -> {
                ApprovalRecordEntity r = inv.getArgument(0);
                r.setId(200L);
                return 1;
            }).when(recordMapper).insert(any());

            Long id = coreService.countersignAction(dto);

            assertEquals(200L, id);
            verify(recordMapper).insert(argThat(r ->
                    "COUNTERSIGN".equals(r.getAction()) &&
                    Long.valueOf(4L).equals(r.getApproverId()) &&
                    record.getInstanceId().equals(r.getInstanceId()) &&
                    record.getNodeName().equals(r.getNodeName())
            ));
        }

        @Test
        @DisplayName("审批记录不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenRecordNotFound() {
            RecordCountersignDTO dto = new RecordCountersignDTO();
            dto.setRecordId(999L);
            dto.setCountersignApproverId(4L);
            dto.setComment("加签原因");
            when(recordMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> coreService.countersignAction(dto));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("实例状态不是PENDING → 抛出BusinessException(DATA_STATUS_INVALID)")
        void shouldThrowWhenInstanceNotPending() {
            instance.setStatus("REJECTED");
            RecordCountersignDTO dto = new RecordCountersignDTO();
            dto.setRecordId(50L);
            dto.setCountersignApproverId(4L);
            dto.setComment("加签原因");
            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(instance);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> coreService.countersignAction(dto));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("加签comment为空 → 仍创建记录")
        void shouldAllowEmptyComment() {
            RecordCountersignDTO dto = new RecordCountersignDTO();
            dto.setRecordId(50L);
            dto.setCountersignApproverId(4L);
            dto.setComment(null);

            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            doAnswer(inv -> {
                ApprovalRecordEntity r = inv.getArgument(0);
                r.setId(201L);
                return 1;
            }).when(recordMapper).insert(any());

            Long id = coreService.countersignAction(dto);

            assertEquals(201L, id);
            verify(recordMapper).insert(any());
        }
    }

    // ==================== urgeAction ====================

    @Nested
    @DisplayName("urgeAction - 审批催办")
    class UrgeActionTests {

        @Test
        @DisplayName("正常催办(自定义消息) → 创建URGE记录")
        void shouldUrgeWithCustomMessage() {
            RecordUrgeDTO dto = new RecordUrgeDTO();
            dto.setRecordId(50L);
            dto.setMessage("请尽快审批");

            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            doAnswer(inv -> {
                ApprovalRecordEntity r = inv.getArgument(0);
                r.setId(300L);
                return 1;
            }).when(recordMapper).insert(any());

            Long id = coreService.urgeAction(dto);

            assertEquals(300L, id);
            verify(recordMapper).insert(argThat(r ->
                    "URGE".equals(r.getAction()) &&
                    "请尽快审批".equals(r.getComment())
            ));
        }

        @Test
        @DisplayName("催办消息为空 → 使用默认消息'催办提醒'")
        void shouldUseDefaultMessageWhenNull() {
            RecordUrgeDTO dto = new RecordUrgeDTO();
            dto.setRecordId(50L);
            dto.setMessage(null);

            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            when(recordMapper.insert(any())).thenReturn(1);

            coreService.urgeAction(dto);

            verify(recordMapper).insert(argThat(r ->
                    "催办提醒".equals(r.getComment())
            ));
        }

        @Test
        @DisplayName("审批记录不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenRecordNotFound() {
            RecordUrgeDTO dto = new RecordUrgeDTO();
            dto.setRecordId(999L);
            when(recordMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> coreService.urgeAction(dto));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("实例状态不是PENDING → 抛出BusinessException(DATA_STATUS_INVALID)")
        void shouldThrowWhenInstanceNotPending() {
            instance.setStatus("APPROVED");
            RecordUrgeDTO dto = new RecordUrgeDTO();
            dto.setRecordId(50L);
            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(instance);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> coreService.urgeAction(dto));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("催办消息超长(5000字符) → 正常保存")
        void shouldHandleVeryLongMessage() {
            RecordUrgeDTO dto = new RecordUrgeDTO();
            dto.setRecordId(50L);
            String longMsg = "催".repeat(5000);
            dto.setMessage(longMsg);

            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(instance);
            when(recordMapper.insert(any())).thenReturn(1);

            coreService.urgeAction(dto);

            verify(recordMapper).insert(argThat(r ->
                    longMsg.equals(r.getComment())
            ));
        }
    }

    // ==================== validateInstancePending ====================

    @Nested
    @DisplayName("validateInstancePending - 实例状态校验")
    class ValidateInstancePendingTests {

        @Test
        @DisplayName("实例为null → DATA_NOT_FOUND")
        void shouldThrowWhenInstanceNull() {
            RecordTransferDTO dto = new RecordTransferDTO();
            dto.setRecordId(50L);
            dto.setTargetApproverId(3L);
            dto.setComment("test");
            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> coreService.transferAction(dto));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("实例非PENDING → DATA_STATUS_INVALID，含REJECTED状态")
        void shouldThrowWhenRejected() {
            instance.setStatus("REJECTED");
            RecordCountersignDTO dto = new RecordCountersignDTO();
            dto.setRecordId(50L);
            dto.setCountersignApproverId(4L);
            dto.setComment("test");
            when(recordMapper.selectById(50L)).thenReturn(record);
            when(instanceMapper.selectById(1L)).thenReturn(instance);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> coreService.countersignAction(dto));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
        }
    }
}
