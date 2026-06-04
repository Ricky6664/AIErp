import request from '@/utils/request'

export interface RoleItem {
  id: number
  roleCode: string
  roleName: string
  roleDesc?: string
  isEnabled: boolean
}

/** 获取角色列表（支持按启用状态筛选） */
export async function getRoleList(isEnabled?: boolean): Promise<RoleItem[]> {
  const res = (await request.get('/api/system/role', {
    params: { pageSize: 999, isEnabled }
  })) as { list?: RoleItem[] }
  return res.list ?? []
}

/** 获取角色的互斥角色ID列表 */
export async function getRoleExclusions(roleId: number): Promise<number[]> {
  const res = (await request.get(`/api/system/role/exclusion/${roleId}`)) as unknown
  return (res as { data?: number[] })?.data ?? []
}

/** 检查两个角色是否互斥 */
export async function checkRoleExclusion(roleA: number, roleB: number): Promise<boolean> {
  const res = (await request.get('/api/system/role/exclusion/check', {
    params: { roleA, roleB }
  })) as unknown
  return (res as { data?: boolean })?.data ?? false
}
