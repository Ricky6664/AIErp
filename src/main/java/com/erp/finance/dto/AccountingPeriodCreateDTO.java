package com.erp.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 会计期间创建DTO.
 *
 * @author AI
 */
@Data
public class AccountingPeriodCreateDTO {

    @NotBlank(message = "会计年度不能为空")
    private String fiscalYear;

    @NotBlank(message = "会计期间不能为空")
    private String period;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @NotBlank(message = "期间状态不能为空")
    private String periodStatus;

    private Boolean isYearEnd;
}
