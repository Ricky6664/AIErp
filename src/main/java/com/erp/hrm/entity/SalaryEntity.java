package com.erp.hrm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hrm_salary")
public class SalaryEntity extends BaseEntity {

    private Long employeeId;

    private BigDecimal baseSalary;

    private BigDecimal allowance;

    private BigDecimal deduction;

    private BigDecimal netSalary;

    private String salaryMonth;

    private Boolean enableFlag;
}
