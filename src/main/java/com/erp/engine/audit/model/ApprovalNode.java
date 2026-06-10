package com.erp.engine.audit.model;

import lombok.Data;

/**
 * 审批节点模型.
 *
 * @author AI
 */
@Data
public class ApprovalNode {

    private String nodeName;
    private String approverRole;
    private Integer nodeOrder;
    private Boolean required;
}
