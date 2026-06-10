package com.erp.module.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import com.erp.module.message.dto.TodoQueryDTO;
import com.erp.module.message.service.MsgTodoService;
import com.erp.module.message.vo.TodoListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 待办接口Controller.
 *
 * @author AI
 */
@RestController
@RequestMapping("/api/message/todo")
@RequiredArgsConstructor
@Tag(name = "单据待办管理", description = "待办列表查询、审批与驳回接口")
public class MsgTodoController {

    private final MsgTodoService todoService;

    @Operation(summary = "分页查询当前用户待办列表")
    @GetMapping
    @SaCheckPermission("message:todo:query")
    public RT<PageResult<TodoListVO>> getTodoList(TodoQueryDTO query) {
        return RT.ok(todoService.getTodoList(query));
    }

    @Operation(summary = "审批通过待办")
    @PostMapping("/{id}/approve")
    @SaCheckPermission("message:todo:approve")
    public RT<Void> approveTodo(
            @Parameter(description = "待办ID") @PathVariable Long id,
            @Parameter(description = "审批意见") @RequestBody Map<String, String> body) {
        String opinion = body.getOrDefault("opinion", "");
        todoService.approveTodo(id, opinion);
        return RT.ok();
    }

    @Operation(summary = "驳回待办")
    @PostMapping("/{id}/reject")
    @SaCheckPermission("message:todo:reject")
    public RT<Void> rejectTodo(
            @Parameter(description = "待办ID") @PathVariable Long id,
            @Parameter(description = "驳回原因") @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "");
        todoService.rejectTodo(id, reason);
        return RT.ok();
    }
}
