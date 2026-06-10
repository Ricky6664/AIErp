package com.erp.module.message.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息模板列表VO.
 *
 * @author AI
 */
@Data
public class MsgTemplateListVO {

    private Long id;

    private String templateCode;

    private String titleTemplate;

    private Boolean isEnabled;

    private LocalDateTime createTime;
}
