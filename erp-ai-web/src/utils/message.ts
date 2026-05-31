import type { VNode } from 'vue'
import type { ElMessageBoxOptions } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CircleCheck, CircleClose, Warning, InfoFilled } from '@element-plus/icons-vue'

/** 消息内容类型：支持纯文本和VNode */
type MessageContent = string | VNode

/** 消息类型 */
type MessageType = 'success' | 'warning' | 'info' | 'error'

/** 消息配置选项 */
interface MessageOptions {
  /** 消息内容 */
  message: MessageContent
  /** 消息类型，默认'info' */
  type?: MessageType
  /** 显示时长(ms)，默认3000 */
  duration?: number
  /** 是否显示关闭按钮，默认true */
  showClose?: boolean
  /** 消息分组标识（相同grouping的消息不重复弹出） */
  grouping?: boolean
}

/** 默认配置 */
export const defaultOptions: Required<Omit<MessageOptions, 'message'>> = {
  type: 'info',
  duration: 3000,
  showClose: true,
  grouping: true
}

/** 统一图标映射 */
const iconMap: Record<MessageType, typeof CircleCheck> = {
  success: CircleCheck,
  warning: Warning,
  info: InfoFilled,
  error: CircleClose
}

/** 消息实例缓存Map（用于去重） */
const messageCache = new Map<string, ReturnType<typeof ElMessage>>()

/**
 * 内部统一消息调用（去重+统一配置）
 */
function showMessage(options: MessageOptions): void {
  const { message, type = 'info', duration = 3000, showClose = true, grouping = true } = options

  if (message === null || message === undefined) {
    console.warn('[message] message is null or undefined, skip showing')
    return
  }

  if (grouping && typeof message === 'string') {
    const cacheKey = `${type}:${message}`
    if (messageCache.has(cacheKey)) {
      return
    }

    const instance = ElMessage({
      message,
      type,
      duration,
      showClose,
      icon: iconMap[type],
      onClose: () => {
        messageCache.delete(cacheKey)
      }
    })

    messageCache.set(cacheKey, instance)
    return
  }

  ElMessage({
    message,
    type,
    duration,
    showClose,
    icon: iconMap[type]
  })
}

/**
 * 显示成功消息
 */
export function showSuccess(message: MessageContent, options?: Partial<MessageOptions>): void {
  showMessage({ message, type: 'success', ...options })
}

/**
 * 显示错误消息
 */
export function showError(message: MessageContent, options?: Partial<MessageOptions>): void {
  showMessage({ message, type: 'error', ...options })
}

/**
 * 显示警告消息
 */
export function showWarning(message: MessageContent, options?: Partial<MessageOptions>): void {
  showMessage({ message, type: 'warning', ...options })
}

/**
 * 显示信息消息
 */
export function showInfo(message: MessageContent, options?: Partial<MessageOptions>): void {
  showMessage({ message, type: 'info', ...options })
}

/**
 * 确认弹窗（Promise化）
 * @returns Promise<boolean> 确认返回true，取消reject
 */
export function confirm(
  message: MessageContent,
  title?: string,
  options?: ElMessageBoxOptions
): Promise<boolean> {
  return new Promise((resolve, reject) => {
    ElMessageBox.confirm(message as string, title ?? '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      closeOnClickModal: false,
      ...options
    })
      .then(() => {
        resolve(true)
      })
      .catch((action: string) => {
        reject(action === 'cancel' ? 'cancel' : action)
      })
  })
}
