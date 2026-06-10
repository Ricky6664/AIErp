package com.erp.engine.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审核操作通用DTO.
 *
 * @author AI
 */
@Data
public class AuditOperationDTO {

    @NotBlank(message = "单据类型不能为空")
    private String docType;

    @NotNull(message = "单据ID不能为空")
    private Long docId;
}
