package com.erp.hrm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalaryUpdateDTO {

    @NotNull(message = "薪资ID不能为空")
    private Long id;

    private BigDecimal baseSalary;

    private BigDecimal allowance;

    private BigDecimal deduction;
}
