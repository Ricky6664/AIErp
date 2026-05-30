import { ref, computed, onMounted, onUnmounted } from 'vue'

export type Breakpoint = 'mobile' | 'tablet' | 'desktop'

const MOBILE_MAX = 768
const TABLET_MAX = 1024

export function useResponsive() {
  const breakpoint = ref<Breakpoint>('desktop')
  const windowWidth = ref<number>(window.innerWidth)

  function updateBreakpoint() {
    windowWidth.value = window.innerWidth
    if (windowWidth.value <= MOBILE_MAX) {
      breakpoint.value = 'mobile'
    } else if (windowWidth.value <= TABLET_MAX) {
      breakpoint.value = 'tablet'
    } else {
      breakpoint.value = 'desktop'
    }
  }

  const isMobile = computed<boolean>(() => breakpoint.value === 'mobile')
  const isTablet = computed<boolean>(() => breakpoint.value === 'tablet')

  onMounted(() => {
    updateBreakpoint()
    window.addEventListener('resize', updateBreakpoint)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', updateBreakpoint)
  })

  return { breakpoint, windowWidth, isMobile, isTablet }
}
