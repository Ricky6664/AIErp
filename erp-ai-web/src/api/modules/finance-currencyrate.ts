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

export interface CurrencyRateSaveDTO {
  currencyCode: string
  currencyName: string
  currencySymbol?: string
  exchangeRate: number
  rateType?: number
  effectiveDate?: string
}

export function createCurrencyRateApi(data: CurrencyRateSaveDTO): Promise<CurrencyRateVO> {
  return request.post('/finance/currency-rate', data)
}

export function updateCurrencyRateApi(
  id: number,
  data: CurrencyRateSaveDTO
): Promise<CurrencyRateVO> {
  return request.put(`/finance/currency-rate/${id}`, data)
}

export function checkCurrencyCodeApi(code: string): Promise<boolean> {
  return request.get('/finance/currency-rate/check-code', {
    params: { code }
  })
}

export function deleteCurrencyRateApi(id: number): Promise<void> {
  return request.delete(`/finance/currency-rate/${id}`)
}
