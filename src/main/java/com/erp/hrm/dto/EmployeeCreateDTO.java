package com.erp.hrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 员工档案创建DTO.
 *
 * @author AI
 */
@Data
public class EmployeeCreateDTO {

    @NotBlank(message = "工号不能为空")
    @Size(max = 20, message = "工号最长20个字符")
    private String employeeNo;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名最长50个字符")
    private String name;

    private String gender;

    private String idCard;

    private String phone;

    private String email;

    private Long departmentId;

    private Long positionId;

    private LocalDate entryDate;

    private String employeeStatus;
}
