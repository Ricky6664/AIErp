package com.erp.engine.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 审核配置更新DTO.
 *
 * @author AI
 */
@Schema(description = "审核配置更新请求")
@Data
public class SysAuditConfigUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "单据类型编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "单据类型不能为空")
    private String docType;

    @Schema(description = "是否自动审核通过")
    private Boolean autoConfirm;

    @Schema(description = "是否启用审批流程")
    private Boolean approvalEnabled;

    @Schema(description = "审批流程配置(JSON)")
    private String approvalFlowConfig;
}
