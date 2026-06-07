/** 仓库查询参数 */
export interface WarehouseQueryDTO {
  warehouseName?: string
  warehouseType?: string
  status?: number
  pageNum?: number
  pageSize?: number
}

/** 仓库创建参数 */
export interface WarehouseCreateDTO {
  warehouseCode: string
  warehouseName: string
  warehouseType: string
  address?: string
  managerId?: number
  phone?: string
  status?: number
}

/** 仓库更新参数 */
export interface WarehouseUpdateDTO extends Partial<Omit<WarehouseCreateDTO, 'warehouseCode'>> {
  id: number
}

/** 仓库列表项 */
export interface WarehouseListVO {
  id: number
  warehouseCode: string
  warehouseName: string
  warehouseType: string
  address: string
  managerId: number
  phone: string
  status: number
  createTime: string
  updateTime: string
}

/** 仓库详情 */
export type WarehouseDetailVO = WarehouseListVO
