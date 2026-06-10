package com.erp.module.product.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类VO.
 *
 * @author AI
 */
@Data
public class ProductClassVO {

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
