package com.erp.approval.dto;

import lombok.Data;

/**
 * 审批实例查询DTO.
 *
 * @author AI
 */
@Data
public class InstanceQueryDTO {

    private Integer pageNum;

    private Integer pageSize;

    private String businessType;

    private String status;

    private Long applicantId;
}
