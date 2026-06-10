package com.erp.module.message.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 业务预警规则更新DTO.
 *
 * @author AI
 */
@Data
public class MsgAlertRuleUpdateDTO {

    private String ruleName;

    private String conditionExpression;

    private BigDecimal threshold;

    private String frequency;

    private String triggerActionConfig;

    private Boolean isEnabled;
}
