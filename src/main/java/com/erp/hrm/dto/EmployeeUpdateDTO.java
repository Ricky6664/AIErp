package com.erp.hrm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工档案更新DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeUpdateDTO extends EmployeeCreateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;
}
