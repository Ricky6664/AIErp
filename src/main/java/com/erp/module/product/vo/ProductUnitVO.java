package com.erp.module.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品多单位VO.
 *
 * @author AI
 */
@Data
public class ProductUnitVO {

    private Long id;

    private Long productId;

    private Long unitId;

    private Boolean isBaseUnit;

    private Boolean isPurchaseUnit;

    private Boolean isSaleUnit;

    private Boolean isProductionUnit;

    private BigDecimal conversionRate;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createBy;

    private Long updateBy;
}
