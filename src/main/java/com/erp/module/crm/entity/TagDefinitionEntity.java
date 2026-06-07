package com.erp.module.crm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签定义实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_tag_definition")
public class TagDefinitionEntity extends BaseEntity {

    private String tagName;

    private String tagGroup;

    private String tagColor;

    private Integer sortOrder;

    private Boolean isActive;
}
