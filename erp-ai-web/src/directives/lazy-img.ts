import type { Directive } from 'vue'

/** 扩展HTMLImageElement，挂载observer实例 */
interface LazyImgElement extends HTMLImageElement {
  _lazyObserver?: IntersectionObserver
  _lazyErrorSrc?: string
}

/** 1x1 透明 SVG 占位图，避免额外 HTTP 请求 */
const PLACEHOLDER =
  'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1 1"%3E%3C/svg%3E'
const DEFAULT_ERROR_IMG = '/placeholder.png'

function createObserver(el: LazyImgElement, src: string): IntersectionObserver {
  const observer = new IntersectionObserver(
    ([entry]) => {
      if (entry.isIntersecting) {
        el.src = src
        observer.unobserve(el)
      }
    },
    { rootMargin: '200px', threshold: 0.01 }
  )
  return observer
}

/**
 * v-lazy-img 图片懒加载指令
 * 绑定值为图片URL字符串
 */
export const vLazyImg: Directive<LazyImgElement, string> = {
  mounted(el, binding) {
    el.src = PLACEHOLDER

    el.onerror = () => {
      el.src = el._lazyErrorSrc || DEFAULT_ERROR_IMG
    }

    const observer = createObserver(el, binding.value)
    el._lazyObserver = observer
    observer.observe(el)
  },

  updated(el, binding) {
    if (binding.value === binding.oldValue) return

    el.src = PLACEHOLDER
    el._lazyObserver?.disconnect()

    const observer = createObserver(el, binding.value)
    el._lazyObserver = observer
    observer.observe(el)
  },

  unmounted(el) {
    el._lazyObserver?.disconnect()
  }
}
