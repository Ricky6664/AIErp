package com.erp.approval.vo;

import lombok.Data;

/**
 * 审批节点VO.
 *
 * @author AI
 */
@Data
public class NodeVO {

    private Long id;

    private Long definitionId;

    private Integer nodeOrder;

    private String nodeName;

    private String nodeType;

    private String approverType;

    private String approverIds;

    private String conditionExpr;

    private String parallelMode;

    private Integer timeoutHours;
}
