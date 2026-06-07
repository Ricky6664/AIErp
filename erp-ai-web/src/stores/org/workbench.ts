import { defineStore } from 'pinia'
import { getOrgWorkbenchApi, type OrgWorkbenchVO } from '@/api/org/workbench'

interface OrgWorkbenchState {
  data: OrgWorkbenchVO | null
  loading: boolean
  fetchedAt: number | null
}

const CACHE_TTL = 5 * 60 * 1000

export const useOrgWorkbenchStore = defineStore('orgWorkbench', {
  state: (): OrgWorkbenchState => ({
    data: null,
    loading: false,
    fetchedAt: null
  }),

  getters: {
    isCacheValid(): boolean {
      return this.fetchedAt !== null && Date.now() - this.fetchedAt < CACHE_TTL
    }
  },

  actions: {
    async fetchData(force = false): Promise<OrgWorkbenchVO> {
      if (!force && this.data && this.isCacheValid) {
        return this.data
      }

      this.loading = true
      try {
        this.data = await getOrgWorkbenchApi()
        this.fetchedAt = Date.now()
        return this.data
      } finally {
        this.loading = false
      }
    },

    clearCache(): void {
      this.data = null
      this.fetchedAt = null
    }
  }
})
