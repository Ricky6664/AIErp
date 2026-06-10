package com.erp.module.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 协作讨论主题实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_collaboration")
public class MsgCollaborationEntity extends BaseEntity {

    private String topic;

    private String category;

    private String content;

    private String participants;

    private Boolean isClosed;

    private LocalDateTime closeTime;
}
