package com.erp.module.warehouse.service;

import java.util.List;
import java.util.Map;

/**
 * 仓库管理工作台聚合数据Service接口.
 *
 * @author AI
 */
public interface WorkbenchAggregateService {

    /**
     * 获取工作台KPI统计数据（仓库总数/启用数/库位总数/启用数）.
     *
     * @return KPI指标Map
     */
    Map<String, Object> getKpiStats();

    /**
     * 获取仓库创建趋势（按日分组）.
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 趋势数据列表
     */
    List<Map<String, Object>> getWarehouseTrend(String startDate, String endDate);

    /**
     * 获取库位创建趋势（按日分组）.
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 趋势数据列表
     */
    List<Map<String, Object>> getLocationTrend(String startDate, String endDate);
}
