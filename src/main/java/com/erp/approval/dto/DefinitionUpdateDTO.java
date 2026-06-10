package com.erp.approval.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 审批定义更新DTO.
 *
 * @author AI
 */
@Data
public class DefinitionUpdateDTO {

    @NotNull(message = "审批定义ID不能为空")
    private Long id;

    private String definitionName;

    private String businessType;

    private String flowConfig;

    private Boolean enableFlag;

    private Integer version;

    private List<NodeCreateDTO> nodes;
}
