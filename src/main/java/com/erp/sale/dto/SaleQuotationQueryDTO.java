package com.erp.sale.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 报价单查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "报价单查询参数")
public class SaleQuotationQueryDTO extends PageQuery {

    @Schema(description = "单据编号")
    private String saleNo;

    @Schema(description = "名称")
    private String saleName;

    @Schema(description = "审核状态")
    private Integer status;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;
}
