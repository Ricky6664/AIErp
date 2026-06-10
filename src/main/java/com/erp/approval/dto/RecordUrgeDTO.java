package com.erp.approval.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批催办DTO.
 *
 * @author AI
 */
@Data
public class RecordUrgeDTO {

    @NotNull(message = "审批记录ID不能为空")
    private Long recordId;

    private String message;
}
