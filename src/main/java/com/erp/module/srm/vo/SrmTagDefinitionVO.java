package com.erp.module.srm.vo;

import lombok.Data;

@Data
public class SrmTagDefinitionVO {
    private Long id;
    private String tagName;
    private String tagCode;
    private String tagType;
    private String tagGroup;
    private String tagColor;
    private String colorCode;
    private Integer sortOrder;
    private Boolean isActive;
    private String remark;
}
