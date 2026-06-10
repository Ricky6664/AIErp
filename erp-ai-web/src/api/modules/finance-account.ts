import request from '@/utils/request'

export interface AccountVO {
  id: number
  accountCode: string
  accountName: string
  parentId: number
  level: number
  accountType: number
  category: string
  balanceDirection: number
  isLeaf: boolean
  isCash: boolean
  isBank: boolean
  isForeignCurrency: string
  isAuxiliary: string
  status: number
  createTime: string
  updateTime: string
}

export interface AccountQueryDTO {
  accountName?: string
  accountType?: number
  status?: number
  parentId?: number
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

export interface AccountTreeVO {
  id: number
  accountCode: string
  accountName: string
  level: number
  isLeaf: boolean
  children?: AccountTreeVO[]
}

export function getAccountTreeApi(): Promise<AccountTreeVO[]> {
  return request.get('/finance/account/tree')
}

export function getAccountPageApi(params: AccountQueryDTO): Promise<PageResult<AccountVO>> {
  return request.get('/finance/account', { params })
}

export function getAccountByIdApi(id: number): Promise<AccountVO> {
  return request.get(`/finance/account/${id}`)
}

export interface AccountSaveDTO {
  parentId: number
  accountCode: string
  accountName: string
  accountType: number
  category: string
  balanceDirection: number
  isCash?: boolean
  isBank?: boolean
  isForeignCurrency?: boolean | string
  isAuxiliary?: boolean | string
  status: number
}

export function createAccountApi(data: AccountSaveDTO): Promise<AccountVO> {
  return request.post('/finance/account', data)
}

export function updateAccountApi(id: number, data: AccountSaveDTO): Promise<AccountVO> {
  return request.put(`/finance/account/${id}`, data)
}

export function updateAccountStatusApi(id: number, status: number): Promise<void> {
  return request.put(`/finance/account/${id}/status`, { status })
}

export function deleteAccountApi(id: number): Promise<void> {
  return request.delete(`/finance/account/${id}`)
}
