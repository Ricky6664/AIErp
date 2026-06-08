package com.erp.approval.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批实例创建DTO.
 *
 * @author AI
 */
@Data
public class InstanceCreateDTO {

    @NotNull(message = "审批定义ID不能为空")
    private Long definitionId;

    @NotNull(message = "业务类型不能为空")
    private String businessType;

    @NotNull(message = "业务单据ID不能为空")
    private Long businessId;
}
