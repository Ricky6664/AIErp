package com.erp.module.srm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("srm_tag_definition")
public class SrmTagDefinition extends BaseEntity {
    private String tagName;
    private String tagCode;
    private String tagType;
    private String tagGroup;
    private String tagColor;
    private String colorCode;
    private Integer sortNo;
    private Boolean enableFlag;
    private String remark;
}
