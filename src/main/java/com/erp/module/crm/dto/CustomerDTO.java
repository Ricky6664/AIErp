package com.erp.module.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 客户DTO.
 *
 * @author AI
 */
@Data
public class CustomerDTO {

    private Long id;

    @NotBlank(message = "客户编码不能为空")
    @Size(max = 50, message = "客户编码最长50个字符")
    private String customerCode;

    @NotBlank(message = "客户名称不能为空")
    @Size(max = 200, message = "客户名称最长200个字符")
    private String customerName;

    @Size(max = 100, message = "客户简称最长100个字符")
    private String shortName;

    @Size(max = 20, message = "电话最长20个字符")
    private String phone;

    @Size(max = 100, message = "邮箱最长100个字符")
    private String email;

    private Long classId;

    private Long salesPersonId;

    private Long salesDeptId;

    private Long companyId;

    @Size(max = 50, message = "客户来源最长50个字符")
    private String source;

    private Integer auditStatus;

    @NotNull(message = "启用状态不能为空")
    private Integer isActive;

    @Size(max = 500, message = "备注最长500个字符")
    private String remark;
}
