const viewModules = import.meta.glob('@/views/**/*.vue')

export function resolveComponent(componentPath: string) {
  const key = `/src/views/${componentPath}.vue`
  if (viewModules[key]) {
    return viewModules[key]
  }
  console.warn(`[Router] 未找到组件: ${key}`)
  return () => import('@/views/error/404.vue')
}
