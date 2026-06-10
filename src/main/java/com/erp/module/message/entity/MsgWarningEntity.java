package com.erp.module.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 预警记录实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_warning")
public class MsgWarningEntity extends BaseEntity {

    private Long ruleId;

    private String businessId;

    private String title;

    private String content;

    private String warningType;

    private Boolean isHandled;

    private LocalDateTime handleTime;

    private Long handlerId;
}
