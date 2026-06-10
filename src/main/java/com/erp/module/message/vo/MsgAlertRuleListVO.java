package com.erp.module.message.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 业务预警规则列表VO.
 *
 * @author AI
 */
@Data
public class MsgAlertRuleListVO {

    private Long id;

    private String ruleName;

    private String conditionExpression;

    private BigDecimal threshold;

    private String frequency;

    private String triggerActionConfig;

    private Boolean isEnabled;

    private LocalDateTime createTime;
}
