const viewModules = import.meta.glob('@/views/**/*.vue')

export function resolveComponent(componentPath: string) {
  if (!componentPath) {
    console.warn('[Router] componentPath is empty')
    return () => import('@/views/error/404.vue')
  }

  // 规范化路径：去除可能的 views/ 前缀和 .vue 后缀
  const normalized = componentPath
    .replace(/^views\//, '') // 去除 views/ 前缀
    .replace(/\.vue$/, '') // 去除 .vue 后缀

  // 尝试多种匹配方式
  const keysToTry = [
    `/src/views/${normalized}.vue`,
    `/src/views/${normalized}/index.vue`,
    `/src/${componentPath}${componentPath.endsWith('.vue') ? '' : '.vue'}`
  ]

  for (const key of keysToTry) {
    if (viewModules[key]) {
      return viewModules[key]
    }
  }

  console.warn(`[Router] 未找到组件: ${componentPath} (tried: ${keysToTry.join(', ')})`)
  return () => import('@/views/error/404.vue')
}
