package com.erp.approval.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批操作DTO.
 *
 * @author AI
 */
@Data
public class RecordActionDTO {

    @NotNull(message = "审批实例ID不能为空")
    private Long instanceId;

    @NotBlank(message = "审批动作不能为空")
    private String action;

    private String comment;
}
