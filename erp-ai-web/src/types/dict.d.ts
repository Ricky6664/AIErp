export interface DictItem {
  dictLabel: string
  dictValue: string
  dictType: string
  cssClass?: string
  listClass?: string
  isDefault?: string
  dictSort: number
}

export interface IParamState {
  dictMap: Record<string, DictItem[]>
  systemConfig: Record<string, string>
  loading: boolean
}
