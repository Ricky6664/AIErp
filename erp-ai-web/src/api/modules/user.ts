import request from '@/utils/request'
import type { UserListItem, UserPageQuery } from '@/types/user'

interface UserPageResult {
  list: UserListItem[]
  total: number
  pageNum: number
  pageSize: number
}

/** 分页查询用户列表 */
export function getUserPageList(params: UserPageQuery): Promise<UserPageResult> {
  return request.get('/api/system/user', { params }) as Promise<UserPageResult>
}

/** 查询用户详情 */
export function getUserDetail(id: number): Promise<UserListItem> {
  return request.get(`/api/system/user/${id}`)
}

/** 新增用户 */
export function createUser(data: Record<string, unknown>): Promise<number> {
  return request.post('/api/system/user', data)
}

/** 修改用户 */
export function updateUser(id: number, data: Record<string, unknown>): Promise<void> {
  return request.put(`/api/system/user/${id}`, data)
}

/** 删除用户 */
export function deleteUser(id: number): Promise<void> {
  return request.delete(`/api/system/user/${id}`)
}

/** 修改用户状态 */
export function updateUserStatus(id: number, status: string): Promise<void> {
  return request.put(`/api/system/user/${id}/status`, { status })
}

/** 解锁用户 */
export function unlockUser(id: number): Promise<void> {
  return request.put(`/api/system/user/${id}/unlock`)
}

/** 管理员重置用户密码 */
export function resetUserPassword(id: number): Promise<string> {
  return request.post(`/api/system/user/${id}/reset-password`)
}
