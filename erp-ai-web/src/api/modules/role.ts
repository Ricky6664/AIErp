import request from '@/utils/request'

export interface RoleItem {
  id: number
  roleCode: string
  roleName: string
  isEnabled: boolean
}

/** 获取角色列表（支持按启用状态筛选） */
export async function getRoleList(isEnabled?: boolean): Promise<RoleItem[]> {
  const res = (await request.get('/api/system/role', {
    params: { pageSize: 999, isEnabled }
  })) as { list?: RoleItem[] }
  return res.list ?? []
}
