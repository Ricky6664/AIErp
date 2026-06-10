package com.erp.module.org.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("org_department")
public class OrgDepartment extends BaseEntity {

    private Long companyId;

    private String departmentName;

    private String deptCode;

    private Long parentId;

    private String managerName;

    private Integer sortNo;

    private Boolean enableFlag;

    private String deptType;
}
