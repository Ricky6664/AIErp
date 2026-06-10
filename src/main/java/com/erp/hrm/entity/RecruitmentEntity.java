package com.erp.hrm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 招聘管理实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hrm_recruitment")
public class RecruitmentEntity extends BaseEntity {

    private Long positionId;

    private Long departmentId;

    private Integer recruitNum;

    private String salaryRange;

    private String requirement;

    private String recruitStatus;

    private LocalDate deadline;
}
