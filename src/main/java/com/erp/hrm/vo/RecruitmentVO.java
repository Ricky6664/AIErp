package com.erp.hrm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 招聘管理VO.
 *
 * @author AI
 */
@Data
public class RecruitmentVO {

    private Long id;

    private String positionName;

    private String departmentName;

    private Integer recruitNum;

    private String salaryRange;

    private String recruitStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
