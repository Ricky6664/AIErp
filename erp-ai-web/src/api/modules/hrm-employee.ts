import request from '@/utils/request'

export interface EmployeeVO {
  id: number
  employeeNo: string
  name: string
  gender: string
  idCard: string
  phone: string
  email: string
  departmentId: number
  positionId: number
  entryDate: string
  employeeStatus: string
  createTime: string
  updateTime: string
}

export interface EmployeeQueryDTO {
  pageNum?: number
  pageSize?: number
  name?: string
  employeeStatus?: string
  departmentId?: number
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface EmployeeCreateDTO {
  employeeNo: string
  name: string
  gender?: string
  idCard?: string
  phone?: string
  email?: string
  departmentId?: number
  positionId?: number
  entryDate?: string
  employeeStatus?: string
}

export interface EmployeeUpdateDTO extends EmployeeCreateDTO {
  id: number
}

export function getEmployeePageApi(query: EmployeeQueryDTO): Promise<PageResult<EmployeeVO>> {
  return request.get('/api/hrm/employee', { params: query })
}

export function getEmployeeByIdApi(id: number): Promise<EmployeeVO> {
  return request.get(`/api/hrm/employee/${id}`)
}

export function createEmployeeApi(data: EmployeeCreateDTO): Promise<EmployeeVO> {
  return request.post('/api/hrm/employee', data)
}

export function updateEmployeeApi(id: number, data: EmployeeUpdateDTO): Promise<EmployeeVO> {
  return request.put(`/api/hrm/employee/${id}`, data)
}

export function deleteEmployeeApi(id: number): Promise<void> {
  return request.delete(`/api/hrm/employee/${id}`)
}
