package com.erp.module.crm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户联系人实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_customer_contact")
public class CustomerContact extends BaseEntity {

    private Long customerId;

    private String contactName;

    private String nickname;

    private String gender;

    private String position;

    private String role;

    private Boolean isDefault;
}
