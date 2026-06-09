package com.erp.sale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 报价单明细创建DTO.
 *
 * @author AI
 */
@Data
public class SaleQuotationDetailCreateDTO {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "数量不能为空")
    private java.math.BigDecimal quantity;

    @NotNull(message = "单价不能为空")
    private java.math.BigDecimal unitPrice;

    private java.math.BigDecimal taxRate;

    private java.math.BigDecimal discount;

    private String remark;
}
