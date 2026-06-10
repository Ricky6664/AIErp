import request from '@/utils/request'

export interface SalaryDetailItem {
  lineNo?: number
  itemName: string
  itemType: '加项' | '减项'
  amount: number
}

export interface SalaryVO {
  id: number
  employeeId: number
  employeeName?: string
  fiscalYear?: number
  fiscalMonth?: number
  baseSalary: number
  overtimePay?: number
  bonus?: number
  allowance: number
  deduction: number
  netSalary: number
  salaryMonth: string
  paymentStatus?: string
  detailItems?: SalaryDetailItem[]
  createTime?: string
}

export interface SalaryQueryDTO {
  pageNum?: number
  pageSize?: number
  employeeId?: number
  salaryMonth?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface SalaryCreateDTO {
  employeeId: number
  baseSalary?: number
  overtimePay?: number
  bonus?: number
  allowance?: number
  deduction?: number
  salaryMonth: string
  paymentStatus?: string
  detailItems?: SalaryDetailItem[]
}

export interface SalaryUpdateDTO {
  id: number
  employeeId?: number
  baseSalary?: number
  overtimePay?: number
  bonus?: number
  allowance?: number
  deduction?: number
  salaryMonth?: string
  paymentStatus?: string
  detailItems?: SalaryDetailItem[]
}

export function getSalaryPageApi(query: SalaryQueryDTO): Promise<PageResult<SalaryVO>> {
  return request.get('/api/hrm/salary', { params: query })
}

export function getSalaryByIdApi(id: number): Promise<SalaryVO> {
  return request.get(`/api/hrm/salary/${id}`)
}

export function createSalaryApi(data: SalaryCreateDTO): Promise<SalaryVO> {
  return request.post('/api/hrm/salary', data)
}

export function updateSalaryApi(data: SalaryUpdateDTO): Promise<SalaryVO> {
  return request.put(`/api/hrm/salary/${data.id}`, data)
}

export function deleteSalaryApi(id: number): Promise<void> {
  return request.delete(`/api/hrm/salary/${id}`)
}
