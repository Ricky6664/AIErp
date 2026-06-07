package com.erp.finance.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会计期间VO.
 *
 * @author AI
 */
@Data
public class AccountingPeriodVO {

    private Long id;

    private String fiscalYear;

    private String period;

    private LocalDate startDate;

    private LocalDate endDate;

    private String periodStatus;

    private Boolean isYearEnd;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
