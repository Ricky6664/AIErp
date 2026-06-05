/**
 * 操作按钮样式类型
 */
export type ActionButtonType = 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'

/**
 * 内置动作类型
 */
export type BuiltInAction =
  | 'add'
  | 'edit'
  | 'delete'
  | 'batch-delete'
  | 'export'
  | 'import'
  | 'refresh'
  | 'print'
  | 'audit'
  | 'anti-audit'
  | 'submit'
  | 'save-draft'
  | 'cancel'
  | 'reset'
  | 'submit-continue'
  | 'custom'

/**
 * ActionBar 显示模式
 */
export type ActionBarMode = 'list' | 'form'

/**
 * 操作按钮配置项
 */
export interface ActionItem {
  /** 唯一标识 */
  key: string
  /** 按钮文本 */
  label: string
  /** 图标（Element Plus icon 名称） */
  icon?: string
  /** 按钮类型 */
  type?: ActionButtonType
  /** 关联动作 */
  action?: BuiltInAction | string
  /** 是否禁用 */
  disabled?: boolean
  /** 是否隐藏 */
  hidden?: boolean
  /** 加载中 */
  loading?: boolean
  /** 提示文本 */
  tooltip?: string
  /** 确认弹窗配置 */
  confirm?: ActionConfirm
  /** 子菜单（下拉按钮组） */
  children?: ActionItem[]
}

/**
 * 确认弹窗配置
 */
export interface ActionConfirm {
  title: string
  message: string
  confirmText?: string
  cancelText?: string
}

/**
 * ActionBar 组件 Props
 */
export interface ActionBarProps {
  /** v-model 绑定的操作栏状态 */
  modelValue: Record<string, unknown>
  /** 操作按钮配置列表 */
  fieldConfig: ActionItem[]
  /** 是否全局禁用 */
  disabled?: boolean
  /** 占位提示 */
  placeholder?: string
  /** 显示模式：list=列表页(左主右辅) / form=表单页(右对齐) */
  mode?: ActionBarMode
}

/**
 * ActionBar useActionBar 组合式函数配置
 */
export interface UseActionBarOptions {
  /** 按钮间距 */
  gap?: number
  /** 是否显示左侧/右侧分隔线 */
  showDivider?: boolean
}
