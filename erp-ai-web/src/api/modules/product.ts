import request from '@/utils/request'
import type { PageResult } from '@/types/api'
import type {
  ProductQueryDTO,
  ProductCreateDTO,
  ProductUpdateDTO,
  ProductListVO,
  ProductDetailVO
} from '@/api/types/product'

/** 分页查询商品列表 */
export function getProductPage(params: ProductQueryDTO): Promise<PageResult<ProductListVO>> {
  return request.get('/api/product/page', { params })
}

/** 查询商品详情 */
export function getProductDetail(id: number): Promise<ProductDetailVO> {
  return request.get(`/api/product/${id}`)
}

/** 新增商品 */
export function createProduct(data: ProductCreateDTO): Promise<number> {
  return request.post('/api/product', data)
}

/** 修改商品 */
export function updateProduct(data: ProductUpdateDTO): Promise<void> {
  return request.put(`/api/product/${data.id}`, data)
}

/** 删除商品 */
export function deleteProduct(id: number): Promise<void> {
  return request.delete(`/api/product/${id}`)
}
