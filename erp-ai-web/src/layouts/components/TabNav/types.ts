import type { TagView } from '@/stores/modules/tagsView'

/** TabNav组件Props */
export interface TabNavProps {
  /** 当前激活标签的路径 */
  activePath: string
  /** 标签列表(从tagsViewStore获取) */
  views: TagView[]
}

/** TabNav组件Emits */
export interface TabNavEmits {
  /** 选中标签 */
  (e: 'select', path: string): void
  /** 关闭标签 */
  (e: 'close', tag: TagView): void
  /** 刷新标签 */
  (e: 'refresh', path: string): void
  /** 右键菜单 */
  (e: 'contextmenu', event: MouseEvent, tag: TagView): void
}
