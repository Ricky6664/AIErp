/** 消息列表项 */
export interface MessageListVO {
  id: number
  messageTitle: string
  messageContent?: string
  readStatus: number
  msgTypeId: number
  createTime: string
}

/** 消息查询参数 */
export interface MessageQueryDTO {
  typeId?: string
  keyword?: string
  pageNum: number
  pageSize: number
}

/** 未读数量统计 */
export interface UnreadCountVO {
  all: number
  system: number
  business: number
  warning: number
  todo: number
}
