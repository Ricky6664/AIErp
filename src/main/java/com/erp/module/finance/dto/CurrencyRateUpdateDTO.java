package com.erp.module.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 币种汇率更新DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CurrencyRateUpdateDTO extends CurrencyRateCreateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;
}
