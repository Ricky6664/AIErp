/** 公司查询参数 */
export interface CompanyQueryDTO {
  name?: string
  code?: string
  status?: number
  pageNum?: number
  pageSize?: number
}

/** 公司创建参数 */
export interface CompanyCreateDTO {
  name: string
  code: string
  shortName?: string
  address?: string
  contactPerson?: string
  contactPhone?: string
  email?: string
  taxNo?: string
  remark?: string
}

/** 公司更新参数 */
export interface CompanyUpdateDTO extends Partial<CompanyCreateDTO> {
  id: number
}

/** 公司列表项 */
export interface CompanyListVO {
  id: number
  name: string
  code: string
  shortName?: string
  contactPerson?: string
  contactPhone?: string
  status: number
  createTime: string
}

/** 公司详情 */
export interface CompanyDetailVO extends CompanyListVO {
  address?: string
  email?: string
  taxNo?: string
  remark?: string
  updateTime: string
}

/** 导入结果 */
export interface ImportResultVO {
  successCount: number
  failCount: number
  errorMessages?: string[]
}
