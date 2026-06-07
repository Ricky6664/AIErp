package com.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 会计期间实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_accounting_period")
public class AccountingPeriodEntity extends BaseEntity {

    private String fiscalYear;

    private String period;

    private LocalDate startDate;

    private LocalDate endDate;

    private String periodStatus;

    private Boolean isYearEnd;
}
