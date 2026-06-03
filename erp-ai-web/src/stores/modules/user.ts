import { defineStore } from 'pinia'
import type { IUserState } from '@/types/user'
import type { LoginDTO } from '@/api/types/auth'
import router from '@/router'
import { loginApi, getUserInfoApi, logoutApi } from '@/api/modules/auth'
import { TOKEN_KEY, REFRESH_TOKEN_KEY } from '@/router/constants'

export const useUserStore = defineStore('user', {
  state: (): IUserState => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    refreshToken: localStorage.getItem(REFRESH_TOKEN_KEY) || '',
    userInfo: null,
    permissions: [],
    roles: [],
    menuTree: []
  }),

  getters: {
    isLoggedIn: (state): boolean => !!state.token,
    hasPermission:
      (state) =>
      (perm: string): boolean =>
        state.permissions.includes(perm),
    avatar: (state): string => state.userInfo?.avatar || '/default-avatar.png',
    nickname: (state): string => state.userInfo?.nickname || state.userInfo?.username || '用户'
  },

  actions: {
    async login(credentials: LoginDTO) {
      const data = await loginApi(credentials)
      this.token = data.token
      localStorage.setItem(TOKEN_KEY, data.token)
      if (data.refreshToken) {
        this.refreshToken = data.refreshToken
        localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken)
      }
      await this.getInfo()
    },

    async getInfo() {
      try {
        const data = await getUserInfoApi()
        this.userInfo = data.userInfo
        this.permissions = data.permissions ?? []
        this.roles = data.roles ?? []
        this.menuTree = data.menuTree ?? []
      } catch {
        this.logout()
      }
    },

    logout() {
      logoutApi().catch(() => {
        // Token may already be expired, still proceed with local cleanup
      })
      this.token = ''
      this.refreshToken = ''
      this.userInfo = null
      this.permissions = []
      this.roles = []
      this.menuTree = []
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(REFRESH_TOKEN_KEY)
      const currentPath = router.currentRoute.value?.fullPath || '/'
      router.replace({ path: '/login', query: { redirect: currentPath } })
    }
  },

  persist: {
    key: 'erp_user',
    pick: ['token', 'refreshToken', 'userInfo', 'permissions', 'roles', 'menuTree']
  }
})
