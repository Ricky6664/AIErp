import request from '@/utils/request'
import type { DictItem } from '@/types/dict'
import type {
  SysParamItem,
  SysParamCreateDTO,
  SysParamUpdateDTO,
  SysParamBatchItem
} from '@/types/system'
import type { DeptTreeNode } from './user'

/** 根据字典类型获取字典数据列表 */
export function getDictDataApi(dictType: string): Promise<DictItem[]> {
  return request.get(`/api/system/dict/data/type/${dictType}`)
}

/** 获取所有系统参数配置 */
export function getSystemConfigApi(): Promise<Record<string, string>> {
  return request.get('/api/system/config')
}

// ==================== 系统参数管理 API ====================

/** 按分类查询参数列表 */
export function getParamListApi(category: string): Promise<SysParamItem[]> {
  return request.get(`/api/system/params/category/${category}`)
}

/** 获取单个参数值 */
export function getParamValueApi(category: string, key: string): Promise<string> {
  return request.get(`/api/system/params/${category}/${key}`)
}

/** 新增参数 */
export function createParamApi(data: SysParamCreateDTO): Promise<void> {
  return request.post('/api/system/params', data)
}

/** 修改参数值 */
export function updateParamApi(
  category: string,
  key: string,
  data: SysParamUpdateDTO
): Promise<void> {
  return request.put(`/api/system/params/${category}/${key}`, data)
}

/** 删除参数 */
export function deleteParamApi(category: string, key: string): Promise<boolean> {
  return request.delete(`/api/system/params/${category}/${key}`)
}

/** 手动刷新参数缓存 */
export function refreshParamCacheApi(): Promise<number> {
  return request.post('/api/system/params/refresh')
}

/** 批量更新参数 */
export function batchUpdateParamsApi(data: SysParamBatchItem[]): Promise<void> {
  return request.put('/api/system/params/batch', data)
}

/** 获取部门树 */
export function getDeptTree(): Promise<DeptTreeNode[]> {
  return request.get('/api/system/dept/tree')
}
