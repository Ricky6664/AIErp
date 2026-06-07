import request from '@/utils/request'

export interface WarehouseWorkbenchKpiVO {
  warehouseTotal: number
  warehouseEnabled: number
  locationTotal: number
  locationEnabled: number
}

/** 获取仓库管理工作台KPI统计数据 */
export function getWarehouseWorkbenchKpiApi(): Promise<WarehouseWorkbenchKpiVO> {
  return request.get('/api/warehouse/workbench/kpi')
}

// ========== 图表数据类型 ==========

export interface ChartTrendItem {
  date: string
  count: number
}

export interface ChartDistributionItem {
  name: string
  value: number
}

export interface WarehouseWorkbenchChartVO {
  warehouseTrend: ChartTrendItem[]
  locationTrend: ChartTrendItem[]
  warehouseDistribution: ChartDistributionItem[]
  locationDistribution: ChartDistributionItem[]
}

export type TimeRange = 'day' | 'week' | 'month'

/** 获取仓库管理工作台图表数据 */
export function getWarehouseWorkbenchChartApi(
  range: TimeRange
): Promise<WarehouseWorkbenchChartVO> {
  return request.get('/warehouse/workbench/chart', { params: { range } })
}
