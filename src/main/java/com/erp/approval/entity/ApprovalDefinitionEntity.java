package com.erp.approval.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批定义实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("approval_definition")
public class ApprovalDefinitionEntity extends BaseEntity {

    private String definitionName;

    private String definitionCode;

    private String businessType;

    private String flowConfig;

    private Boolean enableFlag;
}
