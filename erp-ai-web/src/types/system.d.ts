export interface SysParamItem {
  id: number
  paramCategory: string
  paramKey: string
  paramValue: string
  valueType: number // 1=string, 2=number, 3=boolean, 4=JSON, 5=date
  description: string
  sortOrder: number
  isSystem: number // 0=no, 1=yes
}

export interface SysParamQuery {
  category: string
}

export interface SysParamCreateDTO {
  category: string
  key: string
  value: string
  valueType?: number
  description?: string
}

export interface SysParamUpdateDTO {
  value: string
  valueType?: number
  description?: string
}

export interface SysParamBatchItem {
  category: string
  key: string
  value: string
}
