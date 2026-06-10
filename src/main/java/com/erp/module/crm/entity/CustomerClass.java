package com.erp.module.crm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户分类实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_customer_class")
public class CustomerClass extends BaseEntity {

    private String className;

    private Long parentId;

    private Integer sortOrder;

    private Integer isActive;
}
