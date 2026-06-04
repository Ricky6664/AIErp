import { describe, it, expect } from 'vitest'
import { hasPermission, filterRoutesByPermission } from '../permission'
import type { RouteRecordRaw } from 'vue-router'

describe('hasPermission (utils)', () => {
  it('should return true when user has wildcard permission', () => {
    expect(hasPermission(['system:user:view'], ['*'])).toBe(true)
  })

  it('should return true when user has admin permission', () => {
    expect(hasPermission(['system:user:view'], ['admin'])).toBe(true)
  })

  it('should return true when route has no permission requirements', () => {
    expect(hasPermission(undefined, [])).toBe(true)
    expect(hasPermission([], [])).toBe(true)
  })

  it('should return true when user has matching permission', () => {
    expect(hasPermission(['system:user:view'], ['system:user:view', 'system:role:view'])).toBe(true)
  })

  it('should return false when user has no matching permission', () => {
    expect(hasPermission(['system:user:delete'], ['system:user:view'])).toBe(false)
  })

  it('should match any permission from route requirements (OR logic)', () => {
    expect(hasPermission(['system:user:add', 'system:user:delete'], ['system:user:delete'])).toBe(
      true
    )
  })

  it('should return false when user permissions array is empty', () => {
    expect(hasPermission(['system:user:view'], [])).toBe(false)
  })
})

function makeRoute(overrides: Partial<RouteRecordRaw> = {}): RouteRecordRaw {
  return {
    path: '/test',
    meta: {},
    ...overrides
  }
}

describe('filterRoutesByPermission', () => {
  it('should return all routes when user has wildcard', () => {
    const routes: RouteRecordRaw[] = [
      makeRoute({ path: '/a', meta: { permissions: ['sys:a'] } }),
      makeRoute({ path: '/b', meta: { permissions: ['sys:b'] } })
    ]
    const result = filterRoutesByPermission(routes, ['*'])
    expect(result).toHaveLength(2)
  })

  it('should filter out routes user has no permission for', () => {
    const routes: RouteRecordRaw[] = [
      makeRoute({ path: '/a', meta: { permissions: ['sys:a'] } }),
      makeRoute({ path: '/b', meta: { permissions: ['sys:b'] } })
    ]
    const result = filterRoutesByPermission(routes, ['sys:a'])
    expect(result).toHaveLength(1)
    expect(result[0].path).toBe('/a')
  })

  it('should include routes with no permission requirements', () => {
    const routes: RouteRecordRaw[] = [
      makeRoute({ path: '/public', meta: {} }),
      makeRoute({ path: '/admin', meta: { permissions: ['admin:access'] } })
    ]
    const result = filterRoutesByPermission(routes, [])
    expect(result).toHaveLength(1)
    expect(result[0].path).toBe('/public')
  })

  it('should recursively filter children and remove parents with no visible children', () => {
    const routes: RouteRecordRaw[] = [
      makeRoute({
        path: '/system',
        meta: { title: 'System' },
        children: [
          makeRoute({ path: '/system/user', meta: { permissions: ['user:view'] } }),
          makeRoute({ path: '/system/role', meta: { permissions: ['role:view'] } })
        ]
      })
    ]
    const result = filterRoutesByPermission(routes, ['user:view'])
    expect(result).toHaveLength(1)
    expect(result[0].path).toBe('/system')
    expect(result[0].children).toHaveLength(1)
    expect(result[0].children![0].path).toBe('/system/user')
  })

  it('should remove parent when all children are filtered out', () => {
    const routes: RouteRecordRaw[] = [
      makeRoute({
        path: '/admin',
        meta: { title: 'Admin' },
        children: [makeRoute({ path: '/admin/secret', meta: { permissions: ['secret:access'] } })]
      })
    ]
    const result = filterRoutesByPermission(routes, [])
    expect(result).toHaveLength(0)
  })

  it('should handle deeply nested route trees', () => {
    const routes: RouteRecordRaw[] = [
      makeRoute({
        path: '/a',
        children: [
          makeRoute({
            path: '/a/b',
            meta: { permissions: ['b:access'] },
            children: [makeRoute({ path: '/a/b/c', meta: { permissions: ['c:access'] } })]
          })
        ]
      })
    ]
    const result = filterRoutesByPermission(routes, ['c:access'])
    expect(result).toHaveLength(0)
  })

  it('should return empty array for empty input', () => {
    expect(filterRoutesByPermission([], ['*'])).toEqual([])
  })
})
