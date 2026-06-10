package com.erp.flow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 单据引入/关联操作DTO.
 *
 * @author AI
 */
@Data
@Schema(description = "单据引入/关联操作请求参数")
public class BizDocRelationDTO {

    @NotBlank(message = "源单据类型不能为空")
    @Schema(description = "源单据类型", example = "sale_order")
    private String sourceDocType;

    @NotNull(message = "源单据ID不能为空")
    @Schema(description = "源单据ID")
    private Long sourceDocId;

    @Schema(description = "源单据明细行ID（行级关联时必填）")
    private Long sourceDetailId;

    @NotBlank(message = "目标单据类型不能为空")
    @Schema(description = "目标单据类型", example = "purchase_order")
    private String targetDocType;

    @NotNull(message = "目标单据ID不能为空")
    @Schema(description = "目标单据ID")
    private Long targetDocId;

    @Schema(description = "目标单据明细行ID（行级关联时必填）")
    private Long targetDetailId;

    @NotBlank(message = "关联类型不能为空")
    @Schema(description = "关联类型：import/push/copy", example = "import")
    private String relationType;

    @NotNull(message = "关联数量不能为空")
    @PositiveOrZero(message = "关联数量不能为负数")
    @Schema(description = "关联数量", example = "10.000000")
    private BigDecimal relationQty;
}
