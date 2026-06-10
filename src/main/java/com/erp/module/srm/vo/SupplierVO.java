package com.erp.module.srm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 供应商VO.
 *
 * @author AI
 */
@Data
public class SupplierVO {

    private Long id;

    private String supplierCode;

    private String supplierName;

    private String shortName;

    private String phone;

    private String email;

    private Long classId;

    private Long buyerId;

    private Long buyerDeptId;

    private Long companyId;

    private String source;

    private String auditStatus;

    private Boolean isActive;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createBy;

    private Long updateBy;
}
