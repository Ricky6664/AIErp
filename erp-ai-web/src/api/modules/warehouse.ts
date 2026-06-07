import request from '@/utils/request'
import type { PageResult } from '@/types/api.d'
import type {
  WarehouseQueryDTO,
  WarehouseCreateDTO,
  WarehouseUpdateDTO,
  WarehouseListVO,
  WarehouseDetailVO
} from '@/api/types/warehouse'

/** 分页查询仓库列表 */
export function getWarehousePage(params: WarehouseQueryDTO): Promise<PageResult<WarehouseListVO>> {
  return request.get('/api/warehouse/page', { params })
}

/** 查询仓库详情 */
export function getWarehouseDetail(id: number): Promise<WarehouseDetailVO> {
  return request.get(`/api/warehouse/${id}`)
}

/** 新增仓库 */
export function createWarehouse(data: WarehouseCreateDTO): Promise<number> {
  return request.post('/api/warehouse', data)
}

/** 修改仓库 */
export function updateWarehouse(data: WarehouseUpdateDTO): Promise<void> {
  return request.put(`/api/warehouse/${data.id}`, data)
}

/** 删除仓库 */
export function deleteWarehouse(id: number): Promise<void> {
  return request.delete(`/api/warehouse/${id}`)
}
