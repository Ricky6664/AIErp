package com.erp.approval.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批记录实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("approval_record")
public class ApprovalRecordEntity extends BaseEntity {

    private Long instanceId;

    private String nodeName;

    private Long approverId;

    private String action;

    private String comment;

    private String operateTime;
}
