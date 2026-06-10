package com.erp.hrm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 员工档案实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hrm_employee")
public class EmployeeEntity extends BaseEntity {

    private String employeeNo;

    private String name;

    private String gender;

    private String idCard;

    private String phone;

    private String email;

    private Long departmentId;

    private Long positionId;

    private LocalDate entryDate;

    private String employeeStatus;
}
