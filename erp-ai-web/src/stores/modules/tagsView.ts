import { defineStore } from 'pinia'
import { ref, computed, nextTick } from 'vue'
import type { RouteLocationNormalized } from 'vue-router'
import router from '@/router'

/** 标签页视图数据 */
export interface TagView {
  path: string
  fullPath: string
  name: string | symbol | null | undefined
  title: string
  icon?: string
  affix?: boolean
  keepAlive?: boolean
  query?: Record<string, string>
}

/** keep-alive最大缓存组件数 */
export const MAX_CACHED_VIEWS = 10

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

    // LRU淘汰：超过最大缓存数时移除最早的
    if (cachedViews.value.length > MAX_CACHED_VIEWS) {
      const removed = cachedViews.value.shift()
      console.log(`[KeepAlive] LRU淘汰: ${removed}`)
    }
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

  /** 判断是否为当前激活的标签 */
  function isActiveView(view: TagView): boolean {
    return view.fullPath === router.currentRoute.value.fullPath
  }

  /** 关闭标签并导航到下一个标签(优先右侧→左侧→首页) */
  function closeSelectedTag(view: TagView) {
    const currentIndex = visitedViews.value.findIndex((v) => v.fullPath === view.fullPath)
    const wasActive = isActiveView(view)
    delView(view)
    if (wasActive) {
      const nextView = visitedViews.value[currentIndex] ?? visitedViews.value[currentIndex - 1]
      if (nextView) {
        router.push(nextView.fullPath)
      } else {
        router.push('/home')
      }
    }
  }

  /** 刷新当前页面(通过exclude+nextTick+include实现) */
  async function refreshSelectedPage(route: RouteLocationNormalized) {
    const name = route.name as string
    delCachedView({ name } as TagView)
    await router.replace({ path: '/redirect' + route.fullPath })
    await nextTick()
    addCachedView(route)
  }

  /** 关闭左侧标签 */
  function closeLeftTags(currentView: TagView) {
    const currentIndex = visitedViews.value.findIndex((v) => v.fullPath === currentView.fullPath)
    visitedViews.value = visitedViews.value.filter((v, i) => i >= currentIndex || v.affix)
    syncCachedViews()
  }

  /** 关闭右侧标签 */
  function closeRightTags(currentView: TagView) {
    const currentIndex = visitedViews.value.findIndex((v) => v.fullPath === currentView.fullPath)
    visitedViews.value = visitedViews.value.filter((v, i) => i <= currentIndex || v.affix)
    syncCachedViews()
  }

  /** 关闭其他标签(保留当前与affix) */
  function closeOtherTags(currentView: TagView) {
    visitedViews.value = visitedViews.value.filter(
      (v) => v.affix || v.fullPath === currentView.fullPath
    )
    syncCachedViews()
  }

  /** 关闭全部标签(保留affix，导航到首页) */
  function closeAllTags() {
    visitedViews.value = affixTags.value
    cachedViews.value = affixTags.value.filter((v) => v.keepAlive).map((v) => v.name as string)
    router.push('/home')
  }

  /** 同步cachedViews与visitedViews */
  function syncCachedViews() {
    cachedViews.value = visitedViews.value
      .filter((v) => v.keepAlive)
      .map((v) => v.name as string)
      .filter(Boolean) as string[]
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
    updateVisitedView,
    isActiveView,
    closeSelectedTag,
    refreshSelectedPage,
    closeLeftTags,
    closeRightTags,
    closeOtherTags,
    closeAllTags,
    syncCachedViews
  }
})
