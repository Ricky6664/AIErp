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
}

/**
 * ListTable 组件 Emits
 */
export interface ListTableEmits {
  /** 分页变更 */
  'update:currentPage': [page: number]
  'update:pageSize': [size: number]
  /** 排序变更 */
  'sort-change': [params: SortEventParams]
  /** 筛选变更 */
  'filter-change': [params: FilterEventParams]
  /** 当前行变更 */
  'current-change': [row: Record<string, unknown> | null]
  /** 单元格点击 */
  'cell-click': [row: Record<string, unknown>, column: ListTableColumn]
  /** 行点击 */
  'row-click': [row: Record<string, unknown>]
  /** 行双击 */
  'row-dblclick': [row: Record<string, unknown>]
}
