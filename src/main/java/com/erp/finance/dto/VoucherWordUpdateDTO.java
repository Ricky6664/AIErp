package com.erp.finance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 凭证字更新DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VoucherWordUpdateDTO extends VoucherWordCreateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;
}
