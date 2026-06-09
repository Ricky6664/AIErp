package com.erp.module.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 业务预警规则创建DTO.
 *
 * @author AI
 */
@Data
public class MsgAlertRuleCreateDTO {

    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    private String conditionExpression;

    private BigDecimal threshold;

    private String frequency;

    private String triggerActionConfig;

    private Boolean isEnabled;
}
