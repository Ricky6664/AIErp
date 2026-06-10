package com.erp.module.srm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商联系人实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("srm_supplier_contact")
public class SupplierContact extends BaseEntity {

    private Long supplierId;

    private String contactName;

    private String nickname;

    private String gender;

    private Integer isDefault;

    private String position;
}
