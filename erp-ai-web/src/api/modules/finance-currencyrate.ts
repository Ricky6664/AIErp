import request from '@/utils/request'

export interface CurrencyRateVO {
  id: number
  currencyCode: string
  currencyName: string
  currencySymbol: string
  exchangeRate: number
  rateType: number
  effectiveDate: string
  createTime: string
  updateTime: string
}

export interface CurrencyRateQueryDTO {
  currencyCode?: string
  currencyName?: string
  rateType?: number
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

export function getCurrencyRatePageApi(
  params: CurrencyRateQueryDTO
): Promise<PageResult<CurrencyRateVO>> {
  return request.get('/finance/currency-rate', { params })
}

export function getCurrencyRateByIdApi(id: number): Promise<CurrencyRateVO> {
  return request.get(`/finance/currency-rate/${id}`)
}

export function deleteCurrencyRateApi(id: number): Promise<void> {
  return request.delete(`/finance/currency-rate/${id}`)
}
