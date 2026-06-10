package com.erp.module.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 客户地址DTO.
 *
 * @author AI
 */
@Data
public class CustomerAddressDTO {

    private Long id;

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotBlank(message = "地址类型不能为空")
    @Size(max = 20, message = "地址类型最长20个字符")
    private String addressType;

    @Size(max = 50, message = "国家最长50个字符")
    private String country;

    @Size(max = 50, message = "省份最长50个字符")
    private String province;

    @Size(max = 50, message = "城市最长50个字符")
    private String city;

    @Size(max = 50, message = "区/县最长50个字符")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址最长200个字符")
    private String addressDetail;

    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名最长50个字符")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    @Size(max = 30, message = "联系电话最长30个字符")
    private String contactPhone;

    private Boolean isDefault;
}
