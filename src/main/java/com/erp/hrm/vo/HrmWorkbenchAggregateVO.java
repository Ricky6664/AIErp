package com.erp.hrm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * HRM工作台聚合数据VO.
 *
 * @author AI
 * @since 2026-06-08
 */
@Data
@Schema(description = "HRM工作台聚合数据")
public class HrmWorkbenchAggregateVO {

    @Schema(description = "员工总数")
    private Long totalEmployees;

    @Schema(description = "在职员工数")
    private Long activeEmployees;

    @Schema(description = "本月新入职人数")
    private Long newHiresThisMonth;

    @Schema(description = "招聘中岗位数")
    private Long openRecruitments;

    @Schema(description = "本月应发薪资总额")
    private BigDecimal totalMonthlySalary;

    @Schema(description = "部门数量")
    private Long departmentCount;

    @Schema(description = "员工月度增长趋势")
    private List<TrendItem> employeeMonthlyTrend;

    @Schema(description = "部门人数分布")
    private Map<String, Long> departmentDistribution;

    @Schema(description = "招聘状态分布")
    private Map<String, Long> recruitmentStatusDistribution;

    @Schema(description = "月度考勤趋势")
    private List<TrendItem> attendanceMonthlyTrend;

    @Data
    @Schema(description = "趋势数据项")
    public static class TrendItem {

        @Schema(description = "月份", example = "2026-06")
        private String month;

        @Schema(description = "数量")
        private Long count;
    }
}
