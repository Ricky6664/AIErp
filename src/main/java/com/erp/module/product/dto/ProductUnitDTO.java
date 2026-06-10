package com.erp.module.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品多单位DTO.
 *
 * @author AI
 */
@Data
public class ProductUnitDTO {

    private Long id;

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "单位ID不能为空")
    private Long unitId;

    private Boolean isBaseUnit;

    private Boolean isPurchaseUnit;

    private Boolean isSaleUnit;

    private Boolean isProductionUnit;

    @DecimalMin(value = "0", message = "转换比例不能为负数")
    private BigDecimal conversionRate;
}
