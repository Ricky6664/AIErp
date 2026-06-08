package com.erp.engine.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 审核通过请求DTO.
 *
 * @author AI
 */
@Schema(description = "审核通过请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditApproveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "单据类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "PURCHASE_ORDER")
    @NotBlank(message = "单据类型不能为空")
    private String docType;

    @Schema(description = "单据ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "单据ID不能为空")
    private Long docId;

    @Schema(description = "审核意见", requiredMode = Schema.RequiredMode.REQUIRED, example = "同意")
    @NotBlank(message = "审核意见不能为空")
    private String opinion;
}
