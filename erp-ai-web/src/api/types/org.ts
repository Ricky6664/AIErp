/** 公司查询参数 */
export interface CompanyQueryDTO {
  companyName?: string
  creditCode?: string
  status?: number
  pageNum?: number
  pageSize?: number
}

/** 公司创建参数 */
export interface CompanyCreateDTO {
  companyName: string
  companyShortName?: string
  creditCode?: string
  legalPerson?: string
  registeredCapital?: number
  address?: string
  phone?: string
}

/** 公司更新参数 */
export interface CompanyUpdateDTO {
  id: number
  companyName?: string
  companyShortName?: string
  creditCode?: string
  legalPerson?: string
  registeredCapital?: number
  address?: string
  phone?: string
}

/** 公司列表项 */
export interface CompanyListVO {
  id: number
  companyName: string
  companyShortName?: string
  creditCode?: string
  legalPerson?: string
  phone?: string
  enableFlag?: boolean
  status: number
  createTime: string
}

/** 公司详情 */
export interface CompanyDetailVO {
  id: number
  companyName: string
  companyShortName?: string
  creditCode?: string
  legalPerson?: string
  registeredCapital?: number
  address?: string
  phone?: string
  enableFlag?: boolean
  createTime: string
  updateTime: string
  createdByName?: string
  updatedByName?: string
}

/** 导入结果 */
export interface ImportResultVO {
  successCount: number
  failCount: number
  errorMessages?: string[]
}
