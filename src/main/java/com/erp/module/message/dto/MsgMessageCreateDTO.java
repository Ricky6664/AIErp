package com.erp.module.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 消息创建DTO.
 *
 * @author AI
 */
@Data
public class MsgMessageCreateDTO {

    @NotBlank(message = "消息标题不能为空")
    private String messageTitle;

    private String messageContent;

    @NotNull(message = "接收人不能为空")
    private Long receiverId;

    private Long msgTypeId;

    private String sourceType;
}
