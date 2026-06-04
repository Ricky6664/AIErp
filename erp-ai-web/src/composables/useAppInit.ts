import { ref, watch } from 'vue'
import { useAppStore } from '@/stores/modules/app'
import { useUserStore } from '@/stores/modules/user'

let watermarkCanvas: HTMLCanvasElement | null = null

function renderWatermark(userName: string): void {
  if (watermarkCanvas) {
    watermarkCanvas.remove()
    watermarkCanvas = null
  }

  const canvas = document.createElement('canvas')
  canvas.width = 300
  canvas.height = 200
  canvas.style.position = 'fixed'
  canvas.style.top = '0'
  canvas.style.left = '0'
  canvas.style.width = '100vw'
  canvas.style.height = '100vh'
  canvas.style.zIndex = '9999'
  canvas.style.pointerEvents = 'none'
  canvas.style.opacity = '0.08'

  const ctx = canvas.getContext('2d')
  if (!ctx) return

  ctx.font = '14px sans-serif'
  ctx.fillStyle = '#000000'
  ctx.rotate((-30 * Math.PI) / 180)

  const now = new Date()
  const timeStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`

  for (let y = -200; y < window.innerHeight + 200; y += 160) {
    for (let x = -200; x < window.innerWidth + 200; x += 300) {
      ctx.fillText(`${userName} ${timeStr}`, x, y)
    }
  }

  document.body.appendChild(canvas)
  watermarkCanvas = canvas
}

function removeWatermark(): void {
  if (watermarkCanvas) {
    watermarkCanvas.remove()
    watermarkCanvas = null
  }
}

export function useAppInit() {
  const appStore = useAppStore()
  const initialized = ref(false)
  const initError = ref<string | null>(null)

  async function initialize(): Promise<void> {
    if (initialized.value) return
    try {
      await appStore.initAppConfig()
      initialized.value = true
    } catch {
      initError.value = '系统配置加载失败，使用默认配置'
      initialized.value = true
    }
  }

  function applyThemeColor(color: string): void {
    document.documentElement.style.setProperty('--el-color-primary', color)
    document.documentElement.style.setProperty('--app-system-primary', color)
  }

  function setupWatermark(): void {
    const userStore = useUserStore()
    const userName = userStore.userInfo?.nickname || userStore.userInfo?.username || ''

    if (appStore.watermarkEnabled) {
      renderWatermark(userName)
    }

    watch(
      () => appStore.watermarkEnabled,
      (enabled) => {
        if (enabled) {
          renderWatermark(userName)
        } else {
          removeWatermark()
        }
      }
    )
  }

  watch(
    () => appStore.themeColor,
    (newColor) => {
      applyThemeColor(newColor)
    }
  )

  return {
    initialized,
    initError,
    initialize,
    applyThemeColor,
    setupWatermark
  }
}
