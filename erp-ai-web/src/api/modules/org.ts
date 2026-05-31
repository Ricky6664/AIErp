import request from '@/utils/request'
import type { PageResult } from '@/types/api'
import type {
  CompanyQueryDTO,
  CompanyCreateDTO,
  CompanyUpdateDTO,
  CompanyListVO,
  CompanyDetailVO
} from '@/api/types/org'

/** 分页查询公司列表 */
export function getCompanyPage(params: CompanyQueryDTO): Promise<PageResult<CompanyListVO>> {
  return request.get('/api/org/company/page', { params })
}

/** 查询公司详情 */
export function getCompanyDetail(id: number): Promise<CompanyDetailVO> {
  return request.get(`/api/org/company/${id}`)
}

/** 新增公司 */
export function createCompany(data: CompanyCreateDTO): Promise<number> {
  return request.post('/api/org/company', data)
}

/** 修改公司 */
export function updateCompany(data: CompanyUpdateDTO): Promise<void> {
  return request.put(`/api/org/company/${data.id}`, data)
}

/** 删除公司 */
export function deleteCompany(id: number): Promise<void> {
  return request.delete(`/api/org/company/${id}`)
}
