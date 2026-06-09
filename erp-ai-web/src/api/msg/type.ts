import request from '@/utils/request'
import type { TypeListVO, TypeFormDTO } from '@/types/msg'

/** 获取全部消息类型列表（前端构建树） */
export function getTypeList(): Promise<TypeListVO[]> {
  return request.get('/api/message/type')
}

/** 新增消息类型 */
export function createType(data: TypeFormDTO): Promise<void> {
  return request.post('/api/message/type', data)
}

/** 修改消息类型 */
export function updateType(data: TypeFormDTO): Promise<void> {
  return request.put(`/api/message/type/${data.id}`, data)
}

/** 删除消息类型 */
export function deleteType(id: number): Promise<void> {
  return request.delete(`/api/message/type/${id}`)
}
