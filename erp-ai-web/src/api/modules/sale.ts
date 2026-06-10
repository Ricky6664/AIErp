import request from '@/utils/request'
import type { PageResult } from '@/types/api'
import type {
  SaleOrderQueryDTO,
  SaleOrderCreateDTO,
  SaleOrderUpdateDTO,
  SaleOrderListVO,
  SaleOrderDetailVO,
  ImportResultVO
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

/** 批量删除销售订单 */
export function batchDeleteSaleOrder(ids: number[]): Promise<void> {
  return request.put('/api/sale/order/batch-delete', { ids })
}

/** 导入销售订单 */
export function importSaleOrder(file: File): Promise<ImportResultVO> {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/sale/order/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000
  })
}

/** 导出销售订单 */
export function exportSaleOrder(params: SaleOrderQueryDTO): Promise<Blob> {
  return request.get('/api/sale/order/export', {
    params,
    responseType: 'blob'
  })
}
