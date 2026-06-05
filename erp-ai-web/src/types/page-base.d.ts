/**
 * 页面类型（P01~P15）
 */
export type PageType =
  | 'P01'
  | 'P02'
  | 'P03'
  | 'P04'
  | 'P05'
  | 'P06'
  | 'P07'
  | 'P08'
  | 'P09'
  | 'P10'
  | 'P11'
  | 'P12'
  | 'P13'
  | 'P14'
  | 'P15'

/**
 * KPI 指标卡片配置
 */
export interface KpiCardConfig {
  /** 卡片唯一标识 */
  id: string
  /** 卡片标题 */
  label: string
  /** 指标值 */
  value: number
  /** 图标名 */
  icon: string
  /** 颜色主题 */
  color?: 'blue' | 'green' | 'orange' | 'purple'
  /** 下钻路由路径 */
  to?: string
  /** 趋势值（百分比） */
  trend?: number
  /** 对比标签 */
  compareLabel?: string
}

/**
 * 快捷入口配置
 */
export interface QuickEntryConfig {
  /** 入口唯一标识 */
  id: string
  /** 入口名称 */
  label: string
  /** 图标名 */
  icon: string
  /** 路由路径 */
  to: string
}

/**
 * 图表配置
 */
export interface ChartConfig {
  /** 图表唯一标识 */
  id: string
  /** 图表标题 */
  title: string
  /** 图表类型 */
  type: 'line' | 'bar' | 'pie' | 'number'
  /** 图表数据源API路径 */
  dataUrl?: string
  /** 图表尺寸（grid-column/grid-row span） */
  span?: { col: number; row: number }
}

/**
 * 待办项配置
 */
export interface TodoItemConfig {
  /** 待办唯一标识 */
  id: string
  /** 待办标题 */
  title: string
  /** 待办类型 */
  type: 'approval' | 'alert' | 'message'
  /** 数量 */
  count: number
  /** 路由路径 */
  to?: string
}

/**
 * 最近访问项配置
 */
export interface RecentVisitConfig {
  /** 菜单ID */
  id: string
  /** 菜单名称 */
  label: string
  /** 路由路径 */
  to: string
  /** 最近访问时间 */
  visitedAt?: string
}

/**
 * 工作台统计卡片配置
 */
export interface WorkbenchStatCardConfig {
  /** 卡片唯一标识 */
  id: string
  /** 卡片标题 */
  label: string
  /** 统计值 */
  value: number | string
  /** 图标名 */
  icon: string
  /** 颜色主题 */
  color?: 'blue' | 'green' | 'orange' | 'purple' | 'red'
  /** 下钻路由 */
  to?: string
}

/**
 * 仪表盘页面配置（P01）
 */
export interface DashboardPageConfig {
  /** 欢迎语 */
  welcomeText?: string
  /** 用户名称 */
  userName?: string
  /** KPI 指标卡片列表 */
  kpiCards?: KpiCardConfig[]
  /** 快捷入口列表 */
  quickEntries?: QuickEntryConfig[]
  /** 图表看板配置 */
  charts?: ChartConfig[]
  /** 待办/消息列表 */
  todoItems?: TodoItemConfig[]
  /** 最近访问列表 */
  recentVisits?: RecentVisitConfig[]
  /** 是否启用拖拽布局 */
  draggable?: boolean
}

/**
 * 工作台页面配置（P02）
 */
export interface WorkbenchPageConfig {
  /** 页面标题 */
  title?: string
  /** 统计卡片行 */
  statCards?: WorkbenchStatCardConfig[]
  /** 是否显示查询区面板 */
  showQueryPanel?: boolean
  /** 是否显示操作栏 */
  showActionBar?: boolean
}

/**
 * 主从列表页面配置（P03）
 */
export interface MasterListPageConfig {
  /** 页面标题 */
  title?: string
  /** 是否显示查询区面板 */
  showQueryPanel?: boolean
  /** 是否显示操作栏 */
  showActionBar?: boolean
  /** 主列表宽度占比 (0-100)，默认 40 */
  listWidthPercent?: number
  /** 列表高度 */
  listHeight?: number | string
}

/**
 * 单一列表页面配置（P04）
 */
export interface SimpleListPageConfig {
  /** 页面标题 */
  title?: string
  /** 是否显示查询区面板 */
  showQueryPanel?: boolean
  /** 是否显示操作栏 */
  showActionBar?: boolean
}

/**
 * 树形列表页面配置（P05）
 */
export interface TreeListPageConfig {
  /** 页面标题 */
  title?: string
  /** 是否显示查询区面板 */
  showQueryPanel?: boolean
  /** 是否显示操作栏 */
  showActionBar?: boolean
  /** 树形导航区宽度，默认 280px */
  treeWidth?: number
  /** 是否显示树区域搜索框 */
  showTreeSearch?: boolean
}

/**
 * 主从表单页面配置（P06）
 */
export interface MasterFormPageConfig {
  /** 页面标题 */
  title?: string
  /** 是否显示查询区面板 */
  showQueryPanel?: boolean
  /** 是否显示操作栏 */
  showActionBar?: boolean
  /** 表单最大宽度，默认 960px */
  formMaxWidth?: number | string
}

/**
 * 页面配置联合类型
 */
export type PageConfig =
  | DashboardPageConfig
  | WorkbenchPageConfig
  | MasterListPageConfig
  | SimpleListPageConfig
  | TreeListPageConfig
  | MasterFormPageConfig
  | (Record<string, unknown> & {})

/**
 * 页面就绪事件参数
 */
export interface PageReadyPayload {
  viewId: string
  pageType: PageType
}

/**
 * 数据变更事件参数
 */
export interface DataChangePayload {
  source: string
  data: unknown
}

/**
 * 页面跳转事件参数
 */
export interface NavigatePayload {
  to: string
  query?: Record<string, string>
}

/**
 * 页面基座组件 Props
 */
export interface PageBaseProps {
  /** 视图编号（必传） */
  viewId: string
  /** 页面类型 */
  pageType: PageType
  /** 页面配置 */
  config: PageConfig
  /** 权限列表 */
  permissions: string[]
}

/**
 * 页面基座组件 Emits
 */
export interface PageBaseEmits {
  /** 页面就绪事件 */
  'page-ready': [payload: PageReadyPayload]
  /** 数据变更事件 */
  'data-change': [payload: DataChangePayload]
  /** 页面跳转事件 */
  navigate: [payload: NavigatePayload]
}

/**
 * 页面基座组件 Slots
 */
export interface PageBaseSlots {
  /** 查询区自定义插槽 */
  'query-panel'?: () => unknown
  /** 主内容区自定义插槽 */
  'main-content'?: () => unknown
  /** 操作区自定义插槽 */
  'action-bar'?: () => unknown
  /** 额外区域自定义插槽 */
  'extra-area'?: () => unknown
}
