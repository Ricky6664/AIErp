package com.erp.module.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 单据待办实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_todo")
public class MsgTodoEntity extends BaseEntity {

    private String todoTitle;

    private String businessType;

    private Long businessId;

    private Long assigneeId;

    private Long createdBy;

    private Boolean isCompleted;

    private LocalDateTime completeTime;
}
