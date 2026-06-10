import request from '@/utils/request'

export interface EmployeeArchiveVO {
  id: number
  employeeId: number
  employeeName: string
  education: string
  major: string
  school: string
  emergencyContact: string
  emergencyPhone: string
  address: string
  bankCardNumber: string
  bankName: string
  socialSecurityAccount: string
  archiveDate: string
  status: number
  createTime: string
  updateTime: string
}

export interface EmployeeArchiveQueryDTO {
  pageNum?: number
  pageSize?: number
  employeeName?: string
  education?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface EmployeeArchiveCreateDTO {
  employeeId?: number
  employeeName: string
  education?: string
  major?: string
  school?: string
  emergencyContact?: string
  emergencyPhone?: string
  address?: string
  bankCardNumber?: string
  bankName?: string
  socialSecurityAccount?: string
  archiveDate?: string
  status?: number
}

export interface EmployeeArchiveUpdateDTO extends EmployeeArchiveCreateDTO {
  id: number
}

export function getEmployeeArchivePageApi(
  query: EmployeeArchiveQueryDTO
): Promise<PageResult<EmployeeArchiveVO>> {
  return request.get('/api/hrm/employee-archive', { params: query })
}

export function getEmployeeArchiveByIdApi(id: number): Promise<EmployeeArchiveVO> {
  return request.get(`/api/hrm/employee-archive/${id}`)
}

export function createEmployeeArchiveApi(
  data: EmployeeArchiveCreateDTO
): Promise<EmployeeArchiveVO> {
  return request.post('/api/hrm/employee-archive', data)
}

export function updateEmployeeArchiveApi(
  data: EmployeeArchiveUpdateDTO
): Promise<EmployeeArchiveVO> {
  return request.put(`/api/hrm/employee-archive/${data.id}`, data)
}

export function deleteEmployeeArchiveApi(id: number): Promise<void> {
  return request.delete(`/api/hrm/employee-archive/${id}`)
}

export function updateEmployeeArchiveStatusApi(id: number, status: number): Promise<void> {
  return request.put(`/api/hrm/employee-archive/${id}/status`, { status })
}
