package com.erp.approval.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批实例实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("approval_instance")
public class ApprovalInstanceEntity extends BaseEntity {

    private Long definitionId;

    private String businessType;

    private Long businessId;

    private Long applicantId;

    private Long currentNodeId;

    private String status;
}
