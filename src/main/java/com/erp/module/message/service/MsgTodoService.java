package com.erp.module.message.service;

import com.erp.common.result.PageResult;
import com.erp.module.message.dto.TodoQueryDTO;
import com.erp.module.message.vo.TodoListVO;

/**
 * 单据待办Service接口.
 *
 * @author AI
 */
public interface MsgTodoService {

    PageResult<TodoListVO> getTodoList(TodoQueryDTO query);

    void approveTodo(Long todoId, String opinion);

    void rejectTodo(Long todoId, String reason);
}
