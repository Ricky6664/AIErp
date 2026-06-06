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

/** 字段配置值保存参数 */
export interface FieldConfigSaveParams {
  /** 视图编码 */
  viewCode: string
  /** 字段名 → 字段值的映射 */
  fields: Record<string, unknown>
  /** 乐观锁版本号 */
  version?: number
}

/** 字段配置值保存结果 */
export interface FieldConfigSaveResult {
  /** 保存后的记录 ID */
  id: string | number
  /** 更新后的版本号 */
  version: number
}

/**
 * 新增字段配置值（POST）
 * @param data 保存参数（viewCode + fields 键值对）
 * @returns 保存结果（id + version）
 */
export function createFieldConfig(data: FieldConfigSaveParams): Promise<FieldConfigSaveResult> {
  return request.post('/api/system/field-config', data) as Promise<FieldConfigSaveResult>
}

/**
 * 更新字段配置值（PUT），携带乐观锁版本号
 * @param id 记录 ID
 * @param data 保存参数（viewCode + fields + version）
 * @returns 保存结果（id + version）
 */
export function updateFieldConfig(
  id: string | number,
  data: FieldConfigSaveParams
): Promise<FieldConfigSaveResult> {
  return request.put(`/api/system/field-config/${id}`, data) as Promise<FieldConfigSaveResult>
}

/** 扩展字段值保存参数 */
export interface ExtFieldSaveParams {
  /** 视图编码 */
  viewCode: string
  /** 主记录 ID */
  recordId: string | number
  /** 扩展字段名 → 字段值的映射 */
  extFields: Record<string, unknown>
  /** 乐观锁版本号 */
  version?: number
}

/** 扩展字段值保存结果 */
export interface ExtFieldSaveResult {
  /** 保存后的记录 ID */
  id: string | number
  /** 更新后的版本号 */
  version: number
}

/**
 * 新增扩展字段值（POST）
 * @param data 保存参数（viewCode + recordId + extFields 键值对）
 * @returns 保存结果（id + version）
 */
export function saveExtensionFields(data: ExtFieldSaveParams): Promise<ExtFieldSaveResult> {
  return request.post('/api/system/field-config/extensions', data) as Promise<ExtFieldSaveResult>
}

/**
 * 更新扩展字段值（PUT），携带乐观锁版本号
 * @param id 记录 ID
 * @param data 保存参数（viewCode + recordId + extFields + version）
 * @returns 保存结果（id + version）
 */
export function updateExtensionFields(
  id: string | number,
  data: ExtFieldSaveParams
): Promise<ExtFieldSaveResult> {
  return request.put(
    `/api/system/field-config/extensions/${id}`,
    data
  ) as Promise<ExtFieldSaveResult>
}
