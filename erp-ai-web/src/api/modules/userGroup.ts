import request from '@/utils/request'
import type {
  UserGroupPageQuery,
  UserGroupPageResult,
  UserGroupDetail,
  UserGroupCreateDTO,
  UserGroupUpdateDTO
} from '@/types/userGroup'

/** 分页查询用户组列表 */
export function getUserGroupPageList(params: UserGroupPageQuery): Promise<UserGroupPageResult> {
  return request.get('/api/system/user-group', { params }) as Promise<UserGroupPageResult>
}

/** 查询用户组详情 */
export function getUserGroupDetail(id: number): Promise<UserGroupDetail> {
  return request.get(`/api/system/user-group/${id}`)
}

/** 新增用户组 */
export function createUserGroup(data: UserGroupCreateDTO): Promise<number> {
  return request.post('/api/system/user-group', data)
}

/** 修改用户组 */
export function updateUserGroup(id: number, data: UserGroupUpdateDTO): Promise<void> {
  return request.put(`/api/system/user-group/${id}`, data)
}

/** 删除用户组 */
export function deleteUserGroup(id: number): Promise<void> {
  return request.delete(`/api/system/user-group/${id}`)
}

/** 修改用户组启用状态 */
export function updateUserGroupStatus(id: number, isEnabled: boolean): Promise<void> {
  return request.put(`/api/system/user-group/${id}/status`, { isEnabled })
}

/** 检查组编码唯一性 */
export function checkGroupCode(groupCode: string, excludeId?: number): Promise<boolean> {
  return request.get('/api/system/user-group/check-code', {
    params: { groupCode, excludeId }
  })
}
