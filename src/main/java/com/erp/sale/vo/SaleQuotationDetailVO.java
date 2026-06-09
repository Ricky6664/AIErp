package com.erp.sale.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SaleQuotationDetailVO extends SaleQuotationListVO {

    private String remark;

    private Integer version;

    private List<SaleQuotationDetailVO> details;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bizDate;
}
