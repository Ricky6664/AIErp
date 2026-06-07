package com.erp.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会计期间更新DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountingPeriodUpdateDTO extends AccountingPeriodCreateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;
}
