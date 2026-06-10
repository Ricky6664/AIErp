package com.erp.approval.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批节点创建DTO.
 *
 * @author AI
 */
@Data
public class NodeCreateDTO {

    @NotNull(message = "节点顺序不能为空")
    private Integer nodeOrder;

    @NotNull(message = "节点名称不能为空")
    private String nodeName;

    @NotNull(message = "节点类型不能为空")
    private String nodeType;

    private String approverType;

    private String approverIds;

    private String conditionExpr;

    private String parallelMode;

    private Integer timeoutHours;
}
