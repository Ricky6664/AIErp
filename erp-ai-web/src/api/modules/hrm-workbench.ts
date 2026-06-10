import request from '@/utils/request'

export interface TrendItem {
  month: string
  count: number
}

export interface HrmWorkbenchVO {
  totalEmployees: number
  activeEmployees: number
  newHiresThisMonth: number
  openRecruitments: number
  totalMonthlySalary: number
  departmentCount: number
  employeeMonthlyTrend: TrendItem[]
  departmentDistribution: Record<string, number>
  recruitmentStatusDistribution: Record<string, number>
  attendanceMonthlyTrend: TrendItem[]
}

export function getHrmWorkbenchApi(): Promise<HrmWorkbenchVO> {
  return request.get('/api/hrm/workbench')
}
