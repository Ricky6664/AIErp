import request from '@/utils/request'

export interface BankAccountVO {
  id: number
  accountName: string
  bankAccountNo: string
  bankName: string
  bankBranch: string
  currencyId: number
  accountType: string
  status: number
  createTime: string
  updateTime: string
}

export interface BankAccountQueryDTO {
  accountName?: string
  bankName?: string
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

export function getBankAccountPageApi(
  params: BankAccountQueryDTO
): Promise<PageResult<BankAccountVO>> {
  return request.get('/finance/bank-account', { params })
}

export function getBankAccountByIdApi(id: number): Promise<BankAccountVO> {
  return request.get(`/finance/bank-account/${id}`)
}

export interface BankAccountSaveDTO {
  accountName: string
  bankAccountNo: string
  bankName: string
  bankBranch?: string
  currencyId: number
  accountType: string
  status: number
}

export function createBankAccountApi(data: BankAccountSaveDTO): Promise<BankAccountVO> {
  return request.post('/finance/bank-account', data)
}

export function updateBankAccountApi(id: number, data: BankAccountSaveDTO): Promise<BankAccountVO> {
  return request.put(`/finance/bank-account/${id}`, data)
}

export function updateBankAccountStatusApi(id: number, status: number): Promise<void> {
  return request.put(`/finance/bank-account/${id}/status`, { status })
}

export function deleteBankAccountApi(id: number): Promise<void> {
  return request.delete(`/finance/bank-account/${id}`)
}
