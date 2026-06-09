import request from '@/utils/request'
import type { WarningDashboardVO } from '@/types/msg'

/** 获取预警看板数据 */
export function getWarningDashboard(module?: string): Promise<WarningDashboardVO> {
  return request.get('/api/message/alert', { params: module ? { module } : undefined })
}

/** 处理预警 */
export function handleWarning(id: number): Promise<void> {
  return request.post(`/api/message/alert/${id}/handle`)
}
