package com.erp.sale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class SaleQuotationCreateDTO {

    @NotBlank(message = "名称不能为空")
    private String saleName;

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bizDate;

    @NotNull(message = "明细行不能为空")
    @Size(min = 1, message = "至少一条明细行")
    private List<SaleQuotationDetailCreateDTO> details;
}
