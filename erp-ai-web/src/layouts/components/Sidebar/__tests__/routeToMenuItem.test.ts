import { describe, it, expect } from 'vitest'
import { routeToMenuItem } from '../types'
import type { RouteRecordRaw } from 'vue-router'

function makeRoute(overrides: Partial<RouteRecordRaw> = {}): RouteRecordRaw {
  return {
    path: '/test',
    meta: { title: 'Test' },
    ...overrides
  }
}

describe('routeToMenuItem', () => {
  it('should convert a route without children', () => {
    const route = makeRoute({ path: '/dashboard', meta: { title: '仪表盘' } })
    const result = routeToMenuItem(route)
    expect(result.path).toBe('/dashboard')
    expect(result.title).toBe('仪表盘')
    expect(result.children).toBeUndefined()
    expect(result.hideMenu).toBeUndefined()
  })

  it('should convert a route with children recursively', () => {
    const route = makeRoute({
      path: '/system',
      meta: { title: '系统管理' },
      children: [
        makeRoute({ path: '/system/user', meta: { title: '用户管理' } }),
        makeRoute({ path: '/system/role', meta: { title: '角色管理' } })
      ]
    })
    const result = routeToMenuItem(route)
    expect(result.path).toBe('/system')
    expect(result.title).toBe('系统管理')
    expect(result.children).toHaveLength(2)
    expect(result.children![0].path).toBe('/system/user')
    expect(result.children![0].title).toBe('用户管理')
    expect(result.children![1].path).toBe('/system/role')
  })

  it('should propagate hideMenu from meta', () => {
    const route = makeRoute({
      path: '/hidden',
      meta: { title: 'Hidden', hideMenu: true }
    })
    const result = routeToMenuItem(route)
    expect(result.hideMenu).toBe(true)
  })

  it('should propagate icon and iconType from meta', () => {
    const route = makeRoute({
      path: '/icons',
      meta: { title: 'Icons', icon: 'Setting', iconType: 'element' as const }
    })
    const result = routeToMenuItem(route)
    expect(result.icon).toBe('Setting')
    expect(result.iconType).toBe('element')
  })

  it('should use empty string title when meta.title is missing', () => {
    const route: RouteRecordRaw = { path: '/no-title', meta: {} }
    const result = routeToMenuItem(route)
    expect(result.title).toBe('')
  })

  it('should handle deeply nested routes (3+ levels)', () => {
    const route = makeRoute({
      path: '/level1',
      meta: { title: 'L1' },
      children: [
        makeRoute({
          path: '/level1/level2',
          meta: { title: 'L2' },
          children: [makeRoute({ path: '/level1/level2/level3', meta: { title: 'L3' } })]
        })
      ]
    })
    const result = routeToMenuItem(route)
    expect(result.children![0].children![0].title).toBe('L3')
  })
})

describe('MenuItemData interface (structural)', () => {
  it('should preserve meta field in output', () => {
    const route = makeRoute({
      path: '/with-meta',
      meta: { title: 'Test', keepAlive: true, permissions: ['sys:view'] }
    })
    const result = routeToMenuItem(route)
    expect(result.meta).toBeDefined()
    expect(result.meta?.keepAlive).toBe(true)
    expect(result.meta?.permissions).toEqual(['sys:view'])
  })

  it('should set titleI18n when meta has titleI18n', () => {
    const route = makeRoute({
      path: '/i18n',
      meta: { title: 'I18n', titleI18n: 'menu.system' }
    })
    const result = routeToMenuItem(route)
    expect(result.titleI18n).toBe('menu.system')
  })
})
