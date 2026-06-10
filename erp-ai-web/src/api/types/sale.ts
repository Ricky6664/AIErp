/** 销售订单查询参数 */
export interface SaleOrderQueryDTO {
  orderNo?: string
  customerId?: number
  status?: number
  startDate?: string
  endDate?: string
  pageNum?: number
  pageSize?: number
}

/** 销售订单创建参数 */
export interface SaleOrderCreateDTO {
  customerId: number
  items: SaleOrderItemDTO[]
  remark?: string
}

/** 销售订单明细项 */
export interface SaleOrderItemDTO {
  productId: number
  quantity: number
  unitPrice: number
}

/** 销售订单更新参数 */
export interface SaleOrderUpdateDTO extends Partial<Omit<SaleOrderCreateDTO, 'items'>> {
  id: number
}

/** 销售订单列表项 */
export interface SaleOrderListVO {
  id: number
  orderNo: string
  customerName?: string
  totalAmount: number
  status: number
  createTime: string
}

/** 销售订单详情 */
export interface SaleOrderDetailVO extends SaleOrderListVO {
  items: SaleOrderItemVO[]
  remark?: string
  updateTime: string
}

/** 销售订单明细项视图 */
export interface SaleOrderItemVO {
  id: number
  orderId: number
  productId: number
  productName: string
  quantity: number
  unitPrice: number
  subtotal: number
}

/** 导入结果 */
export interface ImportResultVO {
  successCount: number
  failCount: number
  errorMessages?: string[]
}
