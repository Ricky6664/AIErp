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

/** 消息模板列表项 */
export interface TemplateListVO {
  id: number
  templateCode: string
  templateName: string
  channel: string
  enableFlag: boolean
  createTime: string
}

/** 消息模板查询参数 */
export interface TemplateQueryDTO {
  templateCode?: string
  keyword?: string
  channel?: string
  enableFlag?: boolean
  pageNum: number
  pageSize: number
}

/** 消息模板表单数据 */
export interface TemplateFormDTO {
  id?: number
  templateCode: string
  templateName: string
  templateContent: string
  channel: string
  channels: string[]
  enableFlag: boolean
}

/** 消息类型列表项 */
export interface TypeListVO {
  id: number
  typeCode: string
  typeName: string
  parentId: number
  sortNo: number
  icon: string
  enableFlag: boolean
  children?: TypeListVO[]
}

/** 消息类型表单数据 */
export interface TypeFormDTO {
  id?: number
  parentId?: number
  typeCode: string
  typeName: string
  sortNo: number
  icon: string
  enableFlag: boolean
}
