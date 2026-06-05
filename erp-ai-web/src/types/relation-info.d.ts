/**
 * 关联信息区（RelatedInfoArea）组件类型定义
 *
 * 关联信息区是 ERP 详情页面的核心复合组件，由左侧分组导航栏
 * （GroupNav）和右侧标签页容器组成，用于展示主记录的所有关联信息。
 */

// ============================================================
// 分组导航栏（GroupNav）类型
// ============================================================

/** 导航分组配置 */
export interface NavGroup {
  /** 分组唯一标识 */
  key: string
  /** 分组标签文本 */
  label: string
  /** Element Plus 图标名 */
  icon?: string
  /** 角标数量（undefined 或 0 表示不显示） */
  badge?: number
  /** 是否禁用 */
  disabled?: boolean
  /** 是否隐藏 */
  hidden?: boolean
  /** 子分组（支持嵌套） */
  children?: NavGroup[]
}

/** 分组导航栏 Props */
export interface GroupNavProps {
  /** v-model 双向绑定值（当前选中分组的 key） */
  modelValue?: string
  /** 分组配置列表 */
  fieldConfig?: NavGroup[]
  /** 是否全局禁用 */
  disabled?: boolean
  /** 占位提示文本（无分组时显示） */
  placeholder?: string
}

/** 分组导航栏 Emits */
export interface GroupNavEmits {
  /** v-model 更新事件（分组选中变更时触发） */
  'update:modelValue': [key: string]
  /** 分组确认变更事件 */
  change: [key: string, group: NavGroup]
  /** 焦点事件 */
  focus: [key: string]
  blur: [key: string]
}

/** 分组导航栏 Slots */
export interface GroupNavSlots {
  /** 前缀插槽（导航栏上方内容） */
  prefix?: () => unknown
  /** 后缀插槽（导航栏下方内容） */
  suffix?: () => unknown
  /** 默认内容插槽 */
  default?: () => unknown
}

/** 分组导航栏组件暴露方法 */
export interface GroupNavExpose {
  /** 获取当前选中分组 key */
  getActiveKey: () => string | undefined
  /** 设置选中分组 */
  setActiveKey: (key: string) => void
  /** 展开指定分组 */
  expand: (key: string) => void
  /** 折叠指定分组 */
  collapse: (key: string) => void
  /** 获取当前展开的所有分组 key */
  getExpandedKeys: () => string[]
}

// ============================================================
// 关联信息区（RelatedInfoArea）整体类型
// ============================================================

/** 关联信息区标签页配置 */
export interface RelatedTab {
  /** 标签页唯一标识 */
  key: string
  /** 标签页标题 */
  label: string
  /** 所属分组 key */
  group: string
  /** 权限编码（v-permission 使用） */
  permission?: string
  /** 是否懒加载 */
  lazy?: boolean
  /** 是否禁用 */
  disabled?: boolean
  /** 是否隐藏 */
  hidden?: boolean
}

/** 关联信息区 Props */
export interface RelatedInfoAreaProps {
  /** v-model 双向绑定值（当前选中标签页 key） */
  modelValue?: string
  /** 当前主行数据（用于驱动关联信息数据加载） */
  mainRow?: Record<string, unknown> | null
  /** 分组配置 */
  groups?: NavGroup[]
  /** 标签页配置 */
  tabs?: RelatedTab[]
  /** 是否全局禁用 */
  disabled?: boolean
  /** 占位提示文本 */
  placeholder?: string
  /** 视图编码 */
  viewCode?: string
}

/** 关联信息区 Emits */
export interface RelatedInfoAreaEmits {
  'update:modelValue': [key: string]
  change: [key: string]
  focus: []
  blur: []
}

/** 关联信息区组件暴露方法 */
export interface RelatedInfoAreaExpose {
  /** 刷新当前标签页数据 */
  refreshCurrentTab: () => void
  /** 刷新所有标签页数据 */
  refreshAllTabs: () => void
  /** 获取当前主行数据 */
  getMainRow: () => Record<string, unknown> | null
}
