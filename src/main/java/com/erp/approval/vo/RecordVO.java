package com.erp.approval.vo;

import lombok.Data;

/**
 * 审批记录VO.
 *
 * @author AI
 */
@Data
public class RecordVO {

    private Long id;

    private Long instanceId;

    private String nodeName;

    private Long approverId;

    private String approverName;

    private String action;

    private String comment;

    private String operateTime;
}
