package com.erp.module.message.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办列表VO.
 *
 * @author AI
 */
@Data
public class TodoListVO {

    private Long id;

    private String todoTitle;

    private String businessType;

    private Long businessId;

    private Long assigneeId;

    private Long createdBy;

    private Boolean isCompleted;

    private LocalDateTime completeTime;

    private LocalDateTime createTime;

    private String sourceDocNo;

    private String sourceDocTitle;
}
