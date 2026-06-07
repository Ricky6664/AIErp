package com.erp.module.org.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CompanyListVO {

    private Long id;

    private String companyName;

    private String companyShortName;

    private String creditCode;

    private String legalPerson;

    @JsonFormat(pattern = "#,##0.00")
    private BigDecimal registeredCapital;

    private String phone;

    private Boolean enableFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
