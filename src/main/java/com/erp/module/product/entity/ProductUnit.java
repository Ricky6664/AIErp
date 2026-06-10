package com.erp.module.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品多单位实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prod_product_unit")
public class ProductUnit extends BaseEntity {

    private Long productId;

    private Long unitId;

    private Boolean isBaseUnit;

    private Boolean isPurchaseUnit;

    private Boolean isSaleUnit;

    private Boolean isProductionUnit;

    private BigDecimal conversionRate;
}
