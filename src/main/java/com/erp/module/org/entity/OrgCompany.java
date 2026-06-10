package com.erp.module.org.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("org_company")
public class OrgCompany extends BaseEntity {

    private String companyName;

    private String companyShortName;

    private String creditCode;

    private String legalPerson;

    private BigDecimal registeredCapital;

    private String address;

    private String phone;

    private Boolean enableFlag;

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

    private java.math.BigDecimal extNum1;

    private java.math.BigDecimal extNum2;

    private java.math.BigDecimal extNum3;

    private java.math.BigDecimal extNum4;

    private java.math.BigDecimal extNum5;

    private java.time.LocalDateTime extDate1;

    private java.time.LocalDateTime extDate2;

    private java.time.LocalDateTime extDate3;

    private Boolean extBool1;

    private Boolean extBool2;

    private Boolean extBool3;

    private String extJson;
}
