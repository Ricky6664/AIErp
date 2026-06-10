package com.erp.module.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 币种汇率实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_currency_rate")
public class CurrencyRateEntity extends BaseEntity {

    private String currencyCode;

    private String currencyName;

    private String currencySymbol;

    private java.math.BigDecimal exchangeRate;

    private Integer rateType;

    private java.time.LocalDate effectiveDate;
}
