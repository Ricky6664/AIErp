import request from '@/utils/request'
import type { PageResult } from '@/types/api.d'
import type {
  LocationQueryDTO,
  LocationCreateDTO,
  LocationUpdateDTO,
  LocationListVO,
  LocationDetailVO
} from '@/api/types/location'

/** 分页查询库位列表 */
export function getLocationPage(params: LocationQueryDTO): Promise<PageResult<LocationListVO>> {
  return request.get('/api/warehouse/location/page', { params })
}

/** 查询库位详情 */
export function getLocationDetail(id: number): Promise<LocationDetailVO> {
  return request.get(`/api/warehouse/location/${id}`)
}

/** 新增库位 */
export function createLocation(data: LocationCreateDTO): Promise<number> {
  return request.post('/api/warehouse/location', data)
}

/** 修改库位 */
export function updateLocation(data: LocationUpdateDTO): Promise<void> {
  return request.put(`/api/warehouse/location/${data.id}`, data)
}

/** 删除库位 */
export function deleteLocation(id: number): Promise<void> {
  return request.delete(`/api/warehouse/location/${id}`)
}
