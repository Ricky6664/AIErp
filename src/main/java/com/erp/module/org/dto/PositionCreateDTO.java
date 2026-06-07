package com.erp.module.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PositionCreateDTO {

    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 100, message = "岗位名称最长100个字符")
    private String positionName;

    @NotNull(message = "所属部门不能为空")
    private Long departmentId;

    private Integer sortNo;

    @NotNull(message = "启用状态不能为空")
    private Boolean enableFlag;
}
