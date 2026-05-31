import Decimal from 'decimal.js'

/** 舍入模式 */
export type RoundingMode = 'ROUND_HALF_UP' | 'ROUND_HALF_EVEN' | 'ROUND_DOWN' | 'ROUND_UP'

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

/** decimal.js 舍入模式映射 */
const DECIMAL_ROUNDING: Record<RoundingMode, Decimal.Rounding> = {
  ROUND_HALF_UP: Decimal.ROUND_HALF_UP,
  ROUND_HALF_EVEN: Decimal.ROUND_HALF_EVEN,
  ROUND_DOWN: Decimal.ROUND_DOWN,
  ROUND_UP: Decimal.ROUND_UP
}

function toSafeDecimal(value: number | string, label?: string): Decimal | null {
  try {
    const d = new Decimal(typeof value === 'string' ? value : value.toString())
    if (d.isNaN()) {
      if (label) console.warn('[number] ' + label + ' is NaN, using 0')
      return null
    }
    return d
  } catch {
    if (label) console.warn('[number] Invalid ' + label + ': ' + String(value) + ', using 0')
    return null
  }
}

/**
 * 精确加法（解决0.1+0.2!==0.3问题）
 * @param a - 加数
 * @param b - 被加数
 * @returns 精确结果
 */
export function add(a: number | string, b: number | string): number {
  const da = toSafeDecimal(a, 'add a')
  const db = toSafeDecimal(b, 'add b')
  if (!da || !db) return 0
  return da.plus(db).toNumber()
}

/**
 * 精确减法
 * @param a - 被减数
 * @param b - 减数
 * @returns 精确结果
 */
export function subtract(a: number | string, b: number | string): number {
  const da = toSafeDecimal(a, 'subtract a')
  const db = toSafeDecimal(b, 'subtract b')
  if (!da || !db) return 0
  return da.minus(db).toNumber()
}

/**
 * 精确乘法
 * @param a - 乘数
 * @param b - 被乘数
 * @returns 精确结果
 */
export function multiply(a: number | string, b: number | string): number {
  const da = toSafeDecimal(a, 'multiply a')
  const db = toSafeDecimal(b, 'multiply b')
  if (!da || !db) return 0
  return da.times(db).toNumber()
}

/**
 * 精确除法（含除零保护）
 * @param a - 被除数
 * @param b - 除数，为0时返回0
 * @returns 精确结果，无限循环小数保留8位
 */
export function divide(a: number | string, b: number | string): number {
  const da = toSafeDecimal(a, 'divide a')
  const db = toSafeDecimal(b, 'divide b')
  if (!da || !db) return 0
  if (db.isZero()) {
    console.warn('[number] divide by zero, returning 0')
    return 0
  }
  return da.div(db).toDecimalPlaces(8).toNumber()
}

/**
 * 银行家舍入（四舍六入五成双）
 * @param value - 待舍入的值
 * @param decimals - 保留小数位数
 * @param mode - 舍入模式，默认ROUND_HALF_EVEN(银行家舍入)
 * @returns 舍入后的结果
 */
export function round(
  value: number | string,
  decimals: number,
  mode: RoundingMode = 'ROUND_HALF_EVEN'
): number {
  const d = toSafeDecimal(value, 'round value')
  if (!d) return 0
  return d.toDecimalPlaces(decimals, DECIMAL_ROUNDING[mode]).toNumber()
}
