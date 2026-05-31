import type { VNode } from 'vue'
import type { ElMessageBoxOptions } from 'element-plus'

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
export const iconMap: Record<MessageType, string> = {
  success: 'CircleCheck',
  warning: 'Warning',
  info: 'InfoFilled',
  error: 'CircleClose'
}

/**
 * 显示成功消息
 */
export function showSuccess(_message: MessageContent, _options?: Partial<MessageOptions>): void {
  // TODO: 实现逻辑将在 P0-002-004-003-001-002 中完成
}

/**
 * 显示错误消息
 */
export function showError(_message: MessageContent, _options?: Partial<MessageOptions>): void {
  // TODO: 实现逻辑将在 P0-002-004-003-001-002 中完成
}

/**
 * 显示警告消息
 */
export function showWarning(_message: MessageContent, _options?: Partial<MessageOptions>): void {
  // TODO: 实现逻辑将在 P0-002-004-003-001-002 中完成
}

/**
 * 显示信息消息
 */
export function showInfo(_message: MessageContent, _options?: Partial<MessageOptions>): void {
  // TODO: 实现逻辑将在 P0-002-004-003-001-002 中完成
}

/**
 * 确认弹窗（Promise化）
 * @returns Promise<boolean> 确认返回true，取消reject
 */
export function confirm(
  _message: MessageContent,
  _title?: string,
  _options?: ElMessageBoxOptions
): Promise<boolean> {
  // TODO: 实现逻辑将在 P0-002-004-003-001-002 中完成
  return Promise.resolve(false)
}
