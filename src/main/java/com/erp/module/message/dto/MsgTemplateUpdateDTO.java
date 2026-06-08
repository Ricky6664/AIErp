package com.erp.module.message.dto;

import lombok.Data;

/**
 * 消息模板更新DTO.
 *
 * @author AI
 */
@Data
public class MsgTemplateUpdateDTO {

    private String titleTemplate;

    private String contentTemplate;

    private Boolean isEnabled;
}
