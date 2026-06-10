package com.erp.approval.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 我的审批VO.
 *
 * @author AI
 */
@Data
public class MyApprovalVO {

    private Long instanceId;

    private Long definitionId;

    private String definitionName;

    private String businessType;

    private Long businessId;

    private Long applicantId;

    private String applicantName;

    private String currentNodeName;

    private String status;

    private String myAction;

    private String myComment;

    private String myOperateTime;

    private LocalDateTime createTime;
}
