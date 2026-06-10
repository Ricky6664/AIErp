package com.erp.approval.dto;

import lombok.Data;

/**
 * 审批定义查询DTO.
 *
 * @author AI
 */
@Data
public class DefinitionQueryDTO {

    private String definitionName;

    private String businessType;

    private Boolean enableFlag;

    private Integer pageNum;

    private Integer pageSize;
}
