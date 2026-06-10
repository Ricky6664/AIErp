package com.erp.module.message.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 待办审批完成事件 - 发布后由各业务模块监听处理.
 *
 * @author AI
 */
@Getter
public class TodoApprovedEvent extends ApplicationEvent {

    private final String businessType;

    private final Long businessId;

    private final Long todoId;

    public TodoApprovedEvent(String businessType, Long businessId, Long todoId) {
        super(todoId);
        this.businessType = businessType;
        this.businessId = businessId;
        this.todoId = todoId;
    }
}
