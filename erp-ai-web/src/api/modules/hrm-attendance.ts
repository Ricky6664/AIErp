import request from '@/utils/request'

export interface AttendanceVO {
  id: number
  employeeId: number
  employeeName?: string
  attendanceDate: string
  checkInTime?: string
  checkOutTime?: string
  workHours?: number
  attendanceType?: string
  overtimeHours?: number
  createTime?: string
}

export interface AttendanceQueryDTO {
  pageNum?: number
  pageSize?: number
  employeeId?: number
  employeeName?: string
  attendanceDateStart?: string
  attendanceDateEnd?: string
  attendanceType?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface AttendanceCreateDTO {
  employeeId: number
  attendanceDate: string
  checkInTime?: string
  checkOutTime?: string
  workHours?: number
  attendanceType?: string
  overtimeHours?: number
}

export interface AttendanceUpdateDTO extends AttendanceCreateDTO {
  id: number
}

export function getAttendancePageApi(query: AttendanceQueryDTO): Promise<PageResult<AttendanceVO>> {
  return request.get('/api/hrm/attendance', { params: query })
}

export function getAttendanceByIdApi(id: number): Promise<AttendanceVO> {
  return request.get(`/api/hrm/attendance/${id}`)
}

export function createAttendanceApi(data: AttendanceCreateDTO): Promise<AttendanceVO> {
  return request.post('/api/hrm/attendance', data)
}

export function updateAttendanceApi(data: AttendanceUpdateDTO): Promise<AttendanceVO> {
  return request.put(`/api/hrm/attendance/${data.id}`, data)
}

export function deleteAttendanceApi(id: number): Promise<void> {
  return request.delete(`/api/hrm/attendance/${id}`)
}
