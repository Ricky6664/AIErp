/**
 * 字段配置（字段类型映射、校验规则、联动规则）
 */
export interface FieldConfig {
  /** 字段名 */
  field: string
  /** 字段显示标题 */
  title?: string
  /** 字段显示标签 */
  label?: string
  /** 字段类型映射（text/number/date/select等） */
  fieldType?: string
  /** 校验规则 */
  rules?: FieldValidationRule[]
  /** 联动规则 */
  linkages?: FieldLinkageRule[]
  /** 是否只读 */
  readonly?: boolean
  /** 是否必填 */
  required?: boolean
  /** 占位提示 */
  placeholder?: string
}

/**
 * 字段校验规则
 */
export interface FieldValidationRule {
  /** 规则类型 */
  type: 'required' | 'min' | 'max' | 'pattern' | 'custom'
  /** 规则参数 */
  value?: unknown
  /** 错误提示 */
  message?: string
  /** 自定义校验函数 */
  validator?: (value: unknown) => boolean | string
}

/**
 * 字段联动规则
 */
export interface FieldLinkageRule {
  /** 触发字段 */
  triggerField: string
  /** 触发条件（值匹配） */
  condition?: (value: unknown) => boolean
  /** 目标字段 */
  targetField: string
  /** 联动动作 */
  action: 'show' | 'hide' | 'enable' | 'disable' | 'setValue' | 'setOptions'
  /** 联动参数 */
  params?: Record<string, unknown>
}

/**
 * 联动条件运算符（JSON可序列化）
 */
export type LinkageConditionOperator =
  | 'eq'
  | 'neq'
  | 'gt'
  | 'gte'
  | 'lt'
  | 'lte'
  | 'in'
  | 'notIn'
  | 'isEmpty'
  | 'isNotEmpty'
  | 'startsWith'
  | 'endsWith'
  | 'contains'

/**
 * 联动条件配置（JSON可序列化形式）
 */
export interface LinkageConditionConfig {
  /** 条件运算符 */
  operator: LinkageConditionOperator
  /** 条件参考值 */
  value?: unknown
}

/**
 * 联动规则配置（JSON可序列化形式）
 */
export interface LinkageRuleConfig {
  /** 触发字段 */
  triggerField: string
  /** 触发条件（JSON可序列化） */
  condition?: LinkageConditionConfig
  /** 目标字段 */
  targetField: string
  /** 联动动作 */
  action: 'show' | 'hide' | 'enable' | 'disable' | 'setValue' | 'setOptions'
  /** 联动参数 */
  params?: Record<string, unknown>
}

/**
 * 单列排序字段
 */
export interface SortField {
  /** 排序字段 */
  field: string
  /** 排序方向 */
  order: 'asc' | 'desc'
}

/**
 * 排序配置
 */
export interface SortConfig {
  /** 排序字段（单列排序简写） */
  field?: string
  /** 排序方向（单列排序简写） */
  order?: 'asc' | 'desc'
  /** 多列排序字段列表（与field/order互斥，优先使用fields） */
  fields?: SortField[]
  /** 是否允许多列排序（Shift+点击表头添加排序列） */
  multiple?: boolean
  /** 排序触发方式 */
  trigger?: 'cell' | 'header'
  /** 是否显示排序图标 */
  showIcon?: boolean
  /** 远程排序（服务端排序） */
  remote?: boolean
}

/**
 * 筛选类型
 */
export type FilterType = 'text' | 'number' | 'date' | 'datetime' | 'select'

/**
 * 筛选配置项
 */
export interface FilterOption {
  label: string
  value: string | number
}

/**
 * 筛选配置
 */
export interface FilterConfig {
  /** 筛选类型 */
  type: FilterType
  /** 筛选选项（select类型时使用） */
  options?: FilterOption[]
  /** 是否多选 */
  multiple?: boolean
}

/**
 * 分页模式
 */
export type PageMode = 'client' | 'server'

/**
 * 合计行计算方法
 */
export type SummaryMethod = 'sum' | 'avg' | 'count' | 'min' | 'max' | 'custom' | 'none'

/**
 * 合计行列配置
 */
export interface SummaryColumnConfig {
  /** 字段名 */
  field: string
  /** 合计计算方法 */
  method: SummaryMethod
  /** 自定义合计函数（method为'custom'时使用，参数为当前页数据列表） */
  customMethod?: (data: unknown[]) => number | string
  /** 合计行值格式化函数 */
  formatter?: (value: unknown) => string
  /** 合计行前缀文本 */
  prefix?: string
  /** 合计行后缀文本 */
  suffix?: string
}

/**
 * 合计行配置
 */
export interface SummaryConfig {
  /** 是否启用合计行 */
  enabled: boolean
  /** 合计行列配置列表 */
  columns?: SummaryColumnConfig[]
  /** 合计行高度 */
  height?: number | string
  /** 合计行首列标签字段（该列显示"合计"标签而非计算结果） */
  labelField?: string
  /** 合计行标签文本（默认"合计"） */
  labelText?: string
}

/**
 * 行尺寸
 */
export type RowSize = 'mini' | 'small' | 'medium' | 'large' | 'loose' | 'x-large' | 'xx-large'

/**
 * 表格边框模式
 */
export type BorderMode = 'inner' | 'full' | 'none'

/**
 * 列对齐
 */
export type ColumnAlign = 'left' | 'center' | 'right'

/**
 * 列固定
 */
export type ColumnFixed = 'left' | 'right'

/**
 * 列格式化函数
 */
export type ColumnFormatter = (value: unknown, row: Record<string, unknown>) => string

/**
 * 表格列配置
 */
export interface ListTableColumn {
  /** 列字段名 */
  field: string
  /** 列标题 */
  title: string
  /** 列宽度 */
  width?: number | string
  /** 最小列宽度 */
  minWidth?: number | string
  /** 是否可排序 */
  sortable?: boolean
  /** 筛选配置 */
  filters?: FilterConfig[]
  /** 是否可见 */
  visible?: boolean
  /** 固定列 */
  fixed?: ColumnFixed
  /** 列对齐 */
  align?: ColumnAlign
  /** 列数据类型 */
  type?: FilterType
  /** 格式化函数 */
  formatter?: ColumnFormatter
  /** 是否显示省略提示 */
  showOverflow?: boolean
  /** 自定义插槽名 */
  slot?: string
  /** 合计行列配置 */
  summary?: SummaryColumnConfig
}

/**
 * 行配置
 */
export interface RowConfig {
  /** 是否高亮当前行 */
  isCurrent?: boolean
  /** 是否显示行悬浮效果 */
  isHover?: boolean
  /** 行主键字段 */
  keyField?: string
}

/**
 * 排序事件参数
 */
export interface SortEventParams {
  /** 排序字段 */
  field: string
  /** 排序方向 */
  order: 'asc' | 'desc' | null
  /** 当前所有排序字段（多列排序时包含全部排序列） */
  sortList?: SortField[]
}

/**
 * 筛选事件参数
 */
export interface FilterEventParams {
  /** 筛选字段 */
  field: string
  /** 筛选值 */
  values: unknown[]
}

/**
 * 列格式持久化数据
 */
export interface ColumnPersistData {
  /** 列字段 */
  field: string
  /** 是否可见 */
  visible: boolean
  /** 列宽度 */
  width?: number
  /** 是否固定 */
  fixed?: ColumnFixed
  /** 列顺序索引 */
  order: number
}

/**
 * 搜索模型（列搜索筛选值）
 */
export interface ListTableSearchModel {
  [field: string]: unknown
}

/**
 * 筛选列信息
 */
export interface FilterColumnInfo {
  /** 筛选字段 */
  field: string
  /** 筛选值 */
  values: unknown[]
}

/**
 * ListTable 组件 Props
 */
export interface ListTableProps {
  /** 列配置 */
  columns: ListTableColumn[]
  /** 表格数据 */
  data: unknown[]
  /** 加载状态 */
  loading?: boolean
  /** 总条数（服务端分页时必传） */
  total?: number
  /** 当前页（服务端分页时使用） */
  currentPage?: number
  /** 每页条数 */
  pageSize?: number
  /** 分页模式 */
  pageMode?: PageMode
  /** 行配置 */
  rowConfig?: RowConfig
  /** 表格高度 */
  height?: number | string
  /** 最大高度 */
  maxHeight?: number | string
  /** 行尺寸 */
  size?: RowSize
  /** 斑马纹 */
  stripe?: boolean
  /** 边框模式 */
  border?: BorderMode
  /** 是否显示表头 */
  showHeader?: boolean
  /** 合计行数据 */
  summaryData?: Record<string, unknown>
  /** 合计行配置 */
  summaryConfig?: SummaryConfig
  /** 空数据提示文本 */
  emptyText?: string
  /** 视图编码（用于列配置持久化key区分） */
  viewCode?: string
  /** 是否启用虚拟滚动 */
  virtualScroll?: boolean
  /** 当前选中行数据 */
  currentRow?: Record<string, unknown> | null
  /** 当前排序配置 */
  sortConfig?: SortConfig | null
  /** 列搜索筛选模型（v-model绑定） */
  searchModel?: ListTableSearchModel
  /** 是否禁用所有交互 */
  disabled?: boolean
  /** v-model绑定值（当前选中行） */
  modelValue?: Record<string, unknown> | null
  /** 字段配置映射（field_type映射+校验规则+联动规则） */
  fieldConfig?: Record<string, FieldConfig>
  /** 占位提示文本 */
  placeholder?: string
}

/**
 * ListTable 组件 Emits
 */
export interface ListTableEmits {
  /** 分页变更 */
  'update:currentPage': [page: number]
  'update:pageSize': [size: number]
  /** 搜索模型变更（v-model双向绑定） */
  'update:searchModel': [model: ListTableSearchModel]
  /** 排序变更 */
  'sort-change': [params: SortEventParams]
  /** 筛选变更 */
  'filter-change': [params: FilterEventParams]
  /** 值确认变更 */
  change: [params: FilterEventParams]
  /** 当前行变更 */
  'current-change': [row: Record<string, unknown> | null]
  /** 单元格点击 */
  'cell-click': [row: Record<string, unknown>, column: ListTableColumn]
  /** 行点击 */
  'row-click': [row: Record<string, unknown>]
  /** 行双击 */
  'row-dblclick': [row: Record<string, unknown>]
  /** 焦点事件 */
  focus: []
  blur: []
  /** v-model更新事件 */
  'update:modelValue': [row: Record<string, unknown> | null]
}

/**
 * ListTable 组件 Slots
 */
export interface ListTableSlots {
  /** 表格前缀插槽（表格上方内容） */
  prefix?: () => unknown
  /** 表格后缀插槽（表格下方内容） */
  suffix?: () => unknown
  /** 默认内容插槽 */
  default?: () => unknown
  /** 工具栏插槽 */
  toolbar?: (params: { gridRef: unknown }) => unknown
  /** 底部插槽 */
  footer?: () => unknown
  /** 动态列插槽 */
  [columnSlot: string]:
    | ((params: { row: Record<string, unknown>; column: ListTableColumn }) => unknown)
    | (() => unknown)
    | undefined
}

/**
 * ListTable 组件暴露方法
 */
export interface ListTableExpose {
  /** vxe-grid 实例引用 */
  gridRef: unknown
  /** 重置列配置 */
  resetColumns: () => void
  /** 刷新表格 */
  refresh: () => void
  /** 清除排序 */
  clearSort: () => void
  /** 清除筛选 */
  clearFilter: (field?: string) => void
  /** 清除选中行 */
  clearCurrent: () => void
  /** 获取当前行 */
  getCurrentRow: () => Record<string, unknown> | null
  /** 设置当前行 */
  setCurrentRow: (row: Record<string, unknown>) => void
  /** 设置排序 */
  setSort: (field: string, order: 'asc' | 'desc' | null) => void
  /** 获取当前排序列 */
  getSortColumns: () => SortField[]
  /** 设置列筛选 */
  setFilter: (field: string, values: unknown[]) => void
  /** 获取当前筛选列 */
  getFilterColumns: () => FilterColumnInfo[]
  /** 一键初始化：重置列配置+清除排序+清除筛选+清除选中+重置分页 */
  resetAll: () => void
  /** 一键清空搜索排序：清除排序+清除筛选 */
  clearSearchAndSort: () => void
}
