package com.erp.module.crm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户地址实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_customer_address")
public class CustomerAddress extends BaseEntity {

    private Long customerId;

    private String addressType;

    private String country;

    private String province;

    private String city;

    private String district;

    private String addressDetail;

    private String contactName;

    private String contactPhone;

    private Boolean isDefault;
}
