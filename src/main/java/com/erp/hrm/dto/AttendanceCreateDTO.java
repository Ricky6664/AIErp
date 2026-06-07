package com.erp.hrm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤创建DTO.
 *
 * @author AI
 */
@Data
public class AttendanceCreateDTO {

    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    @NotNull(message = "考勤日期不能为空")
    private LocalDate attendanceDate;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private BigDecimal workHours;

    private String attendanceType;

    private BigDecimal overtimeHours;
}
