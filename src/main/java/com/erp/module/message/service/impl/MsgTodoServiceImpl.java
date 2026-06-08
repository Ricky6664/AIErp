package com.erp.module.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.common.utils.SecurityUtils;
import com.erp.module.message.dto.TodoQueryDTO;
import com.erp.module.message.entity.MsgTodoEntity;
import com.erp.module.message.event.TodoApprovedEvent;
import com.erp.module.message.mapper.MsgTodoMapper;
import com.erp.module.message.service.AuditEngineService;
import com.erp.module.message.service.MsgTodoService;
import com.erp.module.message.service.MsgWebSocketService;
import com.erp.module.message.vo.TodoListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 单据待办Service实现.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MsgTodoServiceImpl implements MsgTodoService {

    private final MsgTodoMapper todoMapper;
    private final MsgWebSocketService webSocketService;
    private final AuditEngineService auditEngineService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public PageResult<TodoListVO> getTodoList(TodoQueryDTO query) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        query.setAssigneeId(currentUserId);

        LambdaQueryWrapper<MsgTodoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MsgTodoEntity::getAssigneeId, currentUserId);
        wrapper.eq(MsgTodoEntity::getIsCompleted, false);
        wrapper.like(StringUtils.hasText(query.getKeyword()),
                MsgTodoEntity::getTodoTitle, query.getKeyword());
        wrapper.eq(StringUtils.hasText(query.getBusinessType()),
                MsgTodoEntity::getBusinessType, query.getBusinessType());
        wrapper.orderByDesc(MsgTodoEntity::getCreateTime);

        int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
        IPage<MsgTodoEntity> page = todoMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);

        return PageResult.of(page).convert(this::toListVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveTodo(Long todoId, String opinion) {
        MsgTodoEntity todo = todoMapper.selectById(todoId);
        validateTodoOperable(todo);

        auditEngineService.approve(todo.getBusinessType(), todo.getBusinessId(), opinion);

        todo.setIsCompleted(true);
        todo.setCompleteTime(LocalDateTime.now());
        todoMapper.updateById(todo);

        eventPublisher.publishEvent(new TodoApprovedEvent(
                todo.getBusinessType(), todo.getBusinessId(), todoId));

        webSocketService.pushToUser(todo.getCreatedBy(), buildTodoDoneMessage(todo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectTodo(Long todoId, String reason) {
        if (!StringUtils.hasText(reason)) {
            throw new BusinessException(ErrorCode.PARAM_MISSING);
        }

        MsgTodoEntity todo = todoMapper.selectById(todoId);
        validateTodoOperable(todo);

        auditEngineService.reject(todo.getBusinessType(), todo.getBusinessId(), reason);

        todo.setIsCompleted(true);
        todo.setCompleteTime(LocalDateTime.now());
        todoMapper.updateById(todo);

        webSocketService.pushToUser(todo.getCreatedBy(), buildTodoRejectMessage(todo, reason));
    }

    private void validateTodoOperable(MsgTodoEntity todo) {
        if (todo == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(todo.getIsCompleted())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID);
        }
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!todo.getAssigneeId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private TodoListVO toListVO(MsgTodoEntity entity) {
        TodoListVO vo = new TodoListVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private String buildTodoDoneMessage(MsgTodoEntity todo) {
        return String.format("您的待办[%s]已被处理完成", todo.getTodoTitle());
    }

    private String buildTodoRejectMessage(MsgTodoEntity todo, String reason) {
        return String.format("您的待办[%s]已被驳回，原因：%s", todo.getTodoTitle(), reason);
    }
}
