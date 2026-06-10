package com.erp.hrm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 招聘管理创建DTO.
 *
 * @author AI
 */
@Data
public class RecruitmentCreateDTO {

    @NotNull(message = "岗位不能为空")
    private Long positionId;

    @NotNull(message = "部门不能为空")
    private Long departmentId;

    @NotNull(message = "招聘人数不能为空")
    @Min(value = 1, message = "招聘人数至少为1")
    private Integer recruitNum;

    @Size(max = 50, message = "薪资范围最长50个字符")
    private String salaryRange;

    private String requirement;

    private LocalDate deadline;
}
