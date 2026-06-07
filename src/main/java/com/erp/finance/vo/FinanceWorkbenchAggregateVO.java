package com.erp.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 财务基础设置工作台聚合数据VO.
 *
 * @author AI
 * @since 2026-06-07
 */
@Data
@Schema(description = "财务基础设置工作台聚合数据")
public class FinanceWorkbenchAggregateVO {

    @Schema(description = "币种汇率总数")
    private Long currencyRateCount;

    @Schema(description = "银行账户总数")
    private Long bankAccountCount;

    @Schema(description = "激活银行账户数")
    private Long activeBankAccountCount;

    @Schema(description = "会计科目总数")
    private Long accountCount;

    @Schema(description = "末级科目数")
    private Long leafAccountCount;

    @Schema(description = "凭证字总数")
    private Long voucherWordCount;

    @Schema(description = "激活凭证字数")
    private Long activeVoucherWordCount;

    @Schema(description = "科目类型分布")
    private Map<String, Long> accountTypeDistribution;

    @Schema(description = "月度创建趋势")
    private List<TrendItem> monthlyTrend;

    @Data
    @Schema(description = "趋势数据项")
    public static class TrendItem {

        @Schema(description = "月份", example = "2026-06")
        private String month;

        @Schema(description = "创建数量")
        private Long count;
    }
}
