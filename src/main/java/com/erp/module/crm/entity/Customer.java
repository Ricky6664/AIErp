package com.erp.module.crm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户主表实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_customer")
public class Customer extends BaseEntity {

    private String customerCode;

    private String customerName;

    private String shortName;

    private String phone;

    private String email;

    private Long classId;

    @TableField("business_rep_id")
    private Long salesPersonId;

    @TableField("business_dept_id")
    private Long salesDeptId;

    private Long companyId;

    private String source;

    @TableField("status")
    private Integer auditStatus;

    private Integer isActive;

    private String remark;
}
