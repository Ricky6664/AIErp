import type { PageResult } from '@/types/api'

export interface DemoOrderItem {
  id: number
  orderNo: string
  customerName: string
  productName: string
  quantity: number
  unitPrice: number
  totalAmount: number
  status: 'pending' | 'processing' | 'completed' | 'cancelled'
  orderDate: string
  deliveryDate: string
}

export interface DemoOrderQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  status?: string
  sortField?: string
  sortOrder?: 'asc' | 'desc'
}

const statuses: DemoOrderItem['status'][] = ['pending', 'processing', 'completed', 'cancelled']
const customers = [
  '华为技术',
  '阿里巴巴',
  '腾讯科技',
  '字节跳动',
  '百度在线',
  '京东集团',
  '小米科技',
  '网易网络'
]
const products = [
  'ERP管理系统V3',
  'CRM客户管理软件',
  'WMS仓储管理平台',
  'MES生产执行系统',
  'QMS质量管理系统',
  'HRM人力资源系统',
  'OA协同办公平台',
  'BI商业智能分析'
]

function randomInt(min: number, max: number): number {
  return Math.floor(Math.random() * (max - min + 1)) + min
}

function randomDate(start: string, end: string): string {
  const s = new Date(start).getTime()
  const e = new Date(end).getTime()
  return new Date(s + Math.random() * (e - s)).toISOString().slice(0, 10)
}

function generateMockData(total: number): DemoOrderItem[] {
  return Array.from({ length: total }, (_, i) => {
    const quantity = randomInt(1, 100)
    const unitPrice = randomInt(100, 50000) / 100
    const totalAmount = Math.round(quantity * unitPrice * 100) / 100
    return {
      id: i + 1,
      orderNo: `ORD-${String(i + 1).padStart(6, '0')}`,
      customerName: customers[randomInt(0, customers.length - 1)],
      productName: products[randomInt(0, products.length - 1)],
      quantity,
      unitPrice,
      totalAmount,
      status: statuses[randomInt(0, statuses.length - 1)],
      orderDate: randomDate('2025-01-01', '2026-06-05'),
      deliveryDate: randomDate('2026-01-01', '2026-12-31')
    }
  })
}

const MOCK_TOTAL = 156
let cachedData: DemoOrderItem[] | null = null

function getCachedData(): DemoOrderItem[] {
  if (!cachedData) {
    cachedData = generateMockData(MOCK_TOTAL)
  }
  return cachedData
}

export function getDemoOrderPage(params: DemoOrderQuery): Promise<PageResult<DemoOrderItem>> {
  return new Promise((resolve) => {
    setTimeout(() => {
      let data = [...getCachedData()]

      if (params.keyword) {
        const kw = params.keyword.toLowerCase()
        data = data.filter(
          (item) =>
            item.orderNo.toLowerCase().includes(kw) ||
            item.customerName.toLowerCase().includes(kw) ||
            item.productName.toLowerCase().includes(kw)
        )
      }

      if (params.status) {
        data = data.filter((item) => item.status === params.status)
      }

      if (params.sortField && params.sortOrder) {
        const field = params.sortField as keyof DemoOrderItem
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

      resolve({
        records,
        total,
        pageNum: params.pageNum,
        pageSize: params.pageSize
      })
    }, 300)
  })
}

let nextId = MOCK_TOTAL + 1

export function saveDemoOrder(
  data: Partial<DemoOrderItem> & { id?: number }
): Promise<DemoOrderItem> {
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
      const newItem: DemoOrderItem = {
        id: nextId++,
        orderNo: `ORD-${String(nextId - 1).padStart(6, '0')}`,
        customerName: data.customerName || customers[0],
        productName: data.productName || products[0],
        quantity: data.quantity || 1,
        unitPrice: data.unitPrice || 0,
        totalAmount: (data.quantity || 1) * (data.unitPrice || 0),
        status: data.status || 'pending',
        orderDate: data.orderDate || new Date().toISOString().slice(0, 10),
        deliveryDate: data.deliveryDate || ''
      }
      list.unshift(newItem)
      resolve(newItem)
    }, 200)
  })
}

export function deleteDemoOrder(id: number): Promise<void> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const list = getCachedData()
      const idx = list.findIndex((item) => item.id === id)
      if (idx >= 0) {
        list.splice(idx, 1)
        resolve()
      } else {
        reject(new Error(`订单 #${id} 不存在`))
      }
    }, 200)
  })
}

export function getDemoOrderDetail(id: number): Promise<DemoOrderItem> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const item = getCachedData().find((d) => d.id === id)
      if (item) {
        resolve({ ...item })
      } else {
        reject(new Error(`订单 #${id} 不存在`))
      }
    }, 150)
  })
}
