import request from '@/utils/request'
import type { DictItem } from '@/types/dict'

/** 根据字典类型获取字典数据列表 */
export function getDictDataApi(dictType: string): Promise<DictItem[]> {
  return request.get(`/api/system/dict/data/type/${dictType}`)
}

/** 获取所有系统参数配置 */
export function getSystemConfigApi(): Promise<Record<string, string>> {
  return request.get('/api/system/config')
}
