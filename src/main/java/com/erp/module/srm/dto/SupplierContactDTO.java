package com.erp.module.srm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 供应商联系人DTO.
 *
 * @author AI
 */
@Data
public class SupplierContactDTO {

    private Long id;

    @NotNull(message = "供应商ID不能为空")
    private Long supplierId;

    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 100, message = "联系人姓名最长100个字符")
    private String contactName;

    @Size(max = 50, message = "昵称最长50个字符")
    private String nickname;

    @Size(max = 10, message = "性别最长10个字符")
    private String gender;

    private Integer isDefault;

    @Size(max = 100, message = "职位最长100个字符")
    private String position;
}
