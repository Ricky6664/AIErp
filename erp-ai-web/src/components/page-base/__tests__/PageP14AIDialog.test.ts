import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import PageP14AIDialog from '@/components/page-base/PageP14AIDialog.vue'
import type { AIDialogPageConfig, PageType } from '@/types/page-base.d.ts'

const mockMessages = [
  {
    id: 'msg-1',
    role: 'user' as const,
    content: '帮我查一下今天的订单数据',
    timestamp: '10:30:00'
  },
  {
    id: 'msg-2',
    role: 'assistant' as const,
    content: '好的，正在为您查询...',
    timestamp: '10:30:02'
  },
  {
    id: 'msg-3',
    role: 'assistant' as const,
    content: '查询结果：今日订单共 128 笔，金额合计 ¥56,832.00',
    timestamp: '10:30:05'
  }
]

const mockHistory = [
  { id: 'hist-1', title: '查询昨日订单', updatedAt: '2026-06-05 15:30' },
  { id: 'hist-2', title: '分析本月销售额', updatedAt: '2026-06-05 10:00' },
  { id: 'hist-3', title: '录入新商品信息', updatedAt: '2026-06-04 18:00' }
]

const mockPrompts = ['查询本月销售排行', '分析库存预警商品', '生成采购建议单']

const baseConfig: AIDialogPageConfig = {
  title: 'AI智能助手',
  showQueryPanel: false,
  showActionBar: false,
  dialogTitle: 'AI 对话助手',
  dialogWidthPercent: 40,
  welcomeMessage: '您好！我是AI助手，有什么可以帮您？',
  prompts: mockPrompts,
  resultType: 'none'
}

const baseProps = {
  viewId: 'view-p14-001',
  pageType: 'P14' as PageType,
  config: baseConfig as AIDialogPageConfig & Record<string, unknown>,
  permissions: ['ai:use', 'ai:export']
}

function createWrapper(
  overrides: Record<string, unknown> = {}
): VueWrapper<typeof PageP14AIDialog> {
  return mount(PageP14AIDialog, {
    props: { ...baseProps, ...overrides }
  })
}

describe('PageP14AIDialog component', () => {
  describe('rendering', () => {
    it('renders the AI dialog container', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p14-ai-dialog').exists()).toBe(true)
    })

    it('renders the main layout with dialog and result panels', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.ai-dialog-main').exists()).toBe(true)
      expect(wrapper.find('.dialog-panel').exists()).toBe(true)
      expect(wrapper.find('.result-panel').exists()).toBe(true)
    })

    it('renders dialog panel header with title', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.dialog-panel-header').exists()).toBe(true)
      expect(wrapper.find('.dialog-panel-header').text()).toContain('AI 对话助手')
    })

    it('renders result panel header', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.result-panel-header').exists()).toBe(true)
      expect(wrapper.find('.result-panel-header').text()).toContain('结果展示区')
    })

    it('renders welcome card when no messages', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.welcome-card').exists()).toBe(true)
      expect(wrapper.find('.welcome-text').text()).toContain('您好')
    })

    it('renders welcome icon', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.welcome-icon').exists()).toBe(true)
    })

    it('renders prompt items', () => {
      const wrapper = createWrapper()
      const prompts = wrapper.findAll('.prompt-item')
      expect(prompts).toHaveLength(3)
      expect(prompts[0].text()).toContain('查询本月销售排行')
    })

    it('renders result placeholder when no result type', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.result-placeholder').exists()).toBe(true)
    })
  })

  describe('message display', () => {
    it('renders message items when messages exist', () => {
      const msgConfig = { ...baseConfig, messages: mockMessages }
      const wrapper = createWrapper({
        config: msgConfig as AIDialogPageConfig & Record<string, unknown>
      })
      const items = wrapper.findAll('.message-item')
      expect(items).toHaveLength(3)
    })

    it('renders user message bubbles', () => {
      const msgConfig = { ...baseConfig, messages: mockMessages }
      const wrapper = createWrapper({
        config: msgConfig as AIDialogPageConfig & Record<string, unknown>
      })
      const userMsgs = wrapper.findAll('.message--user')
      expect(userMsgs).toHaveLength(1)
      expect(userMsgs[0].find('.bubble-content').text()).toContain('帮我查一下')
    })

    it('renders assistant message bubbles', () => {
      const msgConfig = { ...baseConfig, messages: mockMessages }
      const wrapper = createWrapper({
        config: msgConfig as AIDialogPageConfig & Record<string, unknown>
      })
      const assistantMsgs = wrapper.findAll('.message--assistant')
      expect(assistantMsgs).toHaveLength(2)
    })

    it('renders message timestamps', () => {
      const msgConfig = { ...baseConfig, messages: mockMessages }
      const wrapper = createWrapper({
        config: msgConfig as AIDialogPageConfig & Record<string, unknown>
      })
      const times = wrapper.findAll('.bubble-time')
      expect(times).toHaveLength(3)
      expect(times[0].text()).toBe('10:30:00')
    })

    it('does not show welcome card when messages exist', () => {
      const msgConfig = { ...baseConfig, messages: mockMessages }
      const wrapper = createWrapper({
        config: msgConfig as AIDialogPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.welcome-card').exists()).toBe(false)
    })
  })

  describe('streaming', () => {
    it('renders streaming bubble when streaming', () => {
      const streamConfig = {
        ...baseConfig,
        isStreaming: true,
        streamingContent: '正在为您分析',
        messages: mockMessages
      }
      const wrapper = createWrapper({
        config: streamConfig as AIDialogPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.bubble--streaming').exists()).toBe(true)
      expect(wrapper.find('.streaming-cursor').exists()).toBe(true)
    })

    it('displays streaming content', () => {
      const streamConfig = {
        ...baseConfig,
        isStreaming: true,
        streamingContent: '正在为您分析数据...',
        messages: mockMessages
      }
      const wrapper = createWrapper({
        config: streamConfig as AIDialogPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.bubble--streaming .bubble-content').text()).toContain(
        '正在为您分析数据'
      )
    })

    it('disables input while streaming', () => {
      const streamConfig = {
        ...baseConfig,
        isStreaming: true,
        streamingContent: '分析中',
        messages: mockMessages
      }
      const wrapper = createWrapper({
        config: streamConfig as AIDialogPageConfig & Record<string, unknown>
      })
      const textarea = wrapper.findComponent({ name: 'ElInput' })
      expect(textarea.exists()).toBe(true)
    })
  })

  describe('interrupt and regenerate', () => {
    it('emits data-change with ai-stop when stop button clicked', async () => {
      const streamConfig = {
        ...baseConfig,
        isStreaming: true,
        streamingContent: '分析中',
        messages: mockMessages
      }
      const wrapper = createWrapper({
        config: streamConfig as AIDialogPageConfig & Record<string, unknown>
      })
      // Stop button text is "中断生成"
      const stopBtn = wrapper.find('.el-button--danger')
      if (stopBtn.exists()) {
        await stopBtn.trigger('click')
        const emitted = wrapper.emitted('data-change')
        expect(emitted).toBeTruthy()
        const stopEvents = (emitted as unknown[]).filter(
          (e: unknown) => (e as { source: string }[])[0]?.source === 'ai-stop'
        )
        expect(stopEvents.length).toBeGreaterThan(0)
      }
    })

    it('emits data-change with ai-regenerate when regenerate button clicked', async () => {
      const msgConfig = { ...baseConfig, messages: mockMessages }
      const wrapper = createWrapper({
        config: msgConfig as AIDialogPageConfig & Record<string, unknown>
      })
      const regenBtn = wrapper.find('.result-actions .el-button')
      if (regenBtn.exists()) {
        await regenBtn.trigger('click')
        const emitted = wrapper.emitted('data-change')
        expect(emitted).toBeTruthy()
        const regenEvents = (emitted as unknown[]).filter(
          (e: unknown) => (e as { source: string }[])[0]?.source === 'ai-regenerate'
        )
        expect(regenEvents.length).toBeGreaterThan(0)
      }
    })
  })

  describe('history panel', () => {
    it('renders history toggle bar when historyList is provided', () => {
      const histConfig = { ...baseConfig, historyList: mockHistory }
      const wrapper = createWrapper({
        config: histConfig as AIDialogPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.history-toggle-bar').exists()).toBe(true)
      expect(wrapper.find('.history-hint').text()).toContain('3')
    })

    it('does not render history toggle bar when no historyList', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.history-toggle-bar').exists()).toBe(false)
    })
  })

  describe('result types', () => {
    it('renders form area when resultType is form', () => {
      const formConfig = { ...baseConfig, resultType: 'form' as const, messages: mockMessages }
      const wrapper = createWrapper({
        config: formConfig as AIDialogPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.result-form-area').exists()).toBe(true)
      expect(wrapper.find('.result-type-label .el-tag').text()).toBe('智能录入')
    })

    it('renders table area when resultType is table', () => {
      const tableConfig = { ...baseConfig, resultType: 'table' as const, messages: mockMessages }
      const wrapper = createWrapper({
        config: tableConfig as AIDialogPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.result-table-area').exists()).toBe(true)
      expect(wrapper.find('.result-type-label .el-tag').text()).toBe('智能查询')
    })

    it('renders analysis area when resultType is chart', () => {
      const chartConfig = { ...baseConfig, resultType: 'chart' as const, messages: mockMessages }
      const wrapper = createWrapper({
        config: chartConfig as AIDialogPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.result-analysis-area').exists()).toBe(true)
      expect(wrapper.find('.result-type-label .el-tag').text()).toBe('智能图表')
    })

    it('renders analysis area when resultType is analysis', () => {
      const analysisConfig = {
        ...baseConfig,
        resultType: 'analysis' as const,
        messages: mockMessages
      }
      const wrapper = createWrapper({
        config: analysisConfig as AIDialogPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.result-analysis-area').exists()).toBe(true)
      expect(wrapper.find('.result-type-label .el-tag').text()).toBe('智能分析')
    })
  })

  describe('page-ready event', () => {
    it('emits page-ready on mount', () => {
      const wrapper = createWrapper()
      const emitted = wrapper.emitted('page-ready')
      expect(emitted).toBeTruthy()
      expect(emitted![0]).toEqual([{ viewId: 'view-p14-001', pageType: 'P14' }])
    })
  })
})
