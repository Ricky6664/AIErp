import request from '@/utils/request'
import type { PageResult } from '@/types/api'
import type {
  DefinitionQueryDTO,
  DefinitionCreateDTO,
  DefinitionUpdateDTO,
  DefinitionListVO,
  DefinitionDetailVO,
  InstanceQueryDTO,
  InstanceCreateDTO,
  InstanceVO
} from '@/api/types/approval'

/** 分页查询审批定义列表 */
export function getDefinitionPage(
  params: DefinitionQueryDTO
): Promise<PageResult<DefinitionListVO>> {
  return request.get('/api/approval/definition/page', { params })
}

/** 查询审批定义详情 */
export function getDefinitionDetail(id: number): Promise<DefinitionDetailVO> {
  return request.get(`/api/approval/definition/${id}`)
}

/** 新增审批定义 */
export function createDefinition(data: DefinitionCreateDTO): Promise<number> {
  return request.post('/api/approval/definition', data)
}

/** 修改审批定义 */
export function updateDefinition(data: DefinitionUpdateDTO): Promise<void> {
  return request.put(`/api/approval/definition/${data.id}`, data)
}

/** 删除审批定义 */
export function deleteDefinition(id: number): Promise<void> {
  return request.delete(`/api/approval/definition/${id}`)
}

/** 分页查询审批实例列表 */
export function getInstancePage(params: InstanceQueryDTO): Promise<PageResult<InstanceVO>> {
  return request.get('/api/approval/instance', { params })
}

/** 查询审批实例详情 */
export function getInstanceDetail(id: number): Promise<InstanceVO> {
  return request.get(`/api/approval/instance/${id}`)
}

/** 提交审批（创建审批实例） */
export function submitInstance(data: InstanceCreateDTO): Promise<number> {
  return request.post('/api/approval/instance', data)
}

/** 撤回审批 */
export function withdrawInstance(id: number): Promise<void> {
  return request.post(`/api/approval/instance/${id}/withdraw`)
}
