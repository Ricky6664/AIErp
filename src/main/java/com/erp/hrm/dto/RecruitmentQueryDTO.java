package com.erp.hrm.dto;

import lombok.Data;

/**
 * 招聘管理查询DTO.
 *
 * @author AI
 */
@Data
public class RecruitmentQueryDTO {

    private Integer pageNum;

    private Integer pageSize;

    private Long departmentId;

    private Long positionId;

    private String recruitStatus;

    private String keyword;
}
