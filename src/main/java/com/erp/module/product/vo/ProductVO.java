package com.erp.module.product.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品VO.
 *
 * @author AI
 */
@Data
public class ProductVO {

    private Long id;

    private String productCode;

    private String productName;

    private String model;

    private String spec;

    private String brand;

    private Long baseUnitId;

    private Boolean isMultiUnit;

    private Long classId;

    private Boolean isSaleable;

    private Boolean isPurchasable;

    private Boolean isProducible;

    private Boolean isOutsourceable;

    private Boolean isSubPart;

    private String auditStatus;

    private Boolean isActive;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createBy;

    private Long updateBy;
}
