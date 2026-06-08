package com.erp.module.message.dto;

import lombok.Data;

/**
 * 消息模板查询DTO.
 *
 * @author AI
 */
@Data
public class MsgTemplateQueryDTO {

    private String templateCode;

    private String titleTemplate;

    private Boolean isEnabled;

    private Integer pageNum;

    private Integer pageSize;
}
