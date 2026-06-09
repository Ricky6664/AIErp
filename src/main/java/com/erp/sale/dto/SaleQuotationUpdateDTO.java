package com.erp.sale.dto;

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
public class SaleQuotationUpdateDTO extends SaleQuotationCreateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;

    private Integer version;
}
