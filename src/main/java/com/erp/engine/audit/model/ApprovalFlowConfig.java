package com.erp.engine.audit.model;

import lombok.Data;

import java.util.List;

/**
 * 审批流程配置模型.
 *
 * @author AI
 */
@Data
public class ApprovalFlowConfig {

    private String flowDefinitionId;
    private List<ApprovalNode> nodes;
}
