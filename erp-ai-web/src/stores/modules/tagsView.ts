import { defineStore } from 'pinia'

interface TagsViewState {
  cachedViews: string[]
}

export const useTagsViewStore = defineStore('tagsView', {
  state: (): TagsViewState => ({
    cachedViews: []
  }),
  actions: {
    addCachedView(view: string) {
      if (!this.cachedViews.includes(view)) {
        this.cachedViews.push(view)
      }
    },
    removeCachedView(view: string) {
      const index = this.cachedViews.indexOf(view)
      if (index > -1) {
        this.cachedViews.splice(index, 1)
      }
    }
  }
})
