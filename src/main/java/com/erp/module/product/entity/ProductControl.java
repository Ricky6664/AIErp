package com.erp.module.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品控制策略实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("prod_product_control")
public class ProductControl extends BaseEntity {

    private Long productId;

    private Long defaultPurchaseUnitId;

    private Long defaultSaleUnitId;

    private Boolean inventoryManageFlag;

    private Boolean locationManageFlag;

    private Boolean batchManageFlag;

    private Boolean serialManageFlag;

    private Boolean shelfLifeManageFlag;

    private BigDecimal minPackageQty;

    private BigDecimal minOrderQty;
}
