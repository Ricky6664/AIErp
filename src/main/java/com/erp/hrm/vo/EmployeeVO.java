package com.erp.hrm.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 员工档案VO.
 *
 * @author AI
 */
@Data
public class EmployeeVO {

    private Long id;

    private String employeeNo;

    private String name;

    private String gender;

    private String idCard;

    private String phone;

    private String email;

    private Long departmentId;

    private Long positionId;

    private LocalDate entryDate;

    private String employeeStatus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
