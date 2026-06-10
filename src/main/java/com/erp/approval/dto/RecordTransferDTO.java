package com.erp.approval.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批转办DTO.
 *
 * @author AI
 */
@Data
public class RecordTransferDTO {

    @NotNull(message = "审批记录ID不能为空")
    private Long recordId;

    @NotNull(message = "转办目标人ID不能为空")
    private Long targetApproverId;

    @NotBlank(message = "转办原因不能为空")
    private String comment;
}
