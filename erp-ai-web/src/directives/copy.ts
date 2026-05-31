import type { Directive, DirectiveBinding } from 'vue'

/** v-copy绑定值类型：待复制的文本 */
type CopyValue = string

/** 扩展HTMLElement以存储复制状态 */
interface CopyEl extends HTMLElement {
  /** 当前待复制的文本 */
  _copyValue?: string
  /** 点击事件处理函数 */
  _copyHandler?: () => void
}

/**
 * v-copy自定义指令定义
 * 点击元素时复制绑定文本到剪贴板
 * 用法：v-copy="orderNo" 点击元素复制orderNo到剪贴板
 */
export const copyDirective: Directive<CopyEl, CopyValue> = {
  mounted(el: CopyEl, binding: DirectiveBinding<CopyValue>) {
    el._copyValue = binding.value

    el._copyHandler = () => {
      const text = el._copyValue ?? binding.value ?? ''
      navigator.clipboard.writeText(text).catch(() => {
        // 降级方案：使用execCommand（兼容旧浏览器）
        const textarea = document.createElement('textarea')
        textarea.value = text
        textarea.style.position = 'fixed'
        textarea.style.opacity = '0'
        document.body.appendChild(textarea)
        textarea.select()
        document.execCommand('copy')
        document.body.removeChild(textarea)
      })
    }

    el.addEventListener('click', el._copyHandler)
  },

  updated(el: CopyEl, binding: DirectiveBinding<CopyValue>) {
    el._copyValue = binding.value
  },

  unmounted(el: CopyEl) {
    if (el._copyHandler) {
      el.removeEventListener('click', el._copyHandler)
    }
    delete el._copyValue
    delete el._copyHandler
  }
}
