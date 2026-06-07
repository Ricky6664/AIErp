package com.erp.hrm.dto;

import lombok.Data;

/**
 * 员工档案查询DTO.
 *
 * @author AI
 */
@Data
public class EmployeeQueryDTO {

    private Integer pageNum;

    private Integer pageSize;

    private String name;

    private String employeeStatus;

    private Long departmentId;
}
