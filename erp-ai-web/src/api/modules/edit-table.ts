import type { PageResult } from '@/types/api'

/** 录入表格演示行数据 */
export interface EditTableRow {
  id: number
  lineNo: number
  productName: string
  spec: string
  quantity: number
  unitPrice: number
  totalAmount: number
  status: 'draft' | 'confirmed' | 'cancelled'
}

/** 录入表格查询参数 */
export interface EditTableQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  status?: string
  sortField?: string
  sortOrder?: 'asc' | 'desc'
}

const products = [
  'ERP管理系统V3',
  'CRM客户管理软件',
  'WMS仓储管理平台',
  'MES生产执行系统',
  'QMS质量管理系统'
]
const statuses: EditTableRow['status'][] = ['draft', 'confirmed', 'cancelled']

function generateMockRows(total: number): EditTableRow[] {
  return Array.from({ length: total }, (_, i) => {
    const quantity = Math.floor(Math.random() * 50) + 1
    const unitPrice = Math.round((Math.random() * 2000 + 100) * 100) / 100
    return {
      id: i + 1,
      lineNo: i + 1,
      productName: products[i % products.length],
      spec: `规格-${String.fromCharCode(65 + (i % 26))}`,
      quantity,
      unitPrice,
      totalAmount: Math.round(quantity * unitPrice * 100) / 100,
      status: statuses[i % statuses.length]
    }
  })
}

const MOCK_TOTAL = 8
let cachedData: EditTableRow[] | null = null

function getCachedData(): EditTableRow[] {
  if (!cachedData) {
    cachedData = generateMockRows(MOCK_TOTAL)
  }
  return cachedData
}

/** 分页查询录入表格数据 */
export function getEditTablePage(params: EditTableQuery): Promise<PageResult<EditTableRow>> {
  return new Promise((resolve) => {
    setTimeout(() => {
      let data = [...getCachedData()]

      if (params.keyword) {
        const kw = params.keyword.toLowerCase()
        data = data.filter(
          (item) =>
            item.productName.toLowerCase().includes(kw) || item.spec.toLowerCase().includes(kw)
        )
      }

      if (params.status) {
        data = data.filter((item) => item.status === params.status)
      }

      if (params.sortField && params.sortOrder) {
        const field = params.sortField as keyof EditTableRow
        const order = params.sortOrder === 'asc' ? 1 : -1
        data.sort((a, b) => {
          const va = a[field]
          const vb = b[field]
          if (typeof va === 'number' && typeof vb === 'number') {
            return (va - vb) * order
          }
          return String(va).localeCompare(String(vb)) * order
        })
      }

      const total = data.length
      const start = (params.pageNum - 1) * params.pageSize
      const records = data.slice(start, start + params.pageSize)

      resolve({ records, total, pageNum: params.pageNum, pageSize: params.pageSize })
    }, 300)
  })
}

let nextId = MOCK_TOTAL + 1

/** 保存录入表格行（新增/修改） */
export function saveEditTableRow(
  data: Partial<EditTableRow> & { id?: number }
): Promise<EditTableRow> {
  return new Promise((resolve) => {
    setTimeout(() => {
      const list = getCachedData()
      if (data.id) {
        const idx = list.findIndex((item) => item.id === data.id)
        if (idx >= 0) {
          list[idx] = { ...list[idx], ...data, id: list[idx].id }
          resolve(list[idx])
          return
        }
      }
      const quantity = data.quantity ?? 1
      const unitPrice = data.unitPrice ?? 0
      const newItem: EditTableRow = {
        id: nextId++,
        lineNo: data.lineNo ?? list.length + 1,
        productName: data.productName ?? '',
        spec: data.spec ?? '',
        quantity,
        unitPrice,
        totalAmount: Math.round(quantity * unitPrice * 100) / 100,
        status: data.status ?? 'draft'
      }
      list.unshift(newItem)
      resolve(newItem)
    }, 200)
  })
}

/** 删除录入表格行 */
export function deleteEditTableRow(id: number): Promise<void> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const list = getCachedData()
      const idx = list.findIndex((item) => item.id === id)
      if (idx >= 0) {
        list.splice(idx, 1)
        resolve()
      } else {
        reject(new Error(`行 #${id} 不存在`))
      }
    }, 200)
  })
}

/** 查询录入表格行详情 */
export function getEditTableDetail(id: number): Promise<EditTableRow> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const item = getCachedData().find((d) => d.id === id)
      if (item) {
        resolve({ ...item })
      } else {
        reject(new Error(`行 #${id} 不存在`))
      }
    }, 150)
  })
}

/** 重置缓存数据 */
export function resetEditTableCache(): void {
  cachedData = null
  nextId = MOCK_TOTAL + 1
}
