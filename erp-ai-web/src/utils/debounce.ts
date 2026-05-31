import { ref, onUnmounted, type Ref } from 'vue'

interface DebounceOptions {
  leading?: boolean
  trailing?: boolean
}

interface DebouncedFunction<T extends (...args: any[]) => any> {
  (...args: Parameters<T>): void
  cancel: () => void
  flush: () => void
}

export function debounce<T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300,
  options: DebounceOptions = {}
): DebouncedFunction<T> {
  if (typeof fn !== 'function') {
    throw new TypeError('debounce: fn must be a function')
  }

  const { leading = false, trailing = true } = options
  let timer: ReturnType<typeof setTimeout> | null = null
  let pendingCall: (() => void) | null = null

  function invoke() {
    timer = null
    const call = pendingCall
    pendingCall = null
    if (call) call()
  }

  function debounced(this: any, ...args: Parameters<T>): void {
    pendingCall = () => {
      fn.apply(this, args)
    }

    const hadTimer = timer !== null

    if (timer !== null) {
      clearTimeout(timer)
      timer = null
    }

    if (leading && !hadTimer) {
      invoke()
    }

    if (trailing) {
      timer = setTimeout(() => {
        if (pendingCall !== null) {
          invoke()
        }
      }, delay)
    }
  }

  debounced.cancel = () => {
    if (timer !== null) {
      clearTimeout(timer)
      timer = null
    }
    pendingCall = null
  }

  debounced.flush = () => {
    if (timer !== null && pendingCall !== null) {
      invoke()
      debounced.cancel()
    }
  }

  return debounced
}

export function throttle<T extends (...args: any[]) => any>(
  fn: T,
  interval: number = 300,
  options: DebounceOptions = {}
): DebouncedFunction<T> {
  if (typeof fn !== 'function') {
    throw new TypeError('throttle: fn must be a function')
  }

  const { leading = true, trailing = true } = options
  let timer: ReturnType<typeof setTimeout> | null = null
  let lastExecTime: number = 0
  let pendingCall: (() => void) | null = null

  function invoke() {
    lastExecTime = Date.now()
    const call = pendingCall
    pendingCall = null
    if (call) call()
  }

  function throttled(this: any, ...args: Parameters<T>): void {
    const now = Date.now()

    pendingCall = () => {
      fn.apply(this, args)
    }

    if (timer !== null) {
      clearTimeout(timer)
      timer = null
    }

    if (lastExecTime === 0 && !leading) {
      lastExecTime = now
    }

    const elapsed = now - lastExecTime

    if (elapsed >= interval) {
      invoke()
      return
    }

    if (trailing) {
      timer = setTimeout(() => {
        if (pendingCall !== null) {
          invoke()
          timer = null
        }
      }, interval - elapsed)
    }
  }

  throttled.cancel = () => {
    if (timer !== null) {
      clearTimeout(timer)
      timer = null
    }
    pendingCall = null
  }

  throttled.flush = () => {
    if (timer !== null && pendingCall !== null) {
      invoke()
      throttled.cancel()
    }
  }

  return throttled
}

export function once<T extends (...args: any[]) => any>(fn: T): T {
  if (typeof fn !== 'function') {
    throw new TypeError('once: fn must be a function')
  }

  let called = false
  let result: ReturnType<T>
  let error: unknown

  function wrapper(this: any, ...args: Parameters<T>): ReturnType<T> {
    if (!called) {
      called = true
      try {
        result = fn.apply(this, args)
        return result
      } catch (e) {
        called = false
        error = e
        throw e
      }
    }
    if (error) {
      called = false
      error = undefined
      try {
        result = fn.apply(this, args)
        called = true
        return result
      } catch (e) {
        called = false
        error = e
        throw e
      }
    }
    return result
  }

  return wrapper as T
}

export function beforeAfter<T extends (...args: any[]) => any>(
  fn: T,
  hooks: { before?: (...args: Parameters<T>) => void; after?: (result: ReturnType<T>) => void }
): T {
  if (typeof fn !== 'function') {
    throw new TypeError('beforeAfter: fn must be a function')
  }

  function wrapper(this: any, ...args: Parameters<T>): ReturnType<T> {
    if (hooks.before) {
      hooks.before.apply(this, args)
    }
    const result = fn.apply(this, args) as ReturnType<T>
    if (hooks.after) {
      hooks.after(result)
    }
    return result
  }

  return wrapper as T
}

export function withCount<T extends (...args: any[]) => any>(fn: T): { fn: T; count: Ref<number> } {
  if (typeof fn !== 'function') {
    throw new TypeError('withCount: fn must be a function')
  }

  const count = ref(0)

  function wrapper(this: any, ...args: Parameters<T>): ReturnType<T> {
    count.value++
    return fn.apply(this, args) as ReturnType<T>
  }

  return { fn: wrapper as T, count }
}

export function useDebounce<T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
): { run: (...args: Parameters<T>) => void; cancel: () => void; isPending: Ref<boolean> } {
  const isPending = ref(false)
  const debounced = debounce(
    ((...args: Parameters<T>) => {
      isPending.value = false
      fn(...args)
    }) as T,
    delay,
    { leading: false, trailing: true }
  )

  function run(...args: Parameters<T>): void {
    isPending.value = true
    debounced(...args)
  }

  function cancel(): void {
    isPending.value = false
    debounced.cancel()
  }

  onUnmounted(() => {
    cancel()
  })

  return { run, cancel, isPending }
}

export function useThrottle<T extends (...args: any[]) => any>(
  fn: T,
  interval: number = 300
): { run: (...args: Parameters<T>) => void; cancel: () => void; isThrottled: Ref<boolean> } {
  const isThrottled = ref(false)
  const throttled = throttle(
    ((...args: Parameters<T>) => {
      isThrottled.value = false
      fn(...args)
    }) as T,
    interval,
    { leading: true, trailing: false }
  )

  function run(...args: Parameters<T>): void {
    isThrottled.value = true
    throttled(...args)
  }

  function cancel(): void {
    isThrottled.value = false
    throttled.cancel()
  }

  onUnmounted(() => {
    cancel()
  })

  return { run, cancel, isThrottled }
}
