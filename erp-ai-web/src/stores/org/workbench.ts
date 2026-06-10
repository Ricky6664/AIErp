import { defineStore } from 'pinia'
import { getOrgWorkbenchApi, type OrgWorkbenchVO } from '@/api/org/workbench'

interface OrgWorkbenchState {
  data: OrgWorkbenchVO | null
  loading: boolean
  lastFetchTime: number | null
}

export const useOrgWorkbenchStore = defineStore('orgWorkbench', {
  state: (): OrgWorkbenchState => ({
    data: null,
    loading: false,
    lastFetchTime: null
  }),

  getters: {
    hasData: (state): boolean => state.data !== null,
    isStale: (state): boolean => {
      if (!state.lastFetchTime) return true
      return Date.now() - state.lastFetchTime > 5 * 60 * 1000
    }
  },

  actions: {
    async fetchData(force = false): Promise<void> {
      if (this.data && !force && !this.isStale) return
      this.loading = true
      try {
        this.data = await getOrgWorkbenchApi()
        this.lastFetchTime = Date.now()
      } finally {
        this.loading = false
      }
    },

    clearCache(): void {
      this.data = null
      this.lastFetchTime = null
    }
  },

  persist: {
    key: 'erp_org_workbench',
    pick: ['data', 'lastFetchTime']
  }
})
