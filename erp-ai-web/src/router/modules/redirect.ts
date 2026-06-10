import type { RouteRecordRaw } from 'vue-router'

export const REDIRECT_ROUTE: RouteRecordRaw = {
  path: '/redirect/:path(.*)*',
  name: 'Redirect',
  component: () => import('@/views/redirect/index.vue'),
  meta: { title: '重定向', hideMenu: true, hideTab: true }
}
