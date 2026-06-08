package com.erp.module.message.dto;

import com.erp.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 待办查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TodoQueryDTO extends PageQuery {

    private Long assigneeId;

    private String businessType;

    private Boolean isCompleted;

    private String keyword;
}
