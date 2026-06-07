package com.erp.module.finance.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 币种汇率VO.
 *
 * @author AI
 */
@Data
public class CurrencyRateVO {

    private Long id;

    private String currencyCode;

    private String currencyName;

    private String currencySymbol;

    private BigDecimal exchangeRate;

    private Integer rateType;

    private LocalDate effectiveDate;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
