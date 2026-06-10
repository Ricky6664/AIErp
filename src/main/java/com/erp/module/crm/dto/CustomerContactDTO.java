package com.erp.module.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 客户联系人DTO.
 *
 * @author AI
 */
@Data
public class CustomerContactDTO {

    private Long id;

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名最长50个字符")
    private String contactName;

    @Size(max = 50, message = "昵称最长50个字符")
    private String nickname;

    @Size(max = 10, message = "性别最长10个字符")
    private String gender;

    @Size(max = 50, message = "职位最长50个字符")
    private String position;

    @Size(max = 50, message = "角色最长50个字符")
    private String role;

    private Boolean isDefault;
}
