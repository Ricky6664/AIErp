package com.erp.module.srm.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.module.srm.entity.SrmTagDefinition;
import lombok.Data;

@Data
public class SrmTagDefinitionQueryDTO {
    private String tagName;
    private String tagCode;
    private String tagGroup;
    private String sortOrder;
    private Boolean isActive;
    private Integer pageNum;
    private Integer pageSize;

    public Page<SrmTagDefinition> toPage() {
        return new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
    }
}
