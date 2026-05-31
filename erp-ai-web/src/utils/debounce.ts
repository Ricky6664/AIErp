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
