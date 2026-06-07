package com.erp.module.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库更新DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarehouseUpdateDTO extends WarehouseCreateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;
}
