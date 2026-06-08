package com.erp.module.crm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户标签关联实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_customer_tag_rel")
public class CustomerTagRel extends BaseEntity {

    private Long customerId;

    private Long tagId;
}
