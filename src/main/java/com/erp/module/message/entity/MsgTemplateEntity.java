package com.erp.module.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息模板实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_template")
public class MsgTemplateEntity extends BaseEntity {

    private String templateCode;

    private String titleTemplate;

    private String contentTemplate;

    private Boolean isEnabled;
}
