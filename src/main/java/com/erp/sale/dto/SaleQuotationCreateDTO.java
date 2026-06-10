package com.erp.sale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 报价单创建DTO.
 *
 * @author AI
 */
@Data
@Schema(description = "报价单创建请求")
public class SaleQuotationCreateDTO {

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "销售报价单")
    @NotBlank(message = "名称不能为空")
    private String saleName;

    @Schema(description = "业务日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-06-09")
    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bizDate;

    @Schema(description = "明细行", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "明细行不能为空")
    @Size(min = 1, message = "至少一条明细行")
    private List<SaleQuotationDetailCreateDTO> details;
}
