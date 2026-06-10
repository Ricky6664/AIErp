package com.erp.module.finance.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会计科目查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会计科目查询参数")
public class AccountQueryDTO extends PageQuery {

    @Schema(description = "科目编码（模糊搜索）")
    private String accountCode;

    @Schema(description = "科目名称（模糊搜索）")
    private String accountName;

    @Schema(description = "上级科目ID")
    private Long parentId;

    @Schema(description = "科目类型")
    private Integer accountType;

    @Schema(description = "科目类别")
    private String category;

    @Schema(description = "是否末级")
    private Boolean isLeaf;
}
