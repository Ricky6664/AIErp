package com.erp.module.org.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompanyCreateDTO {

    @NotBlank(message = "公司名称不能为空")
    @Size(max = 200, message = "公司名称最长200个字符")
    private String companyName;

    @Size(max = 100, message = "公司简称最长100个字符")
    private String companyShortName;

    @Pattern(regexp = "^[0-9A-HJ-NP-RTUW-Y]{2}\\d{6}[0-9A-HJ-NP-RTUW-Y]{10}$", message = "统一社会信用代码格式不正确")
    private String creditCode;

    @Size(max = 50, message = "法人最长50个字符")
    private String legalPerson;

    @DecimalMin(value = "0", message = "注册资本不能为负数")
    private BigDecimal registeredCapital;

    @Size(max = 300, message = "注册地址最长300个字符")
    private String address;

    @Size(max = 30, message = "联系电话最长30个字符")
    private String phone;
}
