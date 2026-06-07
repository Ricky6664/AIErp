package com.erp.module.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品控制策略DTO.
 *
 * @author AI
 */
@Data
public class ProductControlDTO {

    private Long id;

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    private Long defaultPurchaseUnitId;

    private Long defaultSaleUnitId;

    private Boolean inventoryManageFlag;

    private Boolean locationManageFlag;

    private Boolean batchManageFlag;

    private Boolean serialManageFlag;

    private Boolean shelfLifeManageFlag;

    @Min(value = 0, message = "最小包装量不能为负数")
    private BigDecimal minPackageQty;

    @Min(value = 0, message = "最小订购量不能为负数")
    private BigDecimal minOrderQty;
}
