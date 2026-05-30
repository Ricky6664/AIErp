import { defineStore } from 'pinia'
import type { IParamState, DictItem } from '@/types/dict'
import { getDictDataApi, getSystemConfigApi } from '@/api/modules/system'

const pendingPromises = new Map<string, Promise<void>>()

export const useParamStore = defineStore('param', {
  state: (): IParamState => ({
    dictMap: {} as Record<string, DictItem[]>,
    systemConfig: {} as Record<string, string>,
    loading: false
  }),

  actions: {
    async loadDict(dictType: string): Promise<void> {
      if (this.dictMap[dictType]) return
      const pending = pendingPromises.get(dictType)
      if (pending) return pending
      const promise = (async () => {
        try {
          const data = await getDictDataApi(dictType)
          this.dictMap[dictType] = data
        } finally {
          pendingPromises.delete(dictType)
        }
      })()
      pendingPromises.set(dictType, promise)
      return promise
    },

    async refreshDict(dictType: string): Promise<void> {
      delete this.dictMap[dictType]
      return this.loadDict(dictType)
    },

    async loadSystemConfig(): Promise<void> {
      const data = await getSystemConfigApi()
      this.systemConfig = data
    }
  }
})
