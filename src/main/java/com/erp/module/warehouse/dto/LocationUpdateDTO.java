package com.erp.module.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库位更新DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LocationUpdateDTO extends LocationCreateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;
}
