/**
 * 明细从表区域（DetailTableArea）组件类型定义
 *
 * 明细从表区域是 ERP 详情页面中主表单下方的从表容器组件，
 * 以标签页形式管理多个明细从表（如订单明细、收款记录、物流记录等），
 * 支持标签页切换、懒加载、头部工具栏和区域铺满切换等功能。
 */

// ============================================================
// 明细从表标签页配置
// ============================================================

/** 明细从表标签页配置 */
export interface DetailTableTab {
  /** 标签页唯一标识 */
  key: string
  /** 标签页标题 */
  label: string
  /** 是否懒加载（默认 true，未激活时不渲染内容） */
  lazy?: boolean
  /** 是否禁用 */
  disabled?: boolean
  /** 是否隐藏 */
  hidden?: boolean
}

// ============================================================
// 组件接口
// ============================================================

/** DetailTableArea Props */
export interface DetailTableProps {
  /** v-model 双向绑定值（当前选中标签页的 key） */
  modelValue?: string
  /** 标签页配置列表 */
  fieldConfig?: DetailTableTab[]
  /** 是否全局禁用 */
  disabled?: boolean
  /** 占位提示文本（无标签页时显示） */
  placeholder?: string
}

/** DetailTableArea Emits */
export interface DetailTableEmits {
  /** v-model 更新事件（标签页切换时触发） */
  'update:modelValue': [key: string]
  /** 标签页确认变更事件 */
  change: [key: string, tab: DetailTableTab]
  /** 焦点事件 */
  focus: [key: string]
  /** 失焦事件 */
  blur: [key: string]
}

/** DetailTableArea Slots */
export interface DetailTableSlots {
  /** 前缀插槽（标签页头部前方内容） */
  prefix?: () => unknown
  /** 后缀插槽（标签页头部后方内容） */
  suffix?: () => unknown
  /** 默认内容插槽（标签页内容区域） */
  default?: () => unknown
}

/** DetailTableArea 组件暴露方法 */
export interface DetailTableExpose {
  /** 获取当前激活标签页 key */
  getActiveKey: () => string | undefined
  /** 设置激活标签页 */
  setActiveKey: (key: string) => void
  /** 获取可见标签页列表 */
  getVisibleTabs: () => DetailTableTab[]
}

// ============================================================
// 头部扩展工具栏（HeaderToolbar）
// ============================================================

/** 行高预设值 */
export type DetailTableRowHeightPreset = 'compact' | 'small' | 'default' | 'large' | 'extra-large'

/** 工具栏按钮项配置 */
export interface DetailTableToolbarItem {
  /** 按钮唯一标识 */
  key: string
  /** 内置工具类型 */
  tool?: 'add-row' | 'maximize' | 'refresh' | 'row-height' | string
  /** 按钮文本 */
  label?: string
  /** 提示文本 */
  tooltip?: string
  /** 图标名称（Element Plus Icon 组件名） */
  icon?: string
  /** 是否禁用 */
  disabled?: boolean
  /** 是否隐藏 */
  hidden?: boolean
  /** 是否在按钮后显示分隔线 */
  showDivider?: boolean
}

/** HeaderToolbar 状态 */
export interface DetailTableToolbarState {
  /** 是否铺满 */
  maximized: boolean
  /** 行高预设 */
  rowHeight: DetailTableRowHeightPreset
}

/** HeaderToolbar Props */
export interface DetailTableToolbarProps {
  /** v-model 双向绑定值 */
  modelValue: DetailTableToolbarState
  /** 工具栏按钮配置列表 */
  fieldConfig?: DetailTableToolbarItem[]
  /** 是否全局禁用 */
  disabled?: boolean
  /** 占位提示文本 */
  placeholder?: string
}

/** HeaderToolbar Emits */
export interface DetailTableToolbarEmits {
  'update:modelValue': [value: DetailTableToolbarState]
  change: [tool: string, state: DetailTableToolbarState]
  focus: [key: string]
  blur: [key: string]
}

/** HeaderToolbar Slots */
export interface DetailTableToolbarSlots {
  prefix?: () => unknown
  suffix?: () => unknown
  default?: () => unknown
}

/** HeaderToolbar 组件暴露方法 */
export interface DetailTableToolbarExpose {
  /** 获取当前工具栏状态 */
  getState: () => DetailTableToolbarState
  /** 切换铺满状态 */
  toggleMaximize: () => void
}
