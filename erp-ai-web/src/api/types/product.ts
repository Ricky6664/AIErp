/** 商品查询参数 */
export interface ProductQueryDTO {
  name?: string
  code?: string
  categoryId?: number
  status?: number
  pageNum?: number
  pageSize?: number
}

/** 商品创建参数 */
export interface ProductCreateDTO {
  name: string
  code: string
  categoryId?: number
  unit?: string
  spec?: string
  price?: number
  cost?: number
  barcode?: string
  remark?: string
}

/** 商品更新参数 */
export interface ProductUpdateDTO extends Partial<ProductCreateDTO> {
  id: number
}

/** 商品列表项 */
export interface ProductListVO {
  id: number
  name: string
  code: string
  categoryName?: string
  unit?: string
  spec?: string
  price?: number
  stock?: number
  status: number
  createTime: string
}

/** 商品详情 */
export interface ProductDetailVO extends ProductListVO {
  cost?: number
  barcode?: string
  remark?: string
  updateTime: string
}
