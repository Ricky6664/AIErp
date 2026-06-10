package com.erp.finance.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会计期间查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会计期间查询参数")
public class AccountingPeriodQueryDTO extends PageQuery {

    @Schema(description = "会计年度")
    private String fiscalYear;

    @Schema(description = "会计期间")
    private String period;

    @Schema(description = "期间状态")
    private String periodStatus;
}
