import request from '@/utils/request'

/** 部门类型分布项 */
export interface DeptTypeDistribution {
  deptType: string
  count: number
}

/** 各公司部门数量项 */
export interface CompanyDeptCount {
  companyId: number
  companyName: string
  deptCount: number
}

/** 各部门人员分布项 */
export interface DeptStaffCount {
  deptId: number
  deptName: string
  staffCount: number
  percentage: string
}

/** 组织架构工作台聚合数据 */
export interface OrgWorkbenchVO {
  companyCount: number
  departmentCount: number
  positionCount: number
  employeeCount: number
  deptTypeDistribution: DeptTypeDistribution[]
  companyDeptCount: CompanyDeptCount[]
  deptStaffDistribution: DeptStaffCount[]
}

/** 获取组织架构工作台聚合数据 */
export function getOrgWorkbenchApi(): Promise<OrgWorkbenchVO> {
  return request.get('/api/org/workbench')
}
