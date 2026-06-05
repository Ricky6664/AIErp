import request from '@/utils/request'

/**
 * 批量操作请求 DTO
 */
export interface BatchActionDTO {
  /** 操作类型 */
  action: string
  /** 选中记录的ID列表 */
  ids: (string | number)[]
  /** 额外参数 */
  params?: Record<string, unknown>
}

/**
 * 表单提交 DTO
 */
export interface SubmitFormDTO {
  /** 业务模块名 */
  module: string
  /** 表单数据 */
  data: Record<string, unknown>
  /** 记录ID（编辑时传入） */
  id?: string | number
}

/**
 * 批量删除
 */
export function batchDeleteApi(data: BatchActionDTO): Promise<number> {
  return request.post('/api/common/batch-delete', data)
}

/**
 * 批量审核
 */
export function batchAuditApi(data: BatchActionDTO): Promise<number> {
  return request.post('/api/common/batch-audit', data)
}

/**
 * 导出数据
 */
export function exportDataApi(module: string, params?: Record<string, unknown>): Promise<Blob> {
  return request.get(`/api/common/export/${module}`, {
    params,
    responseType: 'blob'
  })
}

/**
 * 导入数据（获取模板）
 */
export function getImportTemplateApi(module: string): Promise<Blob> {
  return request.get(`/api/common/import/template/${module}`, {
    responseType: 'blob'
  })
}

/**
 * 表单提交（新增/编辑）
 */
export function submitFormApi(data: SubmitFormDTO): Promise<unknown> {
  const url = data.id ? `/api/${data.module}/update` : `/api/${data.module}/add`
  return request.post(url, { ...data.data, id: data.id })
}

/**
 * 保存草稿
 */
export function saveDraftApi(module: string, data: Record<string, unknown>): Promise<unknown> {
  return request.post(`/api/${module}/draft`, data)
}

/**
 * 反审核
 */
export function batchAntiAuditApi(data: BatchActionDTO): Promise<number> {
  return request.post('/api/common/batch-anti-audit', data)
}
