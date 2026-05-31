import dayjs from 'dayjs'
import relativeTimePlugin from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTimePlugin)
dayjs.locale('zh-cn')

type DateFormat = 'YYYY-MM-DD' | 'YYYY-MM-DD HH:mm:ss' | 'YYYY/MM/DD' | string

export function formatDate(
  date: Date | string | number | null | undefined,
  format: DateFormat = 'YYYY-MM-DD HH:mm:ss'
): string {
  if (date === null || date === undefined || date === '') return ''
  const d = dayjs(date)
  if (!d.isValid()) return ''
  return d.format(format)
}

export function parseDate(value: string | number | Date | null | undefined): dayjs.Dayjs | null {
  if (value === null || value === undefined || value === '') return null
  try {
    const d = dayjs(value)
    if (!d.isValid()) return null
    return d
  } catch {
    return null
  }
}

export function dateRange(
  start: Date | string,
  end: Date | string,
  format: DateFormat = 'YYYY-MM-DD'
): string[] {
  const s = dayjs(start)
  const e = dayjs(end)
  if (!s.isValid() || !e.isValid() || s.isAfter(e)) return []
  const result: string[] = []
  let current = s
  while (!current.isAfter(e, 'day')) {
    result.push(current.format(format))
    current = current.add(1, 'day')
  }
  return result
}

export function relativeTime(date: Date | string | number): string {
  const d = dayjs(date)
  if (!d.isValid()) return ''
  const now = dayjs()
  const diffSeconds = now.diff(d, 'second')
  if (diffSeconds < 60) return '刚刚'
  if (diffSeconds < 3600) return `${Math.floor(diffSeconds / 60)}分钟前`
  if (diffSeconds < 86400) return `${Math.floor(diffSeconds / 3600)}小时前`
  if (diffSeconds < 172800) return '昨天'
  if (now.year() === d.year()) return d.format('MM-DD')
  return d.format('YYYY-MM-DD')
}
