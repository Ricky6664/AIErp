import request from '@/utils/request'

export interface TrendItem {
  month: string
  count: number
}

export interface FinanceWorkbenchVO {
  currencyRateCount: number
  bankAccountCount: number
  activeBankAccountCount: number
  accountCount: number
  leafAccountCount: number
  voucherWordCount: number
  activeVoucherWordCount: number
  accountTypeDistribution: Record<string, number>
  monthlyTrend: TrendItem[]
}

export function getFinanceWorkbenchApi(): Promise<FinanceWorkbenchVO> {
  return request.get('/api/finance/workbench')
}
