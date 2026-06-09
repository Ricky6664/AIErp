package com.erp.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "报价单明细创建请求")
public class SaleQuotationDetailCreateDTO {

    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10.0000")
    @NotNull(message = "数量不能为空")
    private java.math.BigDecimal quantity;

    @Schema(description = "单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.0000")
    @NotNull(message = "单价不能为空")
    private java.math.BigDecimal unitPrice;

    @Schema(description = "税率", example = "0.13")
    private java.math.BigDecimal taxRate;

    @Schema(description = "折扣", example = "0.0000")
    private java.math.BigDecimal discount;

    @Schema(description = "备注", example = "测试数据")
    private String remark;
}
