import type { VNode } from 'vue'
import { ElNotification } from 'element-plus'
import { CircleCheck, CircleClose, Warning, InfoFilled } from '@element-plus/icons-vue'

/** 通知内容类型：支持纯文本和VNode */
type NotificationContent = string | VNode

/** 通知类型 */
type NotificationType = 'success' | 'warning' | 'info' | 'error'

/** 通知位置 */
type NotificationPosition = 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left'

/** 通知配置选项 */
interface NotifyOptions {
  /** 通知标题 */
  title: string
  /** 通知内容 */
  message: NotificationContent
  /** 通知类型，默认'info' */
  type?: NotificationType
  /** 显示时长(ms)，默认4500，0表示不自动关闭 */
  duration?: number
  /** 通知位置，默认'top-right' */
  position?: NotificationPosition
  /** 是否显示关闭按钮，默认true */
  showClose?: boolean
  /** 去重标识（相同name的通知不重复弹出） */
  name?: string
  /** 偏移量(px)，默认0 */
  offset?: number
}

/** 默认配置 */
const defaultOptions: Required<Omit<NotifyOptions, 'title' | 'message'>> = {
  type: 'info',
  duration: 4500,
  position: 'top-right',
  showClose: true,
  name: '',
  offset: 0
}

/** 统一图标映射 */
const iconMap: Record<NotificationType, typeof CircleCheck> = {
  success: CircleCheck,
  warning: Warning,
  info: InfoFilled,
  error: CircleClose
}

/** 活跃通知缓存（用于去重和批量关闭） */
const activeNotifications = new Map<string, ReturnType<typeof ElNotification>>()

/**
 * 内部统一通知调用（去重+统一配置）
 */
function notifyImpl(options: NotifyOptions): void {
  const merged = { ...defaultOptions, ...options }
  const { title, message, type, duration, position, showClose, name, offset } = merged

  if (title === '') {
    console.warn('[notification] title is empty string')
  }

  if (name && activeNotifications.has(name)) {
    return
  }

  const instance = ElNotification({
    title,
    message,
    type,
    duration,
    position,
    showClose,
    offset,
    icon: iconMap[type],
    onClose: () => {
      if (name) {
        activeNotifications.delete(name)
      }
    }
  })

  if (name) {
    activeNotifications.set(name, instance)
  }
}

/**
 * 显示通知
 * @param options 通知配置选项
 */
export function notify(options: NotifyOptions): void {
  notifyImpl(options)
}

/**
 * 快捷方法：成功通知
 */
export function notifySuccess(title: string, message: NotificationContent): void {
  notifyImpl({ title, message, type: 'success' })
}

/**
 * 快捷方法：错误通知
 */
export function notifyError(title: string, message: NotificationContent): void {
  notifyImpl({ title, message, type: 'error', duration: 0 })
}

/**
 * 快捷方法：警告通知
 */
export function notifyWarning(title: string, message: NotificationContent): void {
  notifyImpl({ title, message, type: 'warning' })
}

/**
 * 快捷方法：信息通知
 */
export function notifyInfo(title: string, message: NotificationContent): void {
  notifyImpl({ title, message, type: 'info' })
}

/**
 * 关闭所有活跃通知
 */
export function clearAllNotifications(): void {
  const instances = Array.from(activeNotifications.values())
  instances.forEach((instance) => instance.close())
  activeNotifications.clear()
}
