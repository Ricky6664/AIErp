package com.erp.module.message.dto;

import lombok.Data;

/**
 * 消息更新DTO.
 *
 * @author AI
 */
@Data
public class MsgMessageUpdateDTO {

    private String messageTitle;

    private String messageContent;

    private Long msgTypeId;
}
