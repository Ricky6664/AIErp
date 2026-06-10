/** 公告列表项 */
export interface AnnouncementListItem {
  id: number
  title: string
  content: string
  announcementType: string
  publishTime: string
  isTop: boolean
  status: number
  createTime: string
  updateTime: string
  creatorName: string
}

/** 公告分页查询参数 */
export interface AnnouncementQuery {
  pageNum: number
  pageSize: number
  title?: string
  announcementType?: string
  status?: number
  isTop?: boolean
}

/** 公告创建参数 */
export interface AnnouncementCreateDTO {
  title: string
  content?: string
  announcementType?: string
  publishTime?: string
  isTop?: boolean
  status?: number
}

/** 公告更新参数 */
export interface AnnouncementUpdateDTO extends AnnouncementCreateDTO {
  id: number
}

/** 公告分页结果 */
export interface AnnouncementPageResult {
  records: AnnouncementListItem[]
  total: number
  pageNum: number
  pageSize: number
}
