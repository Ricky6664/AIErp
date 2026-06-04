/** 用户组列表项 */
export interface UserGroupListItem {
  id: number
  groupCode: string
  groupName: string
  groupDesc: string
  memberCount: number
  isEnabled: boolean
  sortOrder: number
  createTime: string
}

/** 用户组列表查询参数 */
export interface UserGroupPageQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  enabled?: boolean
}

/** 用户组列表分页结果 */
export interface UserGroupPageResult {
  records: UserGroupListItem[]
  total: number
  pageNum: number
  pageSize: number
}

/** 用户组详情 */
export interface UserGroupDetail {
  id: number
  groupCode: string
  groupName: string
  groupDesc: string
  isEnabled: boolean
  sortOrder: number
  createTime: string
  updateTime: string
}

/** 新增用户组 */
export interface UserGroupCreateDTO {
  groupCode: string
  groupName: string
  groupDesc?: string
  isEnabled?: boolean
  sortOrder?: number
}

/** 修改用户组 */
export interface UserGroupUpdateDTO {
  groupCode?: string
  groupName?: string
  groupDesc?: string
  isEnabled?: boolean
  sortOrder?: number
}
