import { describe, it, expect, vi, afterEach } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import PageP11Screen from '@/components/page-base/PageP11Screen.vue'
import type { ScreenPageConfig, PageType } from '@/types/page-base.d.ts'

const mockKpiCards = [
  { id: 'kpi-1', label: '销售额', value: 1258000, unit: '元', color: 'blue' as const, trend: 12.5 },
  { id: 'kpi-2', label: '订单数', value: 3658, unit: '笔', color: 'green' as const, trend: 8.3 },
  { id: 'kpi-3', label: '客户数', value: 1842, unit: '个', color: 'orange' as const, trend: -3.2 },
  { id: 'kpi-4', label: '利润率', value: 23.6, unit: '%', color: 'purple' as const, trend: 1.8 }
]

const mockCharts = [
  { id: 'chart-1', title: '销售趋势', type: 'line' as const, colSpan: 6, rowSpan: 1 },
  { id: 'chart-2', title: '区域分布', type: 'bar' as const, colSpan: 6, rowSpan: 1 },
  { id: 'chart-3', title: '品类占比', type: 'pie' as const, colSpan: 4, rowSpan: 1 },
  {
    id: 'chart-4',
    title: '完成率',
    type: 'gauge' as const,
    colSpan: 4,
    rowSpan: 1,
    subtitle: '目标 100%'
  },
  { id: 'chart-5', title: '全国热力图', type: 'map' as const, colSpan: 4, rowSpan: 1 }
]

const mockConfig: ScreenPageConfig = {
  title: '经营数据大屏',
  darkTheme: true,
  showFullscreenBtn: true,
  refreshInterval: 60,
  gridCols: 12,
  kpiCards: mockKpiCards,
  charts: mockCharts,
  showKpiArea: true
}

const baseProps = {
  viewId: 'view-screen-001',
  pageType: 'P11' as PageType,
  config: mockConfig as ScreenPageConfig & Record<string, unknown>,
  permissions: ['screen:view', 'screen:export']
}

function createWrapper(overrides: Record<string, unknown> = {}): VueWrapper<typeof PageP11Screen> {
  return mount(PageP11Screen, {
    props: { ...baseProps, ...overrides }
  })
}

describe('PageP11Screen component', () => {
  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('rendering', () => {
    it('renders the screen container', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p11-screen').exists()).toBe(true)
    })

    it('renders dark theme by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.theme-dark').exists()).toBe(true)
    })

    it('renders light theme when darkTheme is false', () => {
      const wrapper = createWrapper({ config: { ...mockConfig, darkTheme: false } })
      expect(wrapper.find('.theme-light').exists()).toBe(true)
      expect(wrapper.find('.theme-dark').exists()).toBe(false)
    })

    it('renders page title when dark theme', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.screen-title').exists()).toBe(true)
      expect(wrapper.find('.screen-title').text()).toBe('经营数据大屏')
    })

    it('does not render page title when light theme', () => {
      const wrapper = createWrapper({ config: { ...mockConfig, darkTheme: false } })
      expect(wrapper.find('.screen-title').exists()).toBe(false)
    })

    it('renders KPI card area when showKpiArea is true', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.kpi-area').exists()).toBe(true)
      expect(wrapper.findAll('.kpi-card')).toHaveLength(4)
    })

    it('hides KPI area when showKpiArea is false', () => {
      const wrapper = createWrapper({ config: { ...mockConfig, showKpiArea: false } })
      expect(wrapper.find('.kpi-area').exists()).toBe(false)
    })

    it('renders chart grid area', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.chart-grid-area').exists()).toBe(true)
    })

    it('renders chart panels from config', () => {
      const wrapper = createWrapper()
      expect(wrapper.findAll('.chart-panel')).toHaveLength(5)
    })

    it('renders chart panel titles', () => {
      const wrapper = createWrapper()
      const titles = wrapper.findAll('.chart-panel-title')
      expect(titles[0].text()).toBe('销售趋势')
      expect(titles[3].text()).toBe('完成率')
    })

    it('renders chart type tags', () => {
      const wrapper = createWrapper()
      const tags = wrapper.findAll('.chart-type-tag')
      expect(tags).toHaveLength(5)
      expect(tags[0].text()).toBe('折线图')
      expect(tags[2].text()).toBe('饼图')
    })

    it('renders chart subtitle when provided', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.chart-panel-subtitle').exists()).toBe(true)
      expect(wrapper.find('.chart-panel-subtitle').text()).toBe('目标 100%')
    })

    it('renders placeholder when no charts configured', () => {
      const wrapper = createWrapper({ config: { darkTheme: true, charts: [] } })
      expect(wrapper.find('.chart-empty').exists()).toBe(true)
    })

    it('applies grid column span to chart panel', () => {
      const wrapper = createWrapper()
      const firstChart = wrapper.findAll('.chart-panel')[0]
      expect(firstChart.attributes('style')).toContain('span 6')
    })

    it('renders KPI card values', () => {
      const wrapper = createWrapper()
      const cards = wrapper.findAll('.kpi-card')
      expect(cards[0].text()).toContain('销售额')
      expect(cards[0].text()).toContain('元')
    })

    it('renders KPI trend indicators', () => {
      const wrapper = createWrapper()
      const cards = wrapper.findAll('.kpi-card')
      expect(cards[0].find('.trend-up').exists()).toBe(true)
      expect(cards[2].find('.trend-down').exists()).toBe(true)
    })

    it('renders refresh timer text when interval is set', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.refresh-timer').exists()).toBe(true)
      expect(wrapper.find('.refresh-timer').text()).toContain('60')
    })

    it('does not render refresh timer when interval is 0', () => {
      const wrapper = createWrapper({ config: { ...mockConfig, refreshInterval: 0 } })
      expect(wrapper.find('.refresh-timer').exists()).toBe(false)
    })
  })

  describe('theme', () => {
    it('applies dark theme styles when darkTheme is true', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.theme-dark').exists()).toBe(true)
    })

    it('applies light theme styles when darkTheme is false', () => {
      const wrapper = createWrapper({ config: { ...mockConfig, darkTheme: false } })
      expect(wrapper.find('.theme-light').exists()).toBe(true)
    })

    it('defaults to dark theme when config has no darkTheme property', () => {
      const wrapper = createWrapper({ config: { charts: [] } })
      expect(wrapper.find('.theme-dark').exists()).toBe(true)
    })
  })

  describe('fullscreen', () => {
    it('renders fullscreen button by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.fullscreen-btn').exists()).toBe(true)
    })

    it('hides fullscreen button when showFullscreenBtn is false', () => {
      const wrapper = createWrapper({ config: { ...mockConfig, showFullscreenBtn: false } })
      expect(wrapper.find('.fullscreen-btn').exists()).toBe(false)
    })

    it('emits data-change on fullscreen toggle', async () => {
      const wrapper = createWrapper()
      const mockRequestFs = vi.fn()
      Object.defineProperty(document, 'fullscreenElement', {
        value: null,
        writable: true,
        configurable: true
      })
      Object.defineProperty(document, 'exitFullscreen', {
        value: vi.fn(),
        configurable: true
      })

      const btn = wrapper.find('.fullscreen-btn')
      const el = wrapper.find('.page-p11-screen').element
      el.requestFullscreen = mockRequestFs

      await btn.trigger('click')
      const emitted = wrapper.emitted('data-change') as Array<
        Array<{ source: string; data: unknown }>
      >
      const fullscreenEvents = emitted?.filter((e) => e[0].source === 'screen-fullscreen-toggle')
      expect(fullscreenEvents).toBeTruthy()
      expect(fullscreenEvents[0][0].data).toEqual({ fullscreen: true })
    })
  })

  describe('props', () => {
    it('accepts viewId prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('viewId')).toBe('view-screen-001')
    })

    it('accepts pageType prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('pageType')).toBe('P11')
    })

    it('accepts config prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('config')).toEqual(mockConfig)
    })

    it('accepts permissions prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('permissions')).toEqual(['screen:view', 'screen:export'])
    })

    it('handles empty config gracefully', () => {
      const wrapper = createWrapper({ config: {} })
      expect(wrapper.find('.page-p11-screen').exists()).toBe(true)
      expect(wrapper.find('.chart-grid-area').exists()).toBe(true)
    })

    it('handles config without charts gracefully', () => {
      const wrapper = createWrapper({ config: { darkTheme: true } })
      expect(wrapper.find('.chart-empty').exists()).toBe(true)
    })
  })

  describe('events', () => {
    it('emits page-ready on mount with viewId and pageType', () => {
      const wrapper = createWrapper()
      const emitted = wrapper.emitted('page-ready') as Array<
        Array<{ viewId: string; pageType: string }>
      >
      expect(emitted).toBeTruthy()
      expect(emitted[0][0]).toEqual({
        viewId: 'view-screen-001',
        pageType: 'P11'
      })
    })

    it('emits screen-auto-refresh data-change when refresh timer fires', async () => {
      vi.useFakeTimers()
      const wrapper = createWrapper({ config: { ...mockConfig, refreshInterval: 5 } })
      vi.advanceTimersByTime(5000)
      const emitted = wrapper.emitted('data-change') as Array<
        Array<{ source: string; data: unknown }>
      >
      const refreshEvents = emitted?.filter((e) => e[0].source === 'screen-auto-refresh')
      expect(refreshEvents).toBeTruthy()
      expect(refreshEvents.length).toBeGreaterThanOrEqual(1)
      expect(refreshEvents[0][0].data).toHaveProperty('timestamp')
      vi.useRealTimers()
      wrapper.unmount()
    })
  })

  describe('slots', () => {
    it('renders main-content slot content', () => {
      const wrapper = mount(PageP11Screen, {
        props: baseProps,
        slots: {
          'main-content': '<div class="custom-main">自定义主内容</div>'
        }
      })
      expect(wrapper.find('.custom-main').exists()).toBe(true)
      expect(wrapper.find('.custom-main').text()).toBe('自定义主内容')
    })

    it('renders extra-area slot content', () => {
      const wrapper = mount(PageP11Screen, {
        props: baseProps,
        slots: {
          'extra-area': '<div class="custom-extra">额外区域</div>'
        }
      })
      expect(wrapper.find('.extra-area').exists()).toBe(true)
      expect(wrapper.find('.custom-extra').text()).toBe('额外区域')
    })

    it('does not render extra-area when slot not provided', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.extra-area').exists()).toBe(false)
    })
  })

  describe('cleanup', () => {
    it('clears refresh timer on unmount', () => {
      vi.useFakeTimers()
      const wrapper = createWrapper({ config: { ...mockConfig, refreshInterval: 5 } })
      wrapper.unmount()
      vi.advanceTimersByTime(10000)
      vi.useRealTimers()
    })
  })

  describe('grid layout', () => {
    it('uses default 12 column grid', () => {
      const wrapper = createWrapper({ config: { darkTheme: true, charts: mockCharts } })
      expect(wrapper.find('.chart-grid').exists()).toBe(true)
    })

    it('applies custom grid columns', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, gridCols: 24 }
      })
      expect(wrapper.find('.chart-grid').exists()).toBe(true)
    })
  })

  describe('chart type labels', () => {
    it('maps line to 折线图', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, charts: [{ id: 'c1', title: '测试', type: 'line' }] }
      })
      expect(wrapper.find('.chart-type-tag').text()).toBe('折线图')
    })

    it('maps gauge to 仪表盘', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, charts: [{ id: 'c1', title: '测试', type: 'gauge' }] }
      })
      expect(wrapper.find('.chart-type-tag').text()).toBe('仪表盘')
    })

    it('maps map to 地图', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, charts: [{ id: 'c1', title: '测试', type: 'map' }] }
      })
      expect(wrapper.find('.chart-type-tag').text()).toBe('地图')
    })
  })
})
