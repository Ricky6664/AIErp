package com.erp.module.crm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户分类VO.
 *
 * @author AI
 */
@Data
public class CustomerClassVO {

    private Long id;

    private String className;

    private Long parentId;

    private Integer sortOrder;

    private Integer isActive;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long creatorId;

    private Long updaterId;
}
