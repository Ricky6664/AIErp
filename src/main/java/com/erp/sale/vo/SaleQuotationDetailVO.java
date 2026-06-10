package com.erp.sale.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "报价单详情视图")
public class SaleQuotationDetailVO extends SaleQuotationListVO {

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "版本号（乐观锁）")
    private Integer version;

    @Schema(description = "明细行列表")
    private List<SaleQuotationDetailVO> details;

    @Schema(description = "业务日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bizDate;
}
