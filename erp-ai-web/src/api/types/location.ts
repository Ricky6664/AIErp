/** 库位查询参数 */
export interface LocationQueryDTO {
  warehouseId?: number
  locationCode?: string
  locationName?: string
  locationType?: string
  status?: number
  pageNum?: number
  pageSize?: number
}

/** 库位创建参数 */
export interface LocationCreateDTO {
  warehouseId: number
  locationCode: string
  locationName: string
  locationType: string
  sortOrder?: number
  status: number
}

/** 库位更新参数 */
export interface LocationUpdateDTO extends Partial<Omit<LocationCreateDTO, 'locationCode'>> {
  id: number
}

/** 库位列表项 */
export interface LocationListVO {
  id: number
  warehouseId: number
  locationCode: string
  locationName: string
  locationType: string
  sortOrder: number
  status: number
  createTime: string
  updateTime: string
}

/** 库位详情 */
export type LocationDetailVO = LocationListVO
