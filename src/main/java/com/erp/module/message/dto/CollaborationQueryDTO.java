package com.erp.module.message.dto;

import lombok.Data;

/**
 * 协作讨论查询DTO.
 *
 * @author AI
 */
@Data
public class CollaborationQueryDTO {

    private Integer pageNum;

    private Integer pageSize;

    private String topic;

    private String category;

    private Long participantId;

    private Boolean isClosed;
}
