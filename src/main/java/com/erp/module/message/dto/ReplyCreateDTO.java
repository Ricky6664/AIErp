package com.erp.module.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 协作讨论回复创建DTO.
 *
 * @author AI
 */
@Data
public class ReplyCreateDTO {

    @NotBlank(message = "回复内容不能为空")
    private String replyContent;

    private Long parentReplyId;
}
