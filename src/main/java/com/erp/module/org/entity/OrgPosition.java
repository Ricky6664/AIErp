package com.erp.module.org.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("org_position")
public class OrgPosition extends BaseEntity {

    private String positionName;

    private String positionCode;

    private Long departmentId;

    private Integer sortNo;

    private Boolean enableFlag;
}
