package com.erp.module.srm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 供应商DTO.
 *
 * @author AI
 */
@Data
public class SupplierDTO {

    private Long id;

    @NotBlank(message = "供应商编码不能为空")
    @Size(max = 50, message = "供应商编码最长50个字符")
    private String supplierCode;

    @NotBlank(message = "供应商名称不能为空")
    @Size(max = 200, message = "供应商名称最长200个字符")
    private String supplierName;

    @Size(max = 100, message = "供应商简称最长100个字符")
    private String shortName;

    @Size(max = 20, message = "电话最长20个字符")
    private String phone;

    @Size(max = 100, message = "邮箱最长100个字符")
    private String email;

    private Long classId;

    private Long buyerId;

    private Long buyerDeptId;

    private Long companyId;

    @Size(max = 50, message = "来源最长50个字符")
    private String source;

    @Size(max = 20, message = "审核状态最长20个字符")
    private String auditStatus;

    @NotNull(message = "启用状态不能为空")
    private Boolean isActive;

    @Size(max = 500, message = "备注最长500个字符")
    private String remark;
}
