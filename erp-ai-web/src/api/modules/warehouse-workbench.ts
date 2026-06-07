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
