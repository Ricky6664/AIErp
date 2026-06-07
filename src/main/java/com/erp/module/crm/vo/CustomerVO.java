package com.erp.module.crm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户VO.
 *
 * @author AI
 */
@Data
public class CustomerVO {

    private Long id;

    private String customerCode;

    private String customerName;

    private String shortName;

    private String phone;

    private String email;

    private Long classId;

    private Long salesPersonId;

    private Long salesDeptId;

    private Long companyId;

    private String source;

    private Integer auditStatus;

    private Integer isActive;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long creatorId;

    private Long updaterId;
}
