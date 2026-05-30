import { defineStore } from 'pinia'
import type { IParamState } from '@/types/dict'

export const useParamStore = defineStore('param', {
  state: (): IParamState => ({
    dictMap: {} as Record<string, import('@/types/dict').DictItem[]>,
    systemConfig: {} as Record<string, string>,
    loading: false
  })
})
