import request from '@/utils/request'
import type { FieldConfigListResponse, FieldConfigItem } from '@/api/types/fieldConfig'

/**
 * 获取指定单据的字段配置列表
 * @param viewCode 视图编码（对应业务单据类型，如 purchase_order、sale_order）
 * @returns 字段配置列表（含字段类型映射、校验规则、联动规则）
 */
export function getFieldConfigList(viewCode: string): Promise<FieldConfigListResponse> {
  return request.get(`/api/system/field-config/${viewCode}`) as Promise<FieldConfigListResponse>
}

/**
 * 获取指定单据的单个字段配置
 * @param viewCode 视图编码
 * @param field 字段名
 * @returns 字段配置项
 */
export function getFieldConfigItem(viewCode: string, field: string): Promise<FieldConfigItem> {
  return request.get(`/api/system/field-config/${viewCode}/${field}`) as Promise<FieldConfigItem>
}
