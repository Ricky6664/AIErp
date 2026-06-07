package com.erp.hrm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SalaryVO {

    private Long id;

    private Long employeeId;

    private BigDecimal baseSalary;

    private BigDecimal allowance;

    private BigDecimal deduction;

    private BigDecimal netSalary;

    private String salaryMonth;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
