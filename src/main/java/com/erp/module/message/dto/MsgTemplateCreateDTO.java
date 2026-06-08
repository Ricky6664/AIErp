package com.erp.module.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 消息模板创建DTO.
 *
 * @author AI
 */
@Data
public class MsgTemplateCreateDTO {

    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    private String titleTemplate;

    private String contentTemplate;

    private Boolean isEnabled;
}
