/** 数字格式化选项 */
export interface FormatNumberOptions {
  /** 小数位数，默认2 */
  decimals?: number
  /** 千分位分隔符，默认',' */
  thousandsSep?: string
  /** 小数点符号，默认'.' */
  decimalSep?: string
  /** 前缀（如货币符号），默认空 */
  prefix?: string
  /** 后缀（如单位），默认空 */
  suffix?: string
}

function toNumber(value: number | string | null | undefined): number {
  if (value === null || value === undefined) return NaN
  const num = typeof value === 'string' ? Number(value) : value
  return num
}

function isValidNumber(num: number): boolean {
  return !isNaN(num) && isFinite(num)
}

/**
 * 格式化金额（千分位+小数位）
 * @param value - 数值或可转换的字符串
 * @param decimals - 小数位数，默认2
 * @returns 格式化后的字符串，如 '1,234,567.89'
 */
export function formatMoney(
  value: number | string | null | undefined,
  decimals: number = 2
): string {
  const num = toNumber(value)
  if (!isValidNumber(num)) return '0.00'
  return new Intl.NumberFormat('zh-CN', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals
  }).format(num)
}

/**
 * 格式化百分比
 * @param value - 小数值（0.1234表示12.34%）
 * @param decimals - 小数位数，默认2
 * @returns 百分比字符串，如 '12.34%'
 */
export function formatPercent(
  value: number | string | null | undefined,
  decimals: number = 2
): string {
  const num = toNumber(value)
  if (!isValidNumber(num)) return '0.00%'
  return (num * 100).toFixed(decimals) + '%'
}

/**
 * 格式化数量（整数，无小数位）
 * @param value - 数量值
 * @returns 千分位整数格式字符串
 */
export function formatQty(value: number | string | null | undefined): string {
  const num = toNumber(value)
  if (!isValidNumber(num)) return '0'
  return new Intl.NumberFormat('zh-CN', {
    maximumFractionDigits: 0
  }).format(num)
}

/**
 * 格式化文件大小（B/KB/MB/GB/TB）
 * @param bytes - 字节数
 * @returns 人类可读的文件大小字符串
 */
export function formatFileSize(bytes: number | null | undefined): string {
  if (bytes === null || bytes === undefined || bytes === 0) return '0 B'
  const num = toNumber(bytes)
  if (!isValidNumber(num) || num < 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let unitIndex = 0
  let size = num
  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024
    unitIndex++
  }
  return size.toFixed(2) + ' ' + units[unitIndex]
}
