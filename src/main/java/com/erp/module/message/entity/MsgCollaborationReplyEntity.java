package com.erp.module.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 协作讨论回复实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_collaboration_reply")
public class MsgCollaborationReplyEntity extends BaseEntity {

    private Long collaborationId;

    private String replyContent;

    private Long replierId;

    private Long parentReplyId;
}
