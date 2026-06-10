/// <reference types="vxe-table" />
/// <reference types="vxe-pc-ui" />

declare module 'vxe-table' {
  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface VxeTablePropTypes {}
}

/** 通用键值对对象 */
declare type RecordObject<T = unknown> = Record<string, T>

/** 分页查询参数 */
declare interface PageQuery {
  pageNum: number
  pageSize: number
  [key: string]: unknown
}

/** 分页响应结果（对应后端PageResult<T>） */
declare interface PageResult<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}

/** 统一API响应结构（对应后端R<T>） */
declare interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

/** 第三方库类型补全 */
declare interface Window {
  NProgress: {
    start: () => void
    done: () => void
    set: (n: number) => void
    inc: (amount?: number) => void
  }
}
