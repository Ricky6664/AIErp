package com.erp.approval.vo;

import lombok.Data;

import java.util.Map;

/**
 * 审批统计VO.
 *
 * @author AI
 */
@Data
public class ApprovalStatisticsVO {

    private Long totalInstances;

    private Long pendingCount;

    private Long approvedCount;

    private Long rejectedCount;

    private Long withdrawnCount;

    private Long myPendingCount;

    private Long myReviewedCount;

    private Long mySubmittedCount;

    private Map<String, Long> statusDistribution;

    private Map<Long, Long> definitionCounts;
}
