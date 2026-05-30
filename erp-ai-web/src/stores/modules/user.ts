import { defineStore } from 'pinia'
import type { IUserState } from '@/types/user'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: (): IUserState & { menus: unknown[] } => ({
    token: localStorage.getItem('erp_user_token') || '',
    userInfo: null,
    permissions: [],
    roles: [],
    menus: []
  }),

  getters: {
    nickname: (state) => state.userInfo?.nickname ?? '',
    avatar: (state) => state.userInfo?.avatar ?? ''
  },

  actions: {
    async getUserInfo() {
      /* 用户信息获取逻辑在后续任务中实现 */
    },
    resetUser() {
      this.userInfo = null
      this.permissions = []
      this.roles = []
    },
    async logout() {
      this.resetUser()
      router.push('/login')
    }
  },

  persist: {
    key: 'erp_user',
    pick: ['token']
  }
})
