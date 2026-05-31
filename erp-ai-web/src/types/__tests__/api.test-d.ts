import { expectTypeOf, test } from 'vitest'
import type { ApiResponse, PageResult, PageQuery } from '../api'
import { isSuccess, getErrorMessage, assertSuccess } from '../../utils/apiHelper'

interface UserVO {
  id: number
  name: string
}

// ==================== ApiResponse<T> ====================

test('ApiResponse<UserVO> code is number', () => {
  expectTypeOf<ApiResponse<UserVO>>().toHaveProperty('code')
  expectTypeOf<ApiResponse<UserVO>['code']>().toEqualTypeOf<number>()
})

test('ApiResponse<UserVO> data is UserVO', () => {
  expectTypeOf<ApiResponse<UserVO>['data']>().toEqualTypeOf<UserVO>()
})

test('ApiResponse<UserVO> message is string', () => {
  expectTypeOf<ApiResponse<UserVO>>().toHaveProperty('message')
  expectTypeOf<ApiResponse<UserVO>['message']>().toEqualTypeOf<string>()
})

test('ApiResponse default generic data defaults to unknown', () => {
  expectTypeOf<ApiResponse['data']>().toEqualTypeOf<unknown>()
})

// ==================== PageResult<T> ====================

test('PageResult<UserVO> records is UserVO[]', () => {
  expectTypeOf<PageResult<UserVO>['records']>().toEqualTypeOf<UserVO[]>()
})

test('PageResult<UserVO> total is number', () => {
  expectTypeOf<PageResult<UserVO>['total']>().toEqualTypeOf<number>()
})

test('PageResult includes pageNum and pageSize', () => {
  expectTypeOf<PageResult<UserVO>>().toHaveProperty('pageNum')
  expectTypeOf<PageResult<UserVO>>().toHaveProperty('pageSize')
  expectTypeOf<PageResult<UserVO>['pageNum']>().toEqualTypeOf<number>()
  expectTypeOf<PageResult<UserVO>['pageSize']>().toEqualTypeOf<number>()
})

// ==================== PageQuery ====================

test('PageQuery pageNum is number', () => {
  expectTypeOf<PageQuery['pageNum']>().toEqualTypeOf<number>()
})

test('PageQuery pageSize is number', () => {
  expectTypeOf<PageQuery['pageSize']>().toEqualTypeOf<number>()
})

// ==================== isSuccess ====================

test('isSuccess returns boolean', () => {
  const response: ApiResponse<UserVO> = { code: 200, data: { id: 1, name: 'test' }, message: 'ok' }
  expectTypeOf(isSuccess(response)).toEqualTypeOf<boolean>()
})

// ==================== getErrorMessage ====================

test('getErrorMessage returns string', () => {
  const response: ApiResponse = { code: 500, data: null as unknown, message: 'error' }
  expectTypeOf(getErrorMessage(response)).toEqualTypeOf<string>()
})

// ==================== assertSuccess ====================

test('assertSuccess narrows data type to non-null', () => {
  const response: ApiResponse<UserVO> = { code: 0, data: { id: 1, name: 'test' }, message: 'ok' }
  assertSuccess(response)
  expectTypeOf(response.data).toEqualTypeOf<UserVO>()
})

// ==================== Fail response type ====================

test('ApiResponse with non-zero code still types data as T', () => {
  const response: ApiResponse<UserVO> = {
    code: 404,
    data: null as unknown as UserVO,
    message: 'not found'
  }
  expectTypeOf(response.data).toEqualTypeOf<UserVO>()
})
