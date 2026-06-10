package com.erp.module.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会计科目更新DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountUpdateDTO extends AccountCreateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;
}
