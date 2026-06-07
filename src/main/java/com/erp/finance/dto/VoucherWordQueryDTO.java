package com.erp.finance.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 凭证字查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "凭证字查询参数")
public class VoucherWordQueryDTO extends PageQuery {

    @Schema(description = "凭证字名称（模糊搜索）")
    private String wordName;

    @Schema(description = "凭证字编码")
    private String wordCode;

    @Schema(description = "启用状态")
    private Integer status;
}
