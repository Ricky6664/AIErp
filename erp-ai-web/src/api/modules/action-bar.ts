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
