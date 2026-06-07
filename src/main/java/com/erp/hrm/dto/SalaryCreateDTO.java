package com.erp.hrm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalaryCreateDTO {

    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    @DecimalMin(value = "0", message = "基本工资不能为负数")
    private BigDecimal baseSalary;

    @DecimalMin(value = "0", message = "津贴不能为负数")
    private BigDecimal allowance;

    @DecimalMin(value = "0", message = "扣款不能为负数")
    private BigDecimal deduction;

    @NotBlank(message = "薪资月份不能为空")
    private String salaryMonth;
}
