import request from '@/utils/request'
import type { OnlineDeviceQuery, OnlineDevicePageResult } from '@/api/types/onlineDevice'

/** 分页查询在线设备列表 */
export function getOnlineDevicePageApi(params: OnlineDeviceQuery): Promise<OnlineDevicePageResult> {
  return request.get('/api/system/online-devices/page', {
    params
  }) as Promise<OnlineDevicePageResult>
}

/** 强制下线设备 */
export function kickDeviceApi(tokenId: string): Promise<void> {
  return request.post(`/api/system/online-devices/${tokenId}/kick`)
}

/** 批量强制下线设备 */
export function batchKickDeviceApi(tokenIds: string[]): Promise<void> {
  return Promise.all(tokenIds.map((id) => kickDeviceApi(id))) as Promise<unknown> as Promise<void>
}
