import request from '@/utils/request'
import type { PageResult } from '@/types/api'
import type { MessageListVO, MessageQueryDTO, UnreadCountVO } from '@/types/msg'

/** 分页查询消息列表 */
export function getMessagePage(params: MessageQueryDTO): Promise<PageResult<MessageListVO>> {
  return request.get('/api/message/message', { params })
}

/** 标记消息已读 */
export function markRead(id: number): Promise<void> {
  return request.put(`/api/message/message/${id}/read`)
}

/** 全部标记已读 */
export function markAllRead(): Promise<void> {
  return request.put('/api/message/message/read-all')
}

/** 获取未读数量 */
export function getUnreadCount(): Promise<UnreadCountVO> {
  return request.get('/api/message/unread-count')
}
