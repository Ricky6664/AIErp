package com.erp.module.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品主表实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prod_product")
public class Product extends BaseEntity {

    private String productCode;

    private String productName;

    private String model;

    private String spec;

    private String brand;

    private Long baseUnitId;

    private Boolean isMultiUnit;

    private Long classId;

    private Boolean isSaleable;

    private Boolean isPurchasable;

    private Boolean isProducible;

    private Boolean isOutsourceable;

    private Boolean isSubPart;

    private String auditStatus;

    private Boolean isActive;

    private String remark;
}
