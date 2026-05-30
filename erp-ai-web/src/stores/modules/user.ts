import { defineStore } from 'pinia'
import type { IUserState } from '@/types/user'
import type { LoginDTO } from '@/api/types/auth'
import router from '@/router'
import { loginApi, getUserInfoApi } from '@/api/modules/auth'

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
    async login(credentials: LoginDTO) {
      const data = await loginApi(credentials)
      this.token = data.token
      await this.getInfo()
    },

    async getInfo() {
      try {
        const data = await getUserInfoApi()
        this.userInfo = data.userInfo
        this.permissions = data.permissions ?? []
        this.roles = data.roles ?? []
      } catch {
        this.logout()
      }
    },

    logout() {
      this.token = ''
      this.userInfo = null
      this.permissions = []
      this.roles = []
      localStorage.removeItem('erp_user')
      router.replace('/login')
    }
  },

  persist: {
    key: 'erp_user',
    pick: ['token']
  }
})
