import { defineStore } from 'pinia'

interface UserState {
  userInfo: Record<string, unknown> | null
  menus: unknown[]
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    userInfo: null,
    menus: []
  }),
  actions: {
    async getUserInfo() {
      /* 用户信息获取逻辑在后续任务中实现 */
    },
    resetUser() {
      this.userInfo = null
      this.menus = []
    }
  }
})
