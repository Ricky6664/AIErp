import { i18n } from '@/i18n'

type StatusType = 'audit' | 'enable' | 'order' | 'payment'

export function renderStatusText(statusType: StatusType, statusCode: string): string {
  const key = `status.${statusType}.${statusCode}`
  const result = i18n.global.t(key)
  if (result === key) {
    if (import.meta.env.DEV) {
      console.warn(`[i18n] Status text not found: ${key}`)
    }
    return `[${statusType}.${statusCode}]`
  }
  return result
}

export type { StatusType }
