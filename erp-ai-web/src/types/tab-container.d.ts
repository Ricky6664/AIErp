/**
 * 标签页容器组件（TabPageContainer）类型定义
 *
 * 通用标签页切换容器，支持配置驱动的标签页渲染、
 * v-model 双向绑定、权限过滤、分组筛选与插槽扩展。
 */

// ============================================================
// 标签页配置项
// ============================================================

/** 单个标签页配置 */
export interface TabItem {
  /** 标签页唯一标识 */
  key: string
  /** 标签页标题 */
  label: string
  /** Element Plus 图标名 */
  icon?: string
  /** 所属分组 key（用于分组筛选） */
  group?: string
  /** 权限编码 */
  permission?: string
  /** 是否懒加载 */
  lazy?: boolean
  /** 是否禁用 */
  disabled?: boolean
  /** 是否隐藏 */
  hidden?: boolean
}

// ============================================================
// TabPageContainer Props
// ============================================================

/** TabPageContainer 组件 Props */
export interface TabPageContainerProps {
  /** v-model 双向绑定值（当前激活标签页的 key） */
  modelValue: string
  /** 标签页配置列表 */
  fieldConfig: TabItem[]
  /** 当前激活的分组 key（传值后仅渲染该分组的标签页） */
  activeGroup?: string
  /** 是否全局禁用 */
  disabled?: boolean
  /** 占位提示文本（无标签页时显示） */
  placeholder?: string
}

// ============================================================
// TabPageContainer Emits
// ============================================================

/** TabPageContainer 组件 Emits */
export interface TabPageContainerEmits {
  /** v-model 更新事件 */
  'update:modelValue': [key: string]
  /** 标签页切换确认事件 */
  change: [key: string, tab: TabItem]
  /** 标签页获得焦点 */
  focus: [key: string]
  /** 标签页失去焦点 */
  blur: [key: string]
}

// ============================================================
// TabPageContainer 暴露方法
// ============================================================

/** TabPageContainer 组件暴露方法 */
export interface TabPageContainerExpose {
  /** 获取当前激活标签页 key */
  getActiveKey: () => string | undefined
  /** 设置激活标签页 */
  setActiveKey: (key: string) => void
  /** 获取当前 group 的可见标签页列表 */
  getVisibleTabs: () => TabItem[]
  /** 因权限被隐藏的标签页数量 */
  permissionHiddenCount: number
}
