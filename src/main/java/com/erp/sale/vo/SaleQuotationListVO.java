package com.erp.sale.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "报价单列表视图")
public class SaleQuotationListVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "单据编号")
    private String saleNo;

    @Schema(description = "名称")
    private String saleName;

    @Schema(description = "审核状态（0草稿/1待审核/2已审核/3已驳回）")
    private Integer status;

    @Schema(description = "审核状态名称")
    private String statusName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String creatorName;
}
