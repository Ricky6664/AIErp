package com.erp.module.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品控制策略VO.
 *
 * @author AI
 */
@Data
public class ProductControlVO {

    private Long id;

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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createBy;

    private Long updateBy;
}
