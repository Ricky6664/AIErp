import request from '@/utils/request'

export interface VoucherWordVO {
  id: number
  wordName: string
  wordCode: string
  sortOrder: number
  status: number
  createTime: string
  updateTime: string
}

export interface VoucherWordQueryDTO {
  wordName?: string
  status?: number
  pageNum?: number
  pageSize?: number
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}

export function getVoucherWordPageApi(
  params: VoucherWordQueryDTO
): Promise<PageResult<VoucherWordVO>> {
  return request.get('/finance/voucher-word', { params })
}

export function getVoucherWordByIdApi(id: number): Promise<VoucherWordVO> {
  return request.get(`/finance/voucher-word/${id}`)
}

export interface VoucherWordSaveDTO {
  wordName: string
  wordCode: string
  sortOrder: number
  status: number
}

export function createVoucherWordApi(data: VoucherWordSaveDTO): Promise<VoucherWordVO> {
  return request.post('/finance/voucher-word', data)
}

export function updateVoucherWordApi(id: number, data: VoucherWordSaveDTO): Promise<VoucherWordVO> {
  return request.put(`/finance/voucher-word/${id}`, data)
}

export function updateVoucherWordStatusApi(id: number, status: number): Promise<void> {
  return request.put(`/finance/voucher-word/${id}/status`, { status })
}

export function deleteVoucherWordApi(id: number): Promise<void> {
  return request.delete(`/finance/voucher-word/${id}`)
}
