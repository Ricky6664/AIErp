package com.erp.module.srm.dto;

import lombok.Data;

@Data
public class SrmTagDefinitionDTO {
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
