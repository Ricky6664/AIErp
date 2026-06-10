package com.erp.module.srm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("srm_supplier")
public class Supplier extends BaseEntity {

    private String supplierCode;

    private String supplierName;

    private String shortName;

    private String phone;

    private String email;

    private Long classId;

    private Long buyerId;

    private Long buyerDeptId;

    private Long companyId;

    private String source;

    private String auditStatus;

    private Boolean isActive;

    private String remark;
}
