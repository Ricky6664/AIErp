package com.erp.module.srm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 供应商联系人VO.
 *
 * @author AI
 */
@Data
public class SupplierContactVO {

    private Long id;

    private Long supplierId;

    private String contactName;

    private String nickname;

    private String gender;

    private Integer isDefault;

    private String position;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createBy;

    private Long updateBy;
}
