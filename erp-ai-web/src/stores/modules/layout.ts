import { defineStore } from 'pinia'

interface LayoutState {
  isCollapsed: boolean
}

export const useLayoutStore = defineStore('layout', {
  state: (): LayoutState => ({
    isCollapsed: false
  }),
  actions: {
    toggleCollapse() {
      this.isCollapsed = !this.isCollapsed
    }
  }
})
