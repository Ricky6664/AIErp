package com.erp.flow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 关联关系列表VO.
 * 用于单据关联关系扁平列表.
 *
 * @author AI
 */
@Data
@Schema(description = "单据关联关系列表项")
public class RelationListVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "源单据类型")
    private String sourceDocType;

    @Schema(description = "源单据ID")
    private Long sourceDocId;

    @Schema(description = "目标单据类型")
    private String targetDocType;

    @Schema(description = "目标单据ID")
    private Long targetDocId;

    @Schema(description = "关联类型")
    private String relationType;

    @Schema(description = "关联数量")
    private BigDecimal relationQty;
}
