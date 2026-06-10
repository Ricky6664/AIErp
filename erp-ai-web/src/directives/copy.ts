import type { Directive, DirectiveBinding } from 'vue'
import { ElMessage } from 'element-plus'

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
 * 复制文本到剪贴板
 * 优先使用navigator.clipboard API，降级使用document.execCommand('copy')
 * @param text - 待复制的文本
 * @returns Promise<boolean> 是否复制成功
 */
async function copyToClipboard(text: string): Promise<boolean> {
  try {
    await navigator.clipboard.writeText(text)
    return true
  } catch {
    // 降级方案：创建临时textarea，使用execCommand
    try {
      const textarea = document.createElement('textarea')
      textarea.value = text
      textarea.style.position = 'fixed'
      textarea.style.left = '-9999px'
      document.body.appendChild(textarea)
      textarea.select()
      document.execCommand('copy')
      document.body.removeChild(textarea)
      return true
    } catch {
      return false
    }
  }
}

/**
 * 创建复制点击处理函数
 * @param el - 绑定v-copy指令的DOM元素
 * @returns 点击处理函数
 */
function createCopyHandler(el: CopyEl): () => void {
  return () => {
    const text = el._copyValue ?? ''
    if (!text) {
      ElMessage.warning('复制内容为空')
      return
    }
    copyToClipboard(text).then((success) => {
      if (success) {
        ElMessage.success('复制成功')
      } else {
        ElMessage.error('复制失败')
      }
    })
  }
}

/**
 * v-copy自定义指令定义
 * 点击元素时复制绑定文本到剪贴板
 * 用法：v-copy="orderNo" 点击元素复制orderNo到剪贴板
 */
export const copyDirective: Directive<CopyEl, CopyValue> = {
  mounted(el: CopyEl, binding: DirectiveBinding<CopyValue>) {
    el._copyValue = binding.value

    el._copyHandler = createCopyHandler(el)

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
