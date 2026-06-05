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
 * 简单表单页面配置（P07）
 */
export interface SimpleFormPageConfig {
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
 * 看板项配置
 */
export interface KanbanItemConfig {
  /** 看板项唯一标识 */
  id: string
  /** 看板项标题 */
  title: string
  /** 看板项描述 */
  description?: string
  /** 标签 */
  tags?: string[]
  /** 负责人 */
  assignee?: string
  /** 优先级 */
  priority?: 'low' | 'medium' | 'high' | 'urgent'
  /** 扩展元数据 */
  metadata?: Record<string, unknown>
}

/**
 * 看板列配置
 */
export interface KanbanColumnConfig {
  /** 列唯一标识 */
  id: string
  /** 列标题 */
  label: string
  /** 列颜色主题 */
  color?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  /** 列内看板项 */
  items?: KanbanItemConfig[]
}

/**
 * 查询字段配置
 */
export interface QueryFieldConfig {
  /** 字段唯一标识 */
  id: string
  /** 字段标签 */
  label: string
  /** 字段名 */
  field: string
  /** 字段类型 */
  type: 'input' | 'select' | 'date' | 'date-range' | 'number' | 'cascader'
  /** 占位文本 */
  placeholder?: string
  /** 选项（select/cascader 类型使用） */
  options?: { label: string; value: string | number }[]
  /** 默认值 */
  defaultValue?: unknown
  /** 字段宽度（栅格, 1-24），默认 6 */
  span?: number
}

/**
 * 查询页面配置（P09）
 */
export interface QueryPageConfig {
  /** 页面标题 */
  title?: string
  /** 是否显示查询区面板 */
  showQueryPanel?: boolean
  /** 是否显示操作栏 */
  showActionBar?: boolean
  /** 查询字段列表 */
  queryFields?: QueryFieldConfig[]
  /** 是否显示结果计数 */
  showResultCount?: boolean
  /** 每页默认条数 */
  pageSize?: number
}

/**
 * 报表过滤字段配置
 */
export interface ReportFilterConfig {
  /** 字段唯一标识 */
  id: string
  /** 字段标签 */
  label: string
  /** 字段名 */
  field: string
  /** 字段类型 */
  type: 'input' | 'select' | 'date' | 'date-range' | 'number'
  /** 占位文本 */
  placeholder?: string
  /** 选项（select 类型使用） */
  options?: { label: string; value: string | number }[]
  /** 字段宽度（栅格，1-24），默认 6 */
  span?: number
}

/**
 * 报表列配置
 */
export interface ReportColumnConfig {
  /** 列唯一标识 */
  id: string
  /** 列标签 */
  label: string
  /** 列字段 */
  field: string
  /** 列宽度 */
  width?: number | string
  /** 对齐方式 */
  align?: 'left' | 'center' | 'right'
  /** 固定列 */
  fixed?: 'left' | 'right'
}

/**
 * 报表树形配置
 */
export interface ReportTreeConfig {
  /** 树数据源 API */
  dataUrl?: string
  /** 标签字段名 */
  labelField?: string
  /** 子节点字段名 */
  childrenField?: string
}

/**
 * 报表账簿配置
 */
export interface ReportLedgerConfig {
  /** 借方金额字段 */
  debitField?: string
  /** 贷方金额字段 */
  creditField?: string
  /** 余额字段 */
  balanceField?: string
}

/**
 * 报表页面配置（P10）
 */
export interface ReportPageConfig {
  /** 页面标题 */
  title?: string
  /** 是否显示条件过滤面板 */
  showFilterPanel?: boolean
  /** 是否显示操作栏 */
  showActionBar?: boolean
  /** 报表类型 */
  reportType?: 'table' | 'tree' | 'ledger'
  /** 过滤条件字段列表 */
  filterFields?: ReportFilterConfig[]
  /** 报表列定义（table/ledger 类型使用） */
  columns?: ReportColumnConfig[]
  /** 报表行数据 */
  rows?: Record<string, unknown>[]
  /** 树形报表配置（tree 类型使用） */
  treeConfig?: ReportTreeConfig
  /** 账簿配置（ledger 类型使用） */
  ledgerConfig?: ReportLedgerConfig
  /** 是否显示打印按钮 */
  showPrint?: boolean
  /** 是否显示导出 Excel 按钮 */
  showExportExcel?: boolean
  /** 是否显示导出 PDF 按钮 */
  showExportPdf?: boolean
}

/**
 * 看板页面配置（P08）
 */
export interface KanbanPageConfig {
  /** 页面标题 */
  title?: string
  /** 是否显示查询区面板 */
  showQueryPanel?: boolean
  /** 是否显示操作栏 */
  showActionBar?: boolean
  /** 看板列宽度，默认 280px */
  columnWidth?: number | string
  /** 看板列配置 */
  columns?: KanbanColumnConfig[]
}

/**
 * 大屏KPI卡片配置（P11）
 */
export interface ScreenKpiConfig {
  /** 卡片唯一标识 */
  id: string
  /** 卡片标签 */
  label: string
  /** 指标值 */
  value: number | string
  /** 单位 */
  unit?: string
  /** 图标名 */
  icon?: string
  /** 颜色主题 */
  color?: 'blue' | 'green' | 'orange' | 'purple' | 'red'
  /** 趋势百分比 */
  trend?: number
  /** 数值动画时长(ms)，默认 1000 */
  animationDuration?: number
}

/**
 * 大屏图表项配置（P11）
 */
export interface ScreenChartItemConfig {
  /** 图表唯一标识 */
  id: string
  /** 图表标题 */
  title: string
  /** 图表类型 */
  type: 'line' | 'bar' | 'pie' | 'number' | 'gauge' | 'map'
  /** 数据源API路径 */
  dataUrl?: string
  /** 栅格列跨度 */
  colSpan?: number
  /** 栅格行跨度 */
  rowSpan?: number
  /** 图表子标题 */
  subtitle?: string
}

/**
 * 大屏页面配置（P11）
 */
export interface ScreenPageConfig {
  /** 页面标题 */
  title?: string
  /** 是否启用深色主题，默认 true */
  darkTheme?: boolean
  /** 是否显示全屏切换按钮 */
  showFullscreenBtn?: boolean
  /** 是否全屏状态（双向绑定） */
  fullscreen?: boolean
  /** 数据定时刷新间隔（秒），0 或未设置表示不自动刷新 */
  refreshInterval?: number
  /** 图表网格列数，默认 12 */
  gridCols?: number
  /** KPI 卡片列表 */
  kpiCards?: ScreenKpiConfig[]
  /** 图表列表 */
  charts?: ScreenChartItemConfig[]
  /** 是否显示KPI区域 */
  showKpiArea?: boolean
}

/**
 * 配置表单字段配置（P13）
 */
export interface ConfigFormFieldConfig {
  /** 字段唯一标识 */
  id: string
  /** 字段标签 */
  label: string
  /** 字段名 */
  field: string
  /** 字段类型 */
  type: 'input' | 'number' | 'select' | 'switch' | 'date' | 'textarea' | 'color'
  /** 默认值 */
  defaultValue?: unknown
  /** 占位文本 */
  placeholder?: string
  /** 选项（select 类型使用） */
  options?: { label: string; value: string | number }[]
  /** 是否必填 */
  required?: boolean
  /** 栅格跨度（1-24），默认 24 */
  span?: number
}

/**
 * 配置分组配置（P13 简单配置页模式）
 */
export interface ConfigGroupConfig {
  /** 分组唯一标识 */
  id: string
  /** 分组标题 */
  title: string
  /** 分组图标 */
  icon?: string
  /** 分组内表单字段列表 */
  fields: ConfigFormFieldConfig[]
}

/**
 * 左侧导航项配置（P13 复杂配置页模式）
 */
export interface ConfigNavItemConfig {
  /** 导航项唯一标识 */
  id: string
  /** 导航项标签 */
  label: string
  /** 导航项图标 */
  icon?: string
  /** 子级导航项 */
  children?: ConfigNavItemConfig[]
}

/**
 * 配置页面配置（P13）
 */
export interface ConfigPageConfig {
  /** 页面标题 */
  title?: string
  /** 是否显示查询区面板 */
  showQueryPanel?: boolean
  /** 是否显示操作栏 */
  showActionBar?: boolean
  /** 配置布局模式：simple 简单表单式 / split 左右分栏式 */
  configLayout?: 'simple' | 'split'
  /** 简单模式：分组表单配置 */
  groups?: ConfigGroupConfig[]
  /** 分栏模式：左侧导航树 */
  navItems?: ConfigNavItemConfig[]
  /** 表单最大宽度，默认 960px */
  formMaxWidth?: number | string
  /** 是否显示保存按钮 */
  showSaveBtn?: boolean
  /** 是否显示重置按钮 */
  showResetBtn?: boolean
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
  | SimpleFormPageConfig
  | KanbanPageConfig
  | QueryPageConfig
  | ReportPageConfig
  | ScreenPageConfig
  | ProfilePageConfig
  | ConfigPageConfig
  | (Record<string, unknown> & {})

/**
 * 个人中心统计卡片配置（P12）
 */
export interface ProfileStatConfig {
  /** 统计项唯一标识 */
  id: string
  /** 统计项标签 */
  label: string
  /** 统计值 */
  value: number | string
  /** 图标名 */
  icon?: string
  /** 颜色主题 */
  color?: 'blue' | 'green' | 'orange' | 'purple' | 'red'
}

/**
 * 个人中心标签页配置（P12）
 */
export interface ProfileTabConfig {
  /** 标签页唯一标识 */
  id: string
  /** 标签页名称 */
  label: string
  /** 图标名 */
  icon?: string
}

/**
 * 个人中心页面配置（P12）
 */
export interface ProfilePageConfig {
  /** 页面标题 */
  title?: string
  /** 是否显示查询区面板 */
  showQueryPanel?: boolean
  /** 是否显示操作栏 */
  showActionBar?: boolean
  /** 用户头像URL */
  avatar?: string
  /** 用户名称 */
  userName?: string
  /** 用户角色 */
  userRole?: string
  /** 用户简介 */
  description?: string
  /** 统计卡片列表 */
  stats?: ProfileStatConfig[]
  /** 标签页列表 */
  tabs?: ProfileTabConfig[]
  /** 当前激活标签页（双向绑定） */
  activeTab?: string
  /** 表单内容最大宽度，默认 960 */
  formMaxWidth?: number | string
}

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
