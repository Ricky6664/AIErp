import request from '@/utils/request'

export interface RecruitmentVO {
  id: number
  positionName: string
  departmentName: string
  recruitNum: number
  salaryRange: string
  requirements: string
  recruitStatus: string
  deadline: string
  createdAt: string
}

export interface RecruitmentQueryDTO {
  pageNum?: number
  pageSize?: number
  positionName?: string
  departmentName?: string
  recruitStatus?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface RecruitmentCreateDTO {
  positionName: string
  departmentName?: string
  recruitNum?: number
  salaryRange?: string
  requirements?: string
  recruitStatus?: string
  deadline?: string
}

export interface RecruitmentUpdateDTO extends RecruitmentCreateDTO {
  id: number
}

export function getRecruitmentPageApi(
  query: RecruitmentQueryDTO
): Promise<PageResult<RecruitmentVO>> {
  return request.get('/api/hrm/recruitment', { params: query })
}

export function getRecruitmentByIdApi(id: number): Promise<RecruitmentVO> {
  return request.get(`/api/hrm/recruitment/${id}`)
}

export function createRecruitmentApi(data: RecruitmentCreateDTO): Promise<RecruitmentVO> {
  return request.post('/api/hrm/recruitment', data)
}

export function updateRecruitmentApi(data: RecruitmentUpdateDTO): Promise<RecruitmentVO> {
  return request.put(`/api/hrm/recruitment/${data.id}`, data)
}

export function deleteRecruitmentApi(id: number): Promise<void> {
  return request.delete(`/api/hrm/recruitment/${id}`)
}

export function updateRecruitmentStatusApi(id: number, status: string): Promise<void> {
  return request.put(`/api/hrm/recruitment/${id}/status`, { status })
}
