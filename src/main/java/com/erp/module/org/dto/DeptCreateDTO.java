package com.erp.module.org.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeptCreateDTO {

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 100, message = "部门名称最长100个字符")
    private String deptName;

    private Long parentId;

    @NotNull(message = "所属公司不能为空")
    private Long companyId;

    private Long managerId;

    @Pattern(regexp = "business|functional|project", message = "部门类型必须为business/functional/project")
    private String deptType;

    @Min(value = 0, message = "排序号不能为负数")
    private Integer sortNo;
}
