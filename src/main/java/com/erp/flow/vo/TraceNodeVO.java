package com.erp.flow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 追溯节点VO.
 * 用于单据追溯链路树形结构.
 *
 * @author AI
 */
@Data
@Schema(description = "单据追溯节点")
public class TraceNodeVO {

    @Schema(description = "单据类型")
    private String docType;

    @Schema(description = "单据ID")
    private Long docId;

    @Schema(description = "单据编号")
    private String docNo;

    @Schema(description = "单据状态")
    private String docStatus;

    @Schema(description = "关联类型")
    private String relationType;

    @Schema(description = "下游单据节点")
    private List<TraceNodeVO> children = new ArrayList<>();
}
