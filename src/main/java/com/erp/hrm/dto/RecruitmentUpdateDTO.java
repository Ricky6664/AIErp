package com.erp.hrm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招聘管理更新DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RecruitmentUpdateDTO extends RecruitmentCreateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;

    private String recruitStatus;
}
