package com.erp.approval.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批加签DTO.
 *
 * @author AI
 */
@Data
public class RecordCountersignDTO {

    @NotNull(message = "审批记录ID不能为空")
    private Long recordId;

    @NotNull(message = "加签人ID不能为空")
    private Long countersignApproverId;

    @NotBlank(message = "加签原因不能为空")
    private String comment;
}
