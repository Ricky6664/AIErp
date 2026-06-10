package com.erp.module.srm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 供应商分类VO.
 *
 * @author AI
 */
@Data
public class SupplierClassVO {

    private Long id;

    private String className;

    private Long parentId;

    private Integer sortOrder;

    private Boolean isActive;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createBy;

    private Long updateBy;
}
