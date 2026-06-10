package com.erp.module.crm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签定义VO.
 *
 * @author AI
 */
@Data
public class TagDefinitionVO {

    private Long id;

    private String tagName;

    private String tagGroup;

    private String tagColor;

    private Integer sortOrder;

    private Boolean isActive;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createBy;

    private Long updateBy;
}
