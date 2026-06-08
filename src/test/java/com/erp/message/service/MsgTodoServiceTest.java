package com.erp.message.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.message.dto.TodoQueryDTO;
import com.erp.module.message.entity.MsgTodoEntity;
import com.erp.module.message.mapper.MsgTodoMapper;
import com.erp.module.message.service.AuditEngineService;
import com.erp.module.message.service.MsgWebSocketService;
import com.erp.module.message.service.impl.MsgTodoServiceImpl;
import com.erp.module.message.vo.TodoListVO;
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
import org.springframework.context.ApplicationEventPublisher;

import com.erp.common.utils.SecurityUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MsgTodoService 单元测试")
class MsgTodoServiceTest {

    @Mock
    private MsgTodoMapper todoMapper;
    @Mock
    private MsgWebSocketService webSocketService;
    @Mock
    private AuditEngineService auditEngineService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private MsgTodoServiceImpl service;
    private MsgTodoEntity entity;
    private TodoQueryDTO queryDTO;

    @BeforeEach
    void setUp() {
        service = new MsgTodoServiceImpl(todoMapper, webSocketService, auditEngineService, eventPublisher);

        entity = new MsgTodoEntity();
        entity.setId(1L);
        entity.setTodoTitle("测试待办");
        entity.setBusinessType("PURCHASE_ORDER");
        entity.setBusinessId(100L);
        entity.setAssigneeId(1L);
        entity.setCreatedBy(2L);
        entity.setIsCompleted(false);
        entity.setVersion(0);

        queryDTO = new TodoQueryDTO();
    }

    // ==================== getTodoList ====================

    @Nested
    @DisplayName("getTodoList - 分页查询当前用户待办")
    class GetTodoListTests {

        @Test
        @DisplayName("有待办数据 → 返回正确分页数据")
        void shouldReturnPageDataWhenHasTodos() {
            IPage<MsgTodoEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(todoMapper.selectPage(any(IPage.class), any())).thenReturn(page);

            PageResult<TodoListVO> result = service.getTodoList(queryDTO);

            assertNotNull(result);
            assertEquals(1L, result.getTotal());
            assertEquals(1, result.getList().size());
            assertEquals("测试待办", result.getList().get(0).getTodoTitle());
            verify(todoMapper).selectPage(any(IPage.class), any());
        }

        @Test
        @DisplayName("无待办数据 → 返回空分页")
        void shouldReturnEmptyPageWhenNoTodos() {
            IPage<MsgTodoEntity> page = new Page<>(1, 10, 0);
            when(todoMapper.selectPage(any(IPage.class), any())).thenReturn(page);

            PageResult<TodoListVO> result = service.getTodoList(queryDTO);

            assertNotNull(result);
            assertEquals(0L, result.getTotal());
            assertTrue(result.getList().isEmpty());
        }

        @Test
        @DisplayName("keyword过滤 → 条件生效")
        void shouldFilterByKeyword() {
            queryDTO.setKeyword("测试");
            IPage<MsgTodoEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(todoMapper.selectPage(any(IPage.class), any())).thenReturn(page);

            PageResult<TodoListVO> result = service.getTodoList(queryDTO);

            assertNotNull(result);
            verify(todoMapper).selectPage(any(IPage.class), any());
        }

        @Test
        @DisplayName("businessType过滤 → 条件生效")
        void shouldFilterByBusinessType() {
            queryDTO.setBusinessType("PURCHASE_ORDER");
            IPage<MsgTodoEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(todoMapper.selectPage(any(IPage.class), any())).thenReturn(page);

            PageResult<TodoListVO> result = service.getTodoList(queryDTO);

            assertNotNull(result);
            verify(todoMapper).selectPage(any(IPage.class), any());
        }

        @Test
        @DisplayName("pageNum/pageSize为空 → 使用默认值(1/10)")
        void shouldUseDefaultPagination() {
            IPage<MsgTodoEntity> page = new Page<>(1, 10, 1);
            page.setRecords(java.util.List.of(entity));
            when(todoMapper.selectPage(any(IPage.class), any())).thenReturn(page);

            service.getTodoList(queryDTO);

            verify(todoMapper).selectPage(any(IPage.class), any());
        }

        @Test
        @DisplayName("多页数据 → pageNum和pageSize正确传递")
        void shouldPassCustomPagination() {
            queryDTO.setPageNum(3);
            queryDTO.setPageSize(5);
            IPage<MsgTodoEntity> page = new Page<>(3, 5, 25);
            page.setRecords(java.util.List.of(entity));
            when(todoMapper.selectPage(any(IPage.class), any())).thenReturn(page);

            PageResult<TodoListVO> result = service.getTodoList(queryDTO);

            assertNotNull(result);
            verify(todoMapper).selectPage(any(IPage.class), any());
        }
    }

    // ==================== approveTodo ====================

    @Nested
    @DisplayName("approveTodo - 审批通过待办")
    class ApproveTodoTests {

        @Test
        @DisplayName("待办存在且未完成 → 审批通过，标记完成，推送通知")
        void shouldApproveWhenValid() {
            when(todoMapper.selectById(1L)).thenReturn(entity);
            when(todoMapper.updateById(any(MsgTodoEntity.class))).thenReturn(1);

            service.approveTodo(1L, "同意");

            verify(todoMapper).selectById(1L);
            verify(auditEngineService).approve("PURCHASE_ORDER", 100L, "同意");
            verify(eventPublisher).publishEvent(any());
            verify(webSocketService).pushToUser(eq(2L), anyString());
            assertTrue(entity.getIsCompleted());
            assertNotNull(entity.getCompleteTime());
        }

        @Test
        @DisplayName("待办不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenTodoNotFound() {
            when(todoMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.approveTodo(999L, "同意"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(todoMapper, never()).updateById(any());
            verify(auditEngineService, never()).approve(anyString(), anyLong(), anyString());
        }

        @Test
        @DisplayName("待办已完成 → 抛出BusinessException(DATA_STATUS_INVALID)")
        void shouldThrowWhenAlreadyCompleted() {
            entity.setIsCompleted(true);
            when(todoMapper.selectById(1L)).thenReturn(entity);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.approveTodo(1L, "同意"));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(todoMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("非本人待办(assignee不匹配) → 抛出BusinessException(FORBIDDEN)")
        void shouldThrowWhenNotAssignee() {
            try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
                mocked.when(SecurityUtils::getCurrentUserId).thenReturn(999L);
                when(todoMapper.selectById(1L)).thenReturn(entity);

                BusinessException ex = assertThrows(BusinessException.class,
                        () -> service.approveTodo(1L, "同意"));
                assertEquals(ErrorCode.FORBIDDEN.getCode(), ex.getCode());
                verify(todoMapper, never()).updateById(any());
            }
        }

        @Test
        @DisplayName("opinion为空字符串 → 审批通过(opinion非必填)")
        void shouldApproveWithEmptyOpinion() {
            when(todoMapper.selectById(1L)).thenReturn(entity);
            when(todoMapper.updateById(any(MsgTodoEntity.class))).thenReturn(1);

            assertDoesNotThrow(() -> service.approveTodo(1L, ""));
            verify(auditEngineService).approve("PURCHASE_ORDER", 100L, "");
        }
    }

    // ==================== rejectTodo ====================

    @Nested
    @DisplayName("rejectTodo - 驳回待办")
    class RejectTodoTests {

        @Test
        @DisplayName("待办存在且未完成且有原因 → 驳回成功，标记完成")
        void shouldRejectWhenValid() {
            when(todoMapper.selectById(1L)).thenReturn(entity);
            when(todoMapper.updateById(any(MsgTodoEntity.class))).thenReturn(1);

            service.rejectTodo(1L, "不符合要求");

            verify(todoMapper).selectById(1L);
            verify(auditEngineService).reject("PURCHASE_ORDER", 100L, "不符合要求");
            verify(webSocketService).pushToUser(eq(2L), anyString());
            assertTrue(entity.getIsCompleted());
            assertNotNull(entity.getCompleteTime());
        }

        @Test
        @DisplayName("reason为空 → 抛出BusinessException(PARAM_MISSING)")
        void shouldThrowWhenReasonBlank() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.rejectTodo(1L, ""));
            assertEquals(ErrorCode.PARAM_MISSING.getCode(), ex.getCode());
            verify(todoMapper, never()).selectById(anyLong());
            verify(auditEngineService, never()).reject(anyString(), anyLong(), anyString());
        }

        @Test
        @DisplayName("reason为null → 抛出BusinessException(PARAM_MISSING)")
        void shouldThrowWhenReasonNull() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.rejectTodo(1L, null));
            assertEquals(ErrorCode.PARAM_MISSING.getCode(), ex.getCode());
            verify(todoMapper, never()).selectById(anyLong());
        }

        @Test
        @DisplayName("待办不存在 → 抛出BusinessException(DATA_NOT_FOUND)")
        void shouldThrowWhenTodoNotFound() {
            when(todoMapper.selectById(999L)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.rejectTodo(999L, "不符合要求"));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            verify(auditEngineService, never()).reject(anyString(), anyLong(), anyString());
        }

        @Test
        @DisplayName("待办已完成 → 抛出BusinessException(DATA_STATUS_INVALID)")
        void shouldThrowWhenAlreadyCompleted() {
            entity.setIsCompleted(true);
            when(todoMapper.selectById(1L)).thenReturn(entity);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.rejectTodo(1L, "不符合要求"));
            assertEquals(ErrorCode.DATA_STATUS_INVALID.getCode(), ex.getCode());
            verify(todoMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("非本人待办 → 抛出BusinessException(FORBIDDEN)")
        void shouldThrowWhenNotAssignee() {
            try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
                mocked.when(SecurityUtils::getCurrentUserId).thenReturn(999L);
                when(todoMapper.selectById(1L)).thenReturn(entity);

                BusinessException ex = assertThrows(BusinessException.class,
                        () -> service.rejectTodo(1L, "不符合要求"));
                assertEquals(ErrorCode.FORBIDDEN.getCode(), ex.getCode());
                verify(todoMapper, never()).updateById(any());
            }
        }
    }
}
