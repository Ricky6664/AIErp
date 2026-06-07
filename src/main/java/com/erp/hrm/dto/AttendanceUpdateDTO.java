package com.erp.hrm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考勤更新DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceUpdateDTO extends AttendanceCreateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;
}
