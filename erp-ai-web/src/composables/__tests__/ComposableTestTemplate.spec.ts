/**
 * Composable 测试模板
 *
 * 使用方式：复制本文件 → 改名为 <YourComposable>.test.ts → 替换示例代码
 *
 * 覆盖场景：
 *   - 直接调用模式（无组件上下文依赖的 composable）
 *   - withSetup 模式（需要生命周期/注入的 composable）
 *   - 响应式状态验证（ref / reactive / computed）
 *   - 异步操作（flushPromises / nextTick）
 *   - 边界条件（null / undefined / 空值）
 *   - 生命周期钩子（onMounted / onUnmounted / watch）
 *   - Mock 模式（API / Pinia / Router / Timer）
 */

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { ref, reactive, computed, watch, onMounted, onUnmounted } from 'vue'
import { flushPromises } from '@vue/test-utils'
import { createApp, type App } from 'vue'

// ============================================================
// withSetup 工具函数
// 用于测试依赖组件上下文（生命周期/inject/provide 等）的 composable
// 在实际项目中可提取到 src/test-utils/withSetup.ts 作为公共工具
// ============================================================
function withSetup<T>(composable: () => T): [T, App] {
  let result!: T
  const app = createApp({
    setup() {
      result = composable()
      return () => null
    }
  })
  app.mount(document.createElement('div'))
  return [result, app]
}

// ============================================================
// 示例 composable（仅用于模板演示，实际使用时替换为真实 import）
// ============================================================
function useCounter(initial = 0) {
  const count = ref(initial)
  const doubled = computed(() => count.value * 2)
  function increment() {
    count.value++
  }
  function decrement() {
    count.value--
  }
  function reset() {
    count.value = initial
  }
  return { count, doubled, increment, decrement, reset }
}

// ============================================================
// 模式一：直接调用（无组件上下文依赖）
// 适用：纯逻辑 composable，不依赖 inject / lifecycle / template ref
// 直接调用 composable 函数即可，无需 mount / withSetup
// ============================================================
describe('useCounter — 直接调用模式', () => {
  describe('初始化', () => {
    it('should initialize with default value when no argument given', () => {
      const { count } = useCounter()
      expect(count.value).toBe(0)
    })

    it('should initialize with custom initial value', () => {
      const { count } = useCounter(5)
      expect(count.value).toBe(5)
    })
  })

  describe('响应式状态', () => {
    it('should update ref value on action', () => {
      const { count, increment } = useCounter()
      increment()
      expect(count.value).toBe(1)
    })

    it('should recompute derived state when dependency changes', () => {
      const { doubled, increment } = useCounter(2)
      expect(doubled.value).toBe(4)
      increment()
      expect(doubled.value).toBe(6)
    })
  })

  describe('reset', () => {
    it('should reset state to initial values', () => {
      const { count, increment, reset } = useCounter(10)
      increment()
      increment()
      reset()
      expect(count.value).toBe(10)
    })
  })

  describe('边界条件', () => {
    it('should handle negative initial values', () => {
      const { count, doubled } = useCounter(-3)
      expect(count.value).toBe(-3)
      expect(doubled.value).toBe(-6)
    })

    it('should handle large numbers', () => {
      const { count } = useCounter(Number.MAX_SAFE_INTEGER)
      expect(count.value).toBe(Number.MAX_SAFE_INTEGER)
    })
  })
})

// ============================================================
// 模式二：withSetup（依赖组件上下文）
// 适用：需要 onMounted / onUnmounted / inject / provide 的 composable
// 通过 withSetup 将 composable 挂载到临时 Vue 应用中
// ============================================================
describe('withSetup 模式示范', () => {
  it('should call onMounted during setup', () => {
    const mountedSpy = vi.fn()

    // 模拟一个依赖 onMounted 的 composable
    function useLifecycleDemo() {
      const data = ref<string | null>(null)
      onMounted(() => {
        mountedSpy()
        data.value = 'ready'
      })
      return { data }
    }

    const [result] = withSetup(() => useLifecycleDemo())

    // onMounted 在 withSetup 的 app.mount() 时同步触发
    expect(mountedSpy).toHaveBeenCalledTimes(1)
    expect(result.data.value).toBe('ready')
  })

  it('should call onUnmounted when app unmounts', () => {
    const cleanup = vi.fn()

    function useCleanupDemo() {
      onUnmounted(cleanup)
      return {}
    }

    const [, app] = withSetup(() => useCleanupDemo())
    expect(cleanup).not.toHaveBeenCalled()

    app.unmount()
    expect(cleanup).toHaveBeenCalledTimes(1)
  })

  it('should test composable with reactive dependencies via watch', async () => {
    // 实际项目中替换为真实 composable import：
    // import { useSearchFilter } from '@/composables/useSearchFilter'
    function useSearchFilter() {
      const keyword = ref('')
      const debounced = ref('')
      let timer: ReturnType<typeof setTimeout> | null = null

      watch(keyword, (val) => {
        if (timer) clearTimeout(timer)
        timer = setTimeout(() => {
          debounced.value = val
        }, 100)
      })

      return { keyword, debounced }
    }

    const { keyword, debounced } = useSearchFilter()
    keyword.value = 'hello'

    // watch 回调是同步触发的，但 setTimeout 是异步的
    expect(debounced.value).toBe('')

    await new Promise((r) => setTimeout(r, 150))
    expect(debounced.value).toBe('hello')
  })
})

// ============================================================
// 模式三：异步操作测试
// 适用：涉及 API 调用 / Promise 的 composable
// 核心工具：flushPromises（等待所有微任务完成）
// ============================================================
describe('异步操作测试', () => {
  it('should set loading=true during fetch and false after', async () => {
    function useAsyncFetch() {
      const loading = ref(false)
      const data = ref<string | null>(null)

      async function fetchData(id: string) {
        loading.value = true
        // 模拟 API 调用
        await new Promise((resolve) => setTimeout(resolve, 0))
        data.value = `result-${id}`
        loading.value = false
      }

      return { loading, data, fetchData }
    }

    const { loading, data, fetchData } = useAsyncFetch()

    const promise = fetchData('item-1')
    // loading 在第一个 await 之前已同步设为 true
    expect(loading.value).toBe(true)

    await promise
    expect(loading.value).toBe(false)
    expect(data.value).toBe('result-item-1')
  })

  it('should handle async errors and set error state', async () => {
    function useAsyncWithError() {
      const error = ref<Error | null>(null)

      async function riskyOp() {
        try {
          await Promise.reject(new Error('Network failure'))
        } catch (e) {
          error.value = e instanceof Error ? e : new Error(String(e))
        }
      }

      return { error, riskyOp }
    }

    const { error, riskyOp } = useAsyncWithError()
    await riskyOp()
    expect(error.value).toBeInstanceOf(Error)
    expect(error.value!.message).toBe('Network failure')
  })

  it('should resolve multiple promises with flushPromises', async () => {
    const resolved: string[] = []
    Promise.resolve().then(() => resolved.push('a'))
    Promise.resolve().then(() => resolved.push('b'))
    Promise.resolve().then(() => resolved.push('c'))

    await flushPromises()
    expect(resolved).toEqual(['a', 'b', 'c'])
  })
})

// ============================================================
// 模式四：Mock 模式
// 适用：隔离外部依赖（API 模块 / Pinia store / Vue Router）
// 规则：vi.mock 调用必须放在文件顶层（会被 hoist），或使用 vi.hoisted
// ============================================================
describe('Mock 模式', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  // --- API Mock ---
  describe('API 模块 Mock', () => {
    it('should mock resolved API call', async () => {
      // 实际用法（放在文件顶层）：
      // vi.mock('@/api/modules/user', () => ({
      //   getUserInfo: vi.fn().mockResolvedValue({ id: 1, name: 'Test' })
      // }))

      const mockApi = vi.fn().mockResolvedValue({ id: 1, name: 'Test' })
      const result = await mockApi()
      expect(result).toEqual({ id: 1, name: 'Test' })
      expect(mockApi).toHaveBeenCalledTimes(1)
    })

    it('should mock rejected API call', async () => {
      const mockApi = vi.fn().mockRejectedValue(new Error('401 Unauthorized'))
      await expect(mockApi()).rejects.toThrow('401 Unauthorized')
    })
  })

  // --- Pinia Store Mock ---
  describe('Pinia Store Mock', () => {
    it('should mock store state and actions', () => {
      // 实际用法（放在文件顶层）：
      // vi.mock('@/stores/modules/user', () => ({
      //   useUserStore: vi.fn(() => ({
      //     userInfo: { id: 1, name: 'Test' },
      //     roles: ['admin'],
      //     permissions: ['user:read'],
      //     isLoggedIn: computed(() => true)
      //   }))
      // }))

      const mockStore = {
        userInfo: { id: 1, name: 'Test' },
        roles: ['admin'],
        permissions: ['user:read'],
        isLoggedIn: true
      }
      expect(mockStore.isLoggedIn).toBe(true)
      expect(mockStore.roles).toContain('admin')
    })
  })

  // --- Router Mock ---
  describe('Router Mock', () => {
    it('should mock useRouter push/replace', () => {
      // 实际用法（放在文件顶层）：
      // vi.mock('vue-router', () => ({
      //   useRouter: () => ({ push: vi.fn(), replace: vi.fn() }),
      //   useRoute: () => ({ params: { id: '123' }, query: {} })
      // }))

      const mockPush = vi.fn()
      const mockRoute = { params: { id: '123' }, query: {} }

      expect(mockRoute.params.id).toBe('123')
      mockPush('/home')
      expect(mockPush).toHaveBeenCalledWith('/home')
    })
  })

  // --- Fake Timers ---
  describe('Fake Timers', () => {
    beforeEach(() => {
      vi.useFakeTimers()
    })

    afterEach(() => {
      vi.useRealTimers()
    })

    it('should advance time and trigger setTimeout', () => {
      const callback = vi.fn()
      setTimeout(callback, 1000)
      expect(callback).not.toHaveBeenCalled()
      vi.advanceTimersByTime(1000)
      expect(callback).toHaveBeenCalledTimes(1)
    })

    it('should run all pending timers', () => {
      const callback = vi.fn()
      setTimeout(callback, 500)
      setTimeout(callback, 1000)
      vi.runAllTimers()
      expect(callback).toHaveBeenCalledTimes(2)
    })
  })
})

// ============================================================
// 模式五：边界条件与异常路径
// ============================================================
describe('边界条件覆盖', () => {
  describe('null / undefined 输入', () => {
    it('should handle null input with fallback', () => {
      function useOrDefault(input: string | null) {
        const value = ref(input)
        const display = computed(() => value.value ?? 'default')
        return { value, display }
      }

      const { display } = useOrDefault(null)
      expect(display.value).toBe('default')
    })

    it('should handle undefined list input', () => {
      function useList(input?: number[]) {
        const items = ref(input ?? [])
        const count = computed(() => items.value.length)
        return { items, count }
      }

      const { items, count } = useList(undefined)
      expect(items.value).toEqual([])
      expect(count.value).toBe(0)
    })
  })

  describe('空集合', () => {
    it('should handle empty array', () => {
      function useListInfo(list: string[]) {
        const items = ref(list)
        const isEmpty = computed(() => items.value.length === 0)
        const first = computed(() => items.value[0] ?? null)
        return { items, isEmpty, first }
      }

      const { isEmpty, first } = useListInfo([])
      expect(isEmpty.value).toBe(true)
      expect(first.value).toBeNull()
    })
  })

  describe('响应式对象深层变更', () => {
    it('should reflect deep reactive changes', () => {
      const state = reactive({ user: { name: 'Alice', age: 30 } })
      state.user.age = 31
      expect(state.user.age).toBe(31)
    })

    it('should trigger computed on reactive dependency change', () => {
      const items = reactive([
        { id: 1, done: false },
        { id: 2, done: false }
      ])
      const doneCount = computed(() => items.filter((i) => i.done).length)

      expect(doneCount.value).toBe(0)
      items[0].done = true
      expect(doneCount.value).toBe(1)
    })
  })
})
