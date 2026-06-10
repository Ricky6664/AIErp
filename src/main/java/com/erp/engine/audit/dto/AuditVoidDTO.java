package com.erp.engine.audit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 作废请求DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditVoidDTO extends AuditOperationDTO {

    @NotBlank(message = "作废原因不能为空")
    private String voidReason;
}
