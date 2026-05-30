import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw, RouterScrollBehavior } from 'vue-router'
import { staticRoutes } from './modules/static'

const scrollBehavior: RouterScrollBehavior = (to, _from, savedPosition) => {
  if (savedPosition) return savedPosition
  if (to.hash) return { el: to.hash, behavior: 'smooth' }
  return { top: 0, behavior: 'smooth' }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: staticRoutes as RouteRecordRaw[],
  scrollBehavior,
  strict: true
})

export default router
