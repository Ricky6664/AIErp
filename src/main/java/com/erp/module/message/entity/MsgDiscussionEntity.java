package com.erp.module.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 单据沟通实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_discussion")
public class MsgDiscussionEntity extends BaseEntity {

    private String businessType;

    private Long businessId;

    private Long senderId;

    private String content;

    private Long parentId;

    private String attachment;
}
