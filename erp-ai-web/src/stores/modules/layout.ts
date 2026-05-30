import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

const COLLAPSED_KEY = 'erp_layout_collapsed'

export const useLayoutStore = defineStore('layout', () => {
  const savedCollapsed = localStorage.getItem(COLLAPSED_KEY)
  const isCollapsed = ref<boolean>(savedCollapsed === 'true')
  const isFullscreen = ref<boolean>(!!document.fullscreenElement)

  watch(isCollapsed, (val) => {
    localStorage.setItem(COLLAPSED_KEY, String(val))
  })

  function toggleCollapse() {
    isCollapsed.value = !isCollapsed.value
  }

  async function toggleFullscreen() {
    if (!document.fullscreenElement) {
      await document.documentElement.requestFullscreen()
      isFullscreen.value = true
    } else {
      await document.exitFullscreen()
      isFullscreen.value = false
    }
  }

  function initFullscreenListener() {
    document.addEventListener('fullscreenchange', () => {
      isFullscreen.value = !!document.fullscreenElement
    })
  }

  return {
    isCollapsed,
    isFullscreen,
    toggleCollapse,
    toggleFullscreen,
    initFullscreenListener
  }
})
