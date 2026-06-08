package com.erp.module.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_message")
public class MsgMessageEntity extends BaseEntity {

    private String messageTitle;

    private String messageContent;

    private Long receiverId;

    private Integer readStatus;

    private Long msgTypeId;

    private String sourceType;

    private Integer status;
}
