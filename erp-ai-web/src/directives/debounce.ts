import type { Directive, DirectiveBinding } from 'vue'

type DebounceCallback = (...args: unknown[]) => void

interface DebounceEl extends HTMLElement {
  _debounceHandler?: (event: Event) => void
  _debounceTimer?: ReturnType<typeof setTimeout>
  _debounceEvent?: string
}

/**
 * v-debounce 自定义指令
 * 用法：v-debounce="handleClick" 或 v-debounce:500="handleClick"（自定义延迟）
 */
export const debounceDirective: Directive<DebounceEl, DebounceCallback> = {
  mounted(el: DebounceEl, binding: DirectiveBinding<DebounceCallback>) {
    const delay = parseDelay(binding.arg)
    const eventType = 'click'
    el._debounceEvent = eventType

    el._debounceHandler = createDebounceHandler(el, binding.value, delay)

    el.addEventListener(eventType, el._debounceHandler)
  },

  updated(el: DebounceEl, binding: DirectiveBinding<DebounceCallback>) {
    const valueChanged = binding.value !== binding.oldValue
    const argChanged = binding.arg !== binding.oldArg

    if (valueChanged || argChanged) {
      const delay = parseDelay(binding.arg)

      if (el._debounceEvent && el._debounceHandler) {
        el.removeEventListener(el._debounceEvent, el._debounceHandler)
      }

      el._debounceHandler = createDebounceHandler(el, binding.value, delay)

      if (el._debounceEvent) {
        el.addEventListener(el._debounceEvent, el._debounceHandler)
      }
    }
  },

  unmounted(el: DebounceEl) {
    if (el._debounceTimer) {
      clearTimeout(el._debounceTimer)
      el._debounceTimer = undefined
    }
    if (el._debounceEvent && el._debounceHandler) {
      el.removeEventListener(el._debounceEvent, el._debounceHandler)
    }
    delete el._debounceHandler
    delete el._debounceTimer
    delete el._debounceEvent
  }
}

function parseDelay(arg: string | undefined): number {
  if (!arg) return 300
  const parsed = parseInt(arg, 10)
  return isNaN(parsed) ? 300 : parsed
}

function createDebounceHandler(
  el: DebounceEl,
  callback: DebounceCallback | undefined,
  delay: number
): (event: Event) => void {
  return (_event: Event) => {
    if (el._debounceTimer) {
      clearTimeout(el._debounceTimer)
    }
    el._debounceTimer = setTimeout(() => {
      callback?.()
    }, delay)
  }
}
