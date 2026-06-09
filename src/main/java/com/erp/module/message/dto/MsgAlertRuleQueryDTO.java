package com.erp.module.message.dto;

import lombok.Data;

/**
 * 业务预警规则查询DTO.
 *
 * @author AI
 */
@Data
public class MsgAlertRuleQueryDTO {

    private String ruleName;

    private Boolean isEnabled;

    private Integer pageNum;

    private Integer pageSize;
}
