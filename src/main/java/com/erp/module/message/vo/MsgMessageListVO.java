package com.erp.module.message.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息列表VO.
 *
 * @author AI
 */
@Data
public class MsgMessageListVO {

    private Long id;

    private String messageTitle;

    private Long receiverId;

    private Integer readStatus;

    private Long msgTypeId;

    private Integer status;

    private LocalDateTime createTime;
}
