import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import router from '@/router'

interface UserInfo {
  id?: number
  username?: string
  nickname?: string
  avatar?: string
  email?: string
  [key: string]: unknown
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const menus = ref<unknown[]>([])

  const nickname = computed(() => userInfo.value?.nickname ?? '')
  const avatar = computed(() => userInfo.value?.avatar ?? '')

  async function getUserInfo() {
    /* 用户信息获取逻辑在后续任务中实现 */
  }

  function resetUser() {
    userInfo.value = null
    menus.value = []
  }

  async function logout() {
    resetUser()
    router.push('/login')
  }

  return {
    userInfo,
    menus,
    nickname,
    avatar,
    getUserInfo,
    resetUser,
    logout
  }
})
