package com.erp.module.crm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户标签关联VO.
 *
 * @author AI
 */
@Data
public class CustomerTagRelVO {

    private Long id;

    private Long customerId;

    private Long tagId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long creatorId;

    private Long updaterId;
}
