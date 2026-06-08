package com.erp.approval.dto;

import lombok.Data;

/**
 * 审批记录日志查询DTO.
 *
 * @author AI
 */
@Data
public class RecordLogQueryDTO {

    private Integer pageNum;

    private Integer pageSize;

    private Long definitionId;

    private String action;

    private String status;

    private String businessType;

    private Long applicantId;

    private Long approverId;

    private String startTime;

    private String endTime;
}
