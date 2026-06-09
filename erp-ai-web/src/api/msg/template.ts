import request from '@/utils/request'
import type { PageResult } from '@/types/api'
import type { TemplateListVO, TemplateQueryDTO, TemplateFormDTO } from '@/types/msg'

/** 分页查询消息模板 */
export function getTemplatePage(params: TemplateQueryDTO): Promise<PageResult<TemplateListVO>> {
  return request.get('/api/message/template', { params })
}

/** 新增消息模板 */
export function createTemplate(data: TemplateFormDTO): Promise<void> {
  return request.post('/api/message/template', data)
}

/** 修改消息模板 */
export function updateTemplate(data: TemplateFormDTO): Promise<void> {
  return request.put(`/api/message/template/${data.id}`, data)
}

/** 删除消息模板 */
export function deleteTemplate(id: number): Promise<void> {
  return request.delete(`/api/message/template/${id}`)
}
