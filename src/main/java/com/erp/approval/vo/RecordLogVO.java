package com.erp.approval.vo;

import lombok.Data;

/**
 * 审批记录日志VO（JOIN instance + definition 的丰富字段）.
 *
 * @author AI
 */
@Data
public class RecordLogVO {

    private Long recordId;

    private Long instanceId;

    private String nodeName;

    private Long approverId;

    private String action;

    private String comment;

    private String operateTime;

    private String recordCreateTime;

    private Long definitionId;

    private String businessType;

    private Long businessId;

    private Long applicantId;

    private String instanceStatus;

    private Long currentNodeId;

    private String definitionName;

    private String definitionCode;
}
