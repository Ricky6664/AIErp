package com.erp.module.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 银行账户更新DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BankAccountUpdateDTO extends BankAccountCreateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;
}
