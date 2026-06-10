package com.erp.module.finance.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 币种汇率查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "币种汇率查询参数")
public class CurrencyRateQueryDTO extends PageQuery {

    @Schema(description = "币种编码")
    private String currencyCode;

    @Schema(description = "币种名称（模糊搜索）")
    private String currencyName;

    @Schema(description = "汇率类型")
    private Integer rateType;
}
