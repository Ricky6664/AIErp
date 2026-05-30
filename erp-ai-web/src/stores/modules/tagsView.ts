import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { RouteLocationNormalized } from 'vue-router'

/** 标签页视图数据 */
interface TagView {
  path: string
  fullPath: string
  name: string | symbol | null | undefined
  title: string
  icon?: string
  affix?: boolean // 固定标签(不可关闭)
  keepAlive?: boolean // 是否缓存
  query?: Record<string, string>
}

export const useTagsViewStore = defineStore('tagsView', () => {
  // 已打开的标签列表
  const visitedViews = ref<TagView[]>([])
  // keep-alive缓存的组件name列表
  const cachedViews = ref<string[]>([])

  // 固定标签列表(首页等affix=true的路由)
  const affixTags = computed(() => visitedViews.value.filter((v) => v.affix))

  /** 添加标签 */
  function addView(route: RouteLocationNormalized) {
    addVisitedView(route)
    addCachedView(route)
  }

  /** 添加到已访问列表 */
  function addVisitedView(route: RouteLocationNormalized) {
    const exists = visitedViews.value.some((v) => v.fullPath === route.fullPath)
    if (exists) return
    visitedViews.value.push({
      path: route.path,
      fullPath: route.fullPath,
      name: route.name,
      title: (route.meta?.title as string) || '未命名',
      icon: route.meta?.icon as string,
      affix: route.meta?.affix as boolean,
      keepAlive: route.meta?.keepAlive as boolean,
      query: route.query as Record<string, string>
    })
  }

  /** 添加到缓存列表 */
  function addCachedView(route: RouteLocationNormalized) {
    const name = route.name as string
    if (!name) return
    if (!route.meta?.keepAlive) return
    if (cachedViews.value.includes(name)) return
    cachedViews.value.push(name)
  }

  /** 关闭标签 */
  function delView(route: TagView) {
    const i = visitedViews.value.findIndex((v) => v.fullPath === route.fullPath)
    if (i > -1) visitedViews.value.splice(i, 1)
    delCachedView(route)
  }

  /** 移除缓存 */
  function delCachedView(route: TagView) {
    const name = route.name as string
    const i = cachedViews.value.indexOf(name)
    if (i > -1) cachedViews.value.splice(i, 1)
  }

  /** 关闭其他标签 */
  function delOtherViews(route: TagView) {
    visitedViews.value = visitedViews.value.filter((v) => v.affix || v.fullPath === route.fullPath)
    cachedViews.value = visitedViews.value.filter((v) => v.keepAlive).map((v) => v.name as string)
  }

  /** 关闭全部标签(保留affix标签) */
  function delAllViews() {
    visitedViews.value = affixTags.value
    cachedViews.value = affixTags.value.filter((v) => v.keepAlive).map((v) => v.name as string)
  }

  /** 更新标签信息 */
  function updateVisitedView(route: RouteLocationNormalized) {
    const view = visitedViews.value.find((v) => v.fullPath === route.fullPath)
    if (view) {
      view.title = (route.meta?.title as string) || view.title
    }
  }

  return {
    visitedViews,
    cachedViews,
    affixTags,
    addView,
    addVisitedView,
    addCachedView,
    delView,
    delCachedView,
    delOtherViews,
    delAllViews,
    updateVisitedView
  }
})
