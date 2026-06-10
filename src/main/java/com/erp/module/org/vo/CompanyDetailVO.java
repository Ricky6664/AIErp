package com.erp.module.org.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CompanyDetailVO {

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

    private String address;

    private String extStr1;

    private String extStr2;

    private String extStr3;

    private String extStr4;

    private String extStr5;

    private String extStr6;

    private String extStr7;

    private String extStr8;

    private String extStr9;

    private String extStr10;

    private BigDecimal extNum1;

    private BigDecimal extNum2;

    private BigDecimal extNum3;

    private BigDecimal extNum4;

    private BigDecimal extNum5;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime extDate1;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime extDate2;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime extDate3;

    private Boolean extBool1;

    private Boolean extBool2;

    private Boolean extBool3;

    private String extJson;

    private String createdByName;

    private String updatedByName;
}
