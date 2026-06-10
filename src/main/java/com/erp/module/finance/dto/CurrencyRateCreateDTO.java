package com.erp.module.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 币种汇率创建DTO.
 *
 * @author AI
 */
@Data
public class CurrencyRateCreateDTO {

    @NotBlank(message = "币种编码不能为空")
    @Size(max = 10, message = "币种编码最长10个字符")
    private String currencyCode;

    @NotBlank(message = "币种名称不能为空")
    @Size(max = 50, message = "币种名称最长50个字符")
    private String currencyName;

    @Size(max = 10, message = "币种符号最长10个字符")
    private String currencySymbol;

    @NotNull(message = "汇率不能为空")
    private BigDecimal exchangeRate;

    private Integer rateType;

    private LocalDate effectiveDate;
}
