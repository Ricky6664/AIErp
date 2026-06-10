package com.erp.approval.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批实例VO.
 *
 * @author AI
 */
@Data
public class InstanceVO {

    private Long id;

    private Long definitionId;

    private String definitionName;

    private String businessType;

    private Long businessId;

    private Long applicantId;

    private String applicantName;

    private Long currentNodeId;

    private String currentNodeName;

    private String status;

    private LocalDateTime createTime;
}
