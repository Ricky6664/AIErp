import request from '@/utils/request'
import type { LoginLogItem, LoginLogQuery, LoginLogPageResult } from '@/api/types/loginLog'

/** 查询登录日志分页列表 */
export function getLoginLogPageApi(params: LoginLogQuery): Promise<LoginLogPageResult> {
  return request.get('/api/system/login-log', { params }) as Promise<LoginLogPageResult>
}

/** 清空所有登录日志（超管） */
export function clearLoginLogApi(): Promise<void> {
  return request.delete('/api/system/login-log')
}

/** 导出登录日志（返回全部符合条件的记录） */
export function exportLoginLogApi(
  params: Omit<LoginLogQuery, 'pageNum' | 'pageSize'>
): Promise<LoginLogItem[]> {
  return request.get('/api/system/login-log/export', { params }) as Promise<LoginLogItem[]>
}
