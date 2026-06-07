package com.erp.hrm.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤VO.
 *
 * @author AI
 */
@Data
public class AttendanceVO {

    private Long id;

    private Long employeeId;

    private LocalDate attendanceDate;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private BigDecimal workHours;

    private String attendanceType;

    private BigDecimal overtimeHours;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
