package com.erp.module.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prod_product_class")
public class ProductClass extends BaseEntity {

    private String className;

    private Long parentId;

    private Integer sortOrder;

    private Boolean isActive;
}
