import request from '@/utils/request'
import type { PageResult } from '@/types/api'
import type {
  SaleOrderQueryDTO,
  SaleOrderCreateDTO,
  SaleOrderUpdateDTO,
  SaleOrderListVO,
  SaleOrderDetailVO
} from '@/api/types/sale'

/** 分页查询销售订单列表 */
export function getSaleOrderPage(params: SaleOrderQueryDTO): Promise<PageResult<SaleOrderListVO>> {
  return request.get('/api/sale/order/page', { params })
}

/** 查询销售订单详情 */
export function getSaleOrderDetail(id: number): Promise<SaleOrderDetailVO> {
  return request.get(`/api/sale/order/${id}`)
}

/** 新增销售订单 */
export function createSaleOrder(data: SaleOrderCreateDTO): Promise<number> {
  return request.post('/api/sale/order', data)
}

/** 修改销售订单 */
export function updateSaleOrder(data: SaleOrderUpdateDTO): Promise<void> {
  return request.put(`/api/sale/order/${data.id}`, data)
}

/** 删除销售订单 */
export function deleteSaleOrder(id: number): Promise<void> {
  return request.delete(`/api/sale/order/${id}`)
}
