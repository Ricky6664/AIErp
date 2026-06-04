import { describe, it, expect, beforeEach } from 'vitest'
import {
  isExternalIcon,
  isSvgIcon,
  normalizePath,
  processMenuData,
  getCachedMenus,
  clearMenuCache
} from '../menuPipeline'
import type { MenuItem } from '@/api/types/menu'

function makeMenuItem(overrides: Partial<MenuItem> = {}): MenuItem {
  return {
    id: 1,
    parentId: 0,
    name: 'Test',
    path: '/test',
    component: '',
    type: 1,
    sort: 0,
    visible: true,
    permissions: '',
    ...overrides
  }
}

describe('isExternalIcon', () => {
  it('should return true for http URLs', () => {
    expect(isExternalIcon('http://example.com/icon.png')).toBe(true)
  })

  it('should return true for https URLs', () => {
    expect(isExternalIcon('https://example.com/icon.png')).toBe(true)
  })

  it('should return true for protocol-relative URLs', () => {
    expect(isExternalIcon('//example.com/icon.png')).toBe(true)
  })

  it('should return false for Element Plus icon names', () => {
    expect(isExternalIcon('Setting')).toBe(false)
  })

  it('should return false for svg: prefixed icons', () => {
    expect(isExternalIcon('svg:custom-icon')).toBe(false)
  })
})

describe('isSvgIcon', () => {
  it('should return true for svg: prefixed strings', () => {
    expect(isSvgIcon('svg:custom-icon')).toBe(true)
  })

  it('should return false for non-prefixed strings', () => {
    expect(isSvgIcon('custom-icon')).toBe(false)
  })

  it('should return false for URLs', () => {
    expect(isSvgIcon('https://example.com/icon.svg')).toBe(false)
  })

  it('should return false for empty string', () => {
    expect(isSvgIcon('')).toBe(false)
  })
})

describe('normalizePath', () => {
  it('should return path unchanged if it starts with /', () => {
    expect(normalizePath('/dashboard')).toBe('/dashboard')
  })

  it('should add leading / when missing', () => {
    expect(normalizePath('dashboard')).toBe('/dashboard')
  })

  it('should combine with parentPath when path is relative', () => {
    expect(normalizePath('user', '/system')).toBe('/system/user')
  })

  it('should handle parentPath without trailing slash', () => {
    expect(normalizePath('user', '/system/')).toBe('/system/user')
  })

  it('should return path as-is if it is already absolute even with parentPath', () => {
    expect(normalizePath('/absolute', '/system')).toBe('/absolute')
  })
})

describe('processMenuData', () => {
  it('should normalize paths on all menu items', () => {
    const menus: MenuItem[] = [makeMenuItem({ path: 'dashboard' })]
    const result = processMenuData(menus)
    expect(result[0].path).toBe('/dashboard')
  })

  it('should mark external URL icons with iconType external', () => {
    const menus: MenuItem[] = [makeMenuItem({ icon: 'https://cdn.example.com/icon.png' })]
    const result = processMenuData(menus)
    expect(result[0].iconType).toBe('external')
  })

  it('should mark svg: prefix icons with iconType svg and strip prefix', () => {
    const menus: MenuItem[] = [makeMenuItem({ icon: 'svg:custom-icon' })]
    const result = processMenuData(menus)
    expect(result[0].iconType).toBe('svg')
    expect(result[0].icon).toBe('custom-icon')
  })

  it('should mark regular icon names as element type', () => {
    const menus: MenuItem[] = [makeMenuItem({ icon: 'Setting' })]
    const result = processMenuData(menus)
    expect(result[0].iconType).toBe('element')
  })

  it('should not set iconType when icon is undefined', () => {
    const menus: MenuItem[] = [makeMenuItem({ icon: undefined })]
    const result = processMenuData(menus)
    expect(result[0].iconType).toBeUndefined()
  })

  it('should recursively process children', () => {
    const menus: MenuItem[] = [
      makeMenuItem({
        path: '/parent',
        icon: 'Folder',
        children: [makeMenuItem({ id: 2, path: '/parent/child', icon: 'svg:child-icon' })]
      })
    ]
    const result = processMenuData(menus)
    expect(result[0].path).toBe('/parent')
    expect(result[0].iconType).toBe('element')
    expect(result[0].children![0].path).toBe('/parent/child')
    expect(result[0].children![0].iconType).toBe('svg')
    expect(result[0].children![0].icon).toBe('child-icon')
  })

  it('should handle empty menu array', () => {
    expect(processMenuData([])).toEqual([])
  })
})

describe('getCachedMenus / clearMenuCache', () => {
  beforeEach(() => {
    clearMenuCache()
  })

  it('should return the same reference for identical input', () => {
    const menus = [makeMenuItem({ path: '/test' })]
    const result1 = getCachedMenus(menus)
    const result2 = getCachedMenus(menus)
    expect(result1).toBe(result2)
  })

  it('should return different results after cache clear', () => {
    const menus = [makeMenuItem({ path: '/test' })]
    const result1 = getCachedMenus(menus)
    clearMenuCache()
    const result2 = getCachedMenus(menus)
    expect(result1).not.toBe(result2)
    expect(result1).toEqual(result2)
  })

  it('should bust cache when input changes', () => {
    const menus1 = [makeMenuItem({ path: '/a' })]
    const menus2 = [makeMenuItem({ path: '/b' })]
    const result1 = getCachedMenus(menus1)
    const result2 = getCachedMenus(menus2)
    expect(result1).not.toBe(result2)
  })
})
