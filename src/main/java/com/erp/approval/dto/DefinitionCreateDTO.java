package com.erp.approval.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 审批定义创建DTO.
 *
 * @author AI
 */
@Data
public class DefinitionCreateDTO {

    @NotBlank(message = "审批定义名称不能为空")
    private String definitionName;

    @NotBlank(message = "审批定义编码不能为空")
    private String definitionCode;

    @NotBlank(message = "业务类型不能为空")
    private String businessType;

    private String flowConfig;

    private Boolean enableFlag;

    private List<NodeCreateDTO> nodes;
}
