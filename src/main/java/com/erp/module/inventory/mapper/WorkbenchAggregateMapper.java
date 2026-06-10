package com.erp.module.inventory.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 仓库管理工作台聚合查询Mapper.
 *
 * @author AI
 */
@Mapper
public interface WorkbenchAggregateMapper {

    /**
     * 查询工作台KPI统计数据.
     *
     * @param tenantId 租户ID
     * @return KPI指标Map（warehouseTotal/warehouseActive/locationTotal/locationActive）
     */
    Map<String, Object> selectKpiStats(@Param("tenantId") Long tenantId);

    /**
     * 查询仓库创建趋势（按日分组）.
     *
     * @param tenantId  租户ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 趋势数据列表
     */
    List<Map<String, Object>> selectWarehouseTrendByDay(@Param("tenantId") Long tenantId,
                                                         @Param("startDate") String startDate,
                                                         @Param("endDate") String endDate);

    /**
     * 查询库位创建趋势（按日分组）.
     *
     * @param tenantId  租户ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 趋势数据列表
     */
    List<Map<String, Object>> selectLocationTrendByDay(@Param("tenantId") Long tenantId,
                                                        @Param("startDate") String startDate,
                                                        @Param("endDate") String endDate);
}
