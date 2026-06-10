package com.erp.hrm.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 考勤查询DTO.
 *
 * @author AI
 */
@Data
public class AttendanceQueryDTO {

    private Integer pageNum;

    private Integer pageSize;

    private Long employeeId;

    private LocalDate attendanceDate;

    private String attendanceType;
}
