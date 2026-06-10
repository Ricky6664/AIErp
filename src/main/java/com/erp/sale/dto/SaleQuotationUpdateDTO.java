package com.erp.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报价单更新DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "报价单更新请求")
public class SaleQuotationUpdateDTO extends SaleQuotationCreateDTO {

    @Schema(description = "报价单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "版本号（乐观锁）", example = "0")
    private Integer version;
}
