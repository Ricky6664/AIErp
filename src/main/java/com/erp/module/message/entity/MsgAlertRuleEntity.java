package com.erp.module.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 业务预警规则实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("msg_alert_rule")
public class MsgAlertRuleEntity extends BaseEntity {

    private String ruleName;

    private String conditionExpression;

    private BigDecimal threshold;

    private String frequency;

    private String triggerActionConfig;

    private Boolean isEnabled;
}
