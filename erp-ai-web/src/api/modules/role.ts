import request from '@/utils/request'
import type {
  RolePageQuery,
  RoleCreateDTO,
  RoleUpdateDTO,
  RoleDetail,
  RolePageResult
} from '@/api/types/role'

/** 分页查询角色列表 */
export function getRolePageList(params: RolePageQuery): Promise<RolePageResult> {
  return request.get('/api/system/role', { params }) as Promise<RolePageResult>
}

/** 查询角色详情 */
export function getRoleDetail(id: number): Promise<RoleDetail> {
  return request.get(`/api/system/role/${id}`) as Promise<RoleDetail>
}

/** 新增角色 */
export function createRole(data: RoleCreateDTO): Promise<number> {
  return request.post('/api/system/role', data) as Promise<number>
}

/** 修改角色 */
export function updateRole(id: number, data: RoleUpdateDTO): Promise<void> {
  return request.put(`/api/system/role/${id}`, data) as Promise<void>
}

/** 删除角色 */
export function deleteRole(id: number): Promise<void> {
  return request.delete(`/api/system/role/${id}`) as Promise<void>
}

/** 修改角色启用状态 */
export function updateRoleStatus(id: number, isEnabled: boolean): Promise<void> {
  return request.put(`/api/system/role/${id}/status`, { isEnabled }) as Promise<void>
}

/** 检查角色编码唯一性 */
export function checkRoleCode(roleCode: string, excludeId?: number): Promise<boolean> {
  return request.get('/api/system/role/check-code', {
    params: { roleCode, excludeId }
  }) as Promise<boolean>
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

// ---- 以下为兼容旧接口，保留供 UserForm / UserGroupForm / UserRoleDialog 使用 ----

export interface RoleItem {
  id: number
  roleCode: string
  roleName: string
  roleDesc?: string
  isEnabled: boolean
}

/** 获取角色列表（支持按启用状态筛选，不分页，用于下拉选择等场景） */
export async function getRoleList(isEnabled?: boolean): Promise<RoleItem[]> {
  const res = (await request.get('/api/system/role', {
    params: { pageSize: 999, isEnabled }
  })) as { records?: RoleItem[] }
  return res.records ?? []
}
