package com.erp.module.message.dto;

import lombok.Data;

/**
 * 消息查询DTO.
 *
 * @author AI
 */
@Data
public class MsgMessageQueryDTO {

    private String messageTitle;

    private Integer readStatus;

    private Long msgTypeId;

    private Long receiverId;

    private Integer pageNum;

    private Integer pageSize;
}
