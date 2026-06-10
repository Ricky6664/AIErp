import request from '@/utils/request'
import type { MenuItem, MenuResponse } from '@/api/types/menu'
import type {
  SysMenuListItem,
  SysMenuPageQuery,
  SysMenuPageResult,
  SysMenuCreateDTO,
  SysMenuUpdateDTO,
  SysMenuDetail
} from '@/api/types/menu'

/** 获取用户菜单和权限信息 */
export function getMenuList(): Promise<MenuResponse> {
  return request.get('/api/system/user/getInfo')
}

/** 扁平化菜单树(用于权限标识查找) */
export function flattenMenuTree(menus: MenuItem[]): MenuItem[] {
  const result: MenuItem[] = []
  function traverse(items: MenuItem[]) {
    items.forEach((item) => {
      result.push(item)
      if (item.children?.length) {
        traverse(item.children)
      }
    })
  }
  traverse(menus)
  return result
}

/** 从菜单树中查找权限标识列表 */
export function extractPermissions(menus: MenuItem[]): string[] {
  const flat = flattenMenuTree(menus)
  return flat.filter((item) => item.permissions).map((item) => item.permissions!)
}

/** 获取菜单树（用于部门筛选等场景） */
export function getMenuTree(): Promise<MenuItem[]> {
  return request.get('/api/system/menus/tree')
}

// ==================== 菜单管理 CRUD ====================

/** 分页查询菜单列表 */
export function getSysMenuPageList(params: SysMenuPageQuery): Promise<SysMenuPageResult> {
  return request.get('/api/system/menus', { params }) as Promise<SysMenuPageResult>
}

/** 查询菜单详情 */
export function getSysMenuDetail(id: number): Promise<SysMenuDetail> {
  return request.get(`/api/system/menus/${id}`) as Promise<SysMenuDetail>
}

/** 检查权限编码唯一性 */
export function checkMenuPermissionCode(
  permissionCode: string,
  excludeId?: number
): Promise<boolean> {
  return request.get('/api/system/menus/check-code', {
    params: { permissionCode, excludeId }
  }) as Promise<boolean>
}

/** 新增菜单 */
export function createSysMenu(data: SysMenuCreateDTO): Promise<number> {
  return request.post('/api/system/menus', data) as Promise<number>
}

/** 修改菜单 */
export function updateSysMenu(id: number, data: SysMenuUpdateDTO): Promise<void> {
  return request.put(`/api/system/menus/${id}`, data) as Promise<void>
}

/** 修改菜单状态(启用/禁用) */
export function updateSysMenuStatus(id: number, isEnabled: boolean): Promise<void> {
  return request.put(`/api/system/menus/${id}/status`, { isEnabled }) as Promise<void>
}

/** 删除菜单(含子菜单) */
export function deleteSysMenu(id: number): Promise<void> {
  return request.delete(`/api/system/menus/${id}`) as Promise<void>
}

/** 获取完整菜单树(管理端) */
export function getSysMenuTree(): Promise<SysMenuListItem[]> {
  return request.get('/api/system/menus/tree') as Promise<SysMenuListItem[]>
}
