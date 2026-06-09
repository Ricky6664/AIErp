package com.erp.sale.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SaleQuotationListVO {

    private Long id;

    private String saleNo;

    private String saleName;

    private Integer status;

    private String statusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String creatorName;
}
