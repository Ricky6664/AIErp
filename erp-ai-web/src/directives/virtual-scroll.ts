import type { Directive, DirectiveBinding, VNode } from 'vue'
import { render } from 'vue'

/** v-virtual-scroll 指令绑定值 */
export interface VirtualScrollOptions<T = unknown> {
  /** 数据源数组 */
  items: T[]
  /** 每行固定高度（px），动态高度场景作为初始估算高度 */
  itemHeight: number
  /** 渲染函数，返回 VNode */
  render: (item: T, index: number) => VNode
  /** 上下各缓冲行数，默认 3 */
  buffer?: number
}

/** 内部状态 */
interface VirtualScrollState {
  startIndex: number
  endIndex: number
  scrollTop: number
  heightCache: Map<number, number>
  visibleCount: number
}

interface VirtualScrollEl extends HTMLElement {
  _vsOptions?: VirtualScrollOptions
  _vsState?: VirtualScrollState
  _vsHandler?: (e: Event) => void
  _vsPhantom?: HTMLDivElement
  _vsContent?: HTMLDivElement
  _vsResizeObserver?: ResizeObserver
  _vsRenderedNodes?: Map<number, HTMLDivElement>
}

function getItemHeight(
  state: VirtualScrollState,
  options: VirtualScrollOptions,
  index: number
): number {
  return state.heightCache.get(index) ?? options.itemHeight
}

function getOffset(
  state: VirtualScrollState,
  options: VirtualScrollOptions,
  index: number
): number {
  let offset = 0
  for (let i = 0; i < index; i++) {
    offset += getItemHeight(state, options, i)
  }
  return offset
}

function getTotalHeight(state: VirtualScrollState, options: VirtualScrollOptions): number {
  let total = 0
  for (let i = 0; i < options.items.length; i++) {
    total += getItemHeight(state, options, i)
  }
  return total
}

function computeVisibleRange(
  state: VirtualScrollState,
  options: VirtualScrollOptions,
  containerHeight: number
): void {
  const buffer = options.buffer ?? 3
  let acc = 0
  let start = 0
  for (let i = 0; i < options.items.length; i++) {
    const h = getItemHeight(state, options, i)
    if (acc + h > state.scrollTop) {
      start = i
      break
    }
    acc += h
  }
  start = Math.max(0, start - buffer)
  let end = start
  acc = getOffset(state, options, start)
  for (let i = start; i < options.items.length; i++) {
    acc += getItemHeight(state, options, i)
    end = i
    if (acc > state.scrollTop + containerHeight) {
      break
    }
  }
  end = Math.min(options.items.length - 1, end + buffer)
  state.startIndex = start
  state.endIndex = end
  state.visibleCount = Math.ceil(containerHeight / options.itemHeight) + buffer * 2
}

function renderContent(el: VirtualScrollEl, options: VirtualScrollOptions): void {
  const state = el._vsState!
  const content = el._vsContent!
  const phantom = el._vsPhantom!
  const renderedNodes = el._vsRenderedNodes!

  const totalHeight = getTotalHeight(state, options)
  phantom.style.height = `${totalHeight}px`

  const nextVisible = new Set<number>()
  for (let i = state.startIndex; i <= state.endIndex; i++) {
    nextVisible.add(i)
  }

  for (const [index, wrapper] of renderedNodes) {
    if (!nextVisible.has(index)) {
      render(null, wrapper)
      content.removeChild(wrapper)
      renderedNodes.delete(index)
    }
  }

  for (let i = state.startIndex; i <= state.endIndex; i++) {
    if (renderedNodes.has(i)) continue
    const item = options.items[i]
    if (!item) continue
    const offset = getOffset(state, options, i)
    const vnode = options.render(item, i)
    const wrapper = document.createElement('div')
    wrapper.style.position = 'absolute'
    wrapper.style.top = '0'
    wrapper.style.left = '0'
    wrapper.style.right = '0'
    wrapper.style.transform = `translateY(${offset}px)`
    content.appendChild(wrapper)
    render(vnode, wrapper)
    renderedNodes.set(i, wrapper)
  }
}

function handleScroll(el: VirtualScrollEl, options: VirtualScrollOptions): void {
  const state = el._vsState!
  const oldStart = state.startIndex
  const oldEnd = state.endIndex
  state.scrollTop = el.scrollTop
  computeVisibleRange(state, options, el.clientHeight)
  if (state.startIndex !== oldStart || state.endIndex !== oldEnd) {
    renderContent(el, options)
  }
}

function measureHeights(el: VirtualScrollEl): void {
  const state = el._vsState!
  const renderedNodes = el._vsRenderedNodes!
  if (!renderedNodes) return
  for (const [index, wrapper] of renderedNodes) {
    const measured = wrapper.getBoundingClientRect().height
    if (measured > 0 && measured !== state.heightCache.get(index)) {
      state.heightCache.set(index, measured)
    }
  }
}

export const vVirtualScroll: Directive<VirtualScrollEl, VirtualScrollOptions> = {
  mounted(el: VirtualScrollEl, binding: DirectiveBinding<VirtualScrollOptions>) {
    const options = binding.value
    el._vsOptions = options
    el._vsRenderedNodes = new Map()

    el.style.position = 'relative'
    el.style.overflowY = 'auto'

    const phantom = document.createElement('div')
    phantom.style.position = 'relative'
    phantom.style.width = '100%'
    el._vsPhantom = phantom

    const content = document.createElement('div')
    content.style.position = 'absolute'
    content.style.top = '0'
    content.style.left = '0'
    content.style.right = '0'
    el._vsContent = content

    el.appendChild(phantom)
    el.appendChild(content)

    const state: VirtualScrollState = {
      startIndex: 0,
      endIndex: 0,
      scrollTop: 0,
      heightCache: new Map(),
      visibleCount: 0
    }
    el._vsState = state

    computeVisibleRange(state, options, el.clientHeight)
    renderContent(el, options)

    const handler = () => handleScroll(el, options)
    el._vsHandler = handler
    el.addEventListener('scroll', handler, { passive: true })

    if (typeof ResizeObserver !== 'undefined') {
      const ro = new ResizeObserver(() => {
        measureHeights(el)
        const totalHeight = getTotalHeight(state, options)
        if (el._vsPhantom) {
          el._vsPhantom.style.height = `${totalHeight}px`
        }
      })
      el._vsResizeObserver = ro
      const c = el._vsContent
      if (c) {
        ro.observe(c)
      }
    }
  },

  updated(el: VirtualScrollEl, binding: DirectiveBinding<VirtualScrollOptions>) {
    const options = binding.value
    const oldOptions = binding.oldValue as VirtualScrollOptions | undefined
    if (!el._vsState) return
    el._vsOptions = options
    if (oldOptions && oldOptions.items !== options.items) {
      el._vsState.heightCache.clear()
      el.scrollTop = 0
      el._vsState.scrollTop = 0
    }
    computeVisibleRange(el._vsState, options, el.clientHeight)
    renderContent(el, options)
  },

  unmounted(el: VirtualScrollEl) {
    if (el._vsHandler) {
      el.removeEventListener('scroll', el._vsHandler)
    }
    if (el._vsResizeObserver) {
      el._vsResizeObserver.disconnect()
    }
    if (el._vsRenderedNodes) {
      for (const [, wrapper] of el._vsRenderedNodes) {
        render(null, wrapper)
      }
      el._vsRenderedNodes.clear()
    }
    if (el._vsContent && el._vsContent.parentNode === el) {
      el.removeChild(el._vsContent)
    }
    if (el._vsPhantom && el._vsPhantom.parentNode === el) {
      el.removeChild(el._vsPhantom)
    }
    delete el._vsOptions
    delete el._vsState
    delete el._vsHandler
    delete el._vsPhantom
    delete el._vsContent
    delete el._vsResizeObserver
    delete el._vsRenderedNodes
  }
}
