import type { RouteRecordRaw } from 'vue-router'

export function hasPermission(
  routePermissions: string[] | undefined,
  userPermissions: string[]
): boolean {
  if (userPermissions.includes('*') || userPermissions.includes('admin')) return true
  if (!routePermissions || routePermissions.length === 0) return true
  return routePermissions.some((p) => userPermissions.includes(p))
}

export function filterRoutesByPermission(
  routes: RouteRecordRaw[],
  userPermissions: string[]
): RouteRecordRaw[] {
  return routes.reduce<RouteRecordRaw[]>((acc, route) => {
    const routePerms = route.meta?.permissions as string[] | undefined

    if (!hasPermission(routePerms, userPermissions)) return acc

    const filteredRoute: RouteRecordRaw = { ...route }
    if (route.children?.length) {
      filteredRoute.children = filterRoutesByPermission(route.children, userPermissions)
      if (filteredRoute.children.length === 0) return acc
    }

    acc.push(filteredRoute)
    return acc
  }, [])
}
