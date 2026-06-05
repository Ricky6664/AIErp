import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import PageP12Profile from '@/components/page-base/PageP12Profile.vue'
import type { ProfilePageConfig, PageType } from '@/types/page-base.d.ts'

const mockStats = [
  { id: 'stat-1', label: '本月订单', value: 128, icon: 'Document', color: 'blue' as const },
  { id: 'stat-2', label: '收藏商品', value: 56, icon: 'Star', color: 'orange' as const },
  { id: 'stat-3', label: '浏览记录', value: 302, icon: 'View', color: 'green' as const },
  { id: 'stat-4', label: '未读消息', value: 5, icon: 'Message', color: 'red' as const }
]

const mockTabs = [
  { id: 'tab-info', label: '基本信息', icon: 'User' },
  { id: 'tab-security', label: '安全设置', icon: 'Lock' },
  { id: 'tab-notification', label: '消息通知', icon: 'Bell' }
]

const mockConfig: ProfilePageConfig = {
  title: '个人中心',
  showQueryPanel: false,
  showActionBar: true,
  avatar: '',
  userName: '管理员',
  userRole: '系统管理员',
  description: '这个人很懒，什么都没留下~',
  stats: mockStats,
  tabs: mockTabs,
  activeTab: 'tab-info',
  formMaxWidth: 960
}

const baseProps = {
  viewId: 'view-profile-001',
  pageType: 'P12' as PageType,
  config: mockConfig as ProfilePageConfig & Record<string, unknown>,
  permissions: ['profile:view', 'profile:edit']
}

function createWrapper(overrides: Record<string, unknown> = {}): VueWrapper<typeof PageP12Profile> {
  return mount(PageP12Profile, {
    props: { ...baseProps, ...overrides }
  })
}

describe('PageP12Profile component', () => {
  describe('rendering', () => {
    it('renders the profile container', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p12-profile').exists()).toBe(true)
    })

    it('renders profile header', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.profile-header').exists()).toBe(true)
    })

    it('renders profile header background', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.profile-header-bg').exists()).toBe(true)
    })

    it('renders avatar section', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.profile-avatar-section').exists()).toBe(true)
      expect(wrapper.find('.profile-avatar').exists()).toBe(true)
    })

    it('renders user name', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.profile-name').exists()).toBe(true)
      expect(wrapper.find('.profile-name').text()).toBe('管理员')
    })

    it('renders user role', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.profile-role').exists()).toBe(true)
      expect(wrapper.find('.profile-role').text()).toContain('系统管理员')
    })

    it('renders user description', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.profile-desc').exists()).toBe(true)
      expect(wrapper.find('.profile-desc').text()).toBe('这个人很懒，什么都没留下~')
    })

    it('renders stat cards', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.profile-stats').exists()).toBe(true)
      expect(wrapper.findAll('.profile-stat-card')).toHaveLength(4)
    })

    it('renders stat card values and labels', () => {
      const wrapper = createWrapper()
      const cards = wrapper.findAll('.profile-stat-card')
      expect(cards[0].text()).toContain('128')
      expect(cards[0].text()).toContain('本月订单')
      expect(cards[1].text()).toContain('56')
      expect(cards[1].text()).toContain('收藏商品')
    })

    it('applies stat card color classes', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.stat-card--blue').exists()).toBe(true)
      expect(wrapper.find('.stat-card--orange').exists()).toBe(true)
      expect(wrapper.find('.stat-card--green').exists()).toBe(true)
      expect(wrapper.find('.stat-card--red').exists()).toBe(true)
    })

    it('does not render stats section when stats is empty', () => {
      const wrapper = createWrapper({ config: { ...mockConfig, stats: [] } })
      expect(wrapper.find('.profile-stats').exists()).toBe(false)
    })

    it('does not render stats section when stats is undefined', () => {
      const wrapper = createWrapper({ config: { userName: 'Test' } })
      expect(wrapper.find('.profile-stats').exists()).toBe(false)
    })

    it('renders tab navigation when tabs provided', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.profile-tabs-nav').exists()).toBe(true)
      expect(wrapper.findAll('.profile-tab-item')).toHaveLength(3)
    })

    it('does not render tabs when not provided', () => {
      const wrapper = createWrapper({ config: { userName: 'Test', tabs: [] } })
      expect(wrapper.find('.profile-tabs-nav').exists()).toBe(false)
    })

    it('highlights active tab', () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.profile-tab-item')
      expect(tabs[0].classes()).toContain('is-active')
      expect(tabs[1].classes()).not.toContain('is-active')
    })

    it('renders main content area', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.main-content-area').exists()).toBe(true)
    })

    it('renders form wrapper', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.form-wrapper').exists()).toBe(true)
    })

    it('renders main content placeholder by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.form-wrapper .area-placeholder').exists()).toBe(true)
    })

    it('applies default form max width of 960px', () => {
      const wrapper = createWrapper()
      const formWrapper = wrapper.find('.form-wrapper')
      const style = formWrapper.attributes('style')
      expect(style).toContain('960px')
    })

    it('hides query area when showQueryPanel is false', () => {
      const wrapper = createWrapper({ config: { ...mockConfig, showQueryPanel: false } })
      expect(wrapper.find('.query-area').exists()).toBe(false)
    })

    it('renders query area when showQueryPanel is true', () => {
      const wrapper = createWrapper({ config: { ...mockConfig, showQueryPanel: true } })
      expect(wrapper.find('.query-area').exists()).toBe(true)
    })

    it('renders action area by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.action-area').exists()).toBe(true)
    })

    it('hides action area when showActionBar is false', () => {
      const wrapper = createWrapper({ config: { ...mockConfig, showActionBar: false } })
      expect(wrapper.find('.action-area').exists()).toBe(false)
    })
  })

  describe('props', () => {
    it('accepts viewId prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('viewId')).toBe('view-profile-001')
    })

    it('accepts pageType prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('pageType')).toBe('P12')
    })

    it('accepts config prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('config')).toEqual(mockConfig)
    })

    it('accepts permissions prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('permissions')).toEqual(['profile:view', 'profile:edit'])
    })

    it('handles empty config gracefully', () => {
      const wrapper = createWrapper({ config: {} })
      expect(wrapper.find('.page-p12-profile').exists()).toBe(true)
      expect(wrapper.find('.main-content-area').exists()).toBe(true)
      expect(wrapper.find('.form-wrapper').exists()).toBe(true)
    })

    it('handles config without userName gracefully', () => {
      const wrapper = createWrapper({ config: { userRole: 'Admin' } })
      expect(wrapper.find('.profile-name').exists()).toBe(false)
      expect(wrapper.find('.profile-role').exists()).toBe(true)
    })

    it('handles config without avatar gracefully', () => {
      const wrapper = createWrapper({ config: { userName: 'Test' } })
      expect(wrapper.find('.profile-avatar').exists()).toBe(true)
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
        viewId: 'view-profile-001',
        pageType: 'P12'
      })
    })

    it('emits data-change on tab click', async () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.profile-tab-item')
      await tabs[1].trigger('click')
      const emitted = wrapper.emitted('data-change') as Array<
        Array<{ source: string; data: unknown }>
      >
      const tabEvents = emitted?.filter((e) => e[0].source === 'profile-tab-change')
      expect(tabEvents).toBeTruthy()
      expect(tabEvents[0][0].data).toEqual({ activeTab: 'tab-security' })
    })

    it('updates active tab class on click', async () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.profile-tab-item')
      await tabs[2].trigger('click')
      expect(tabs[2].classes()).toContain('is-active')
      expect(tabs[0].classes()).not.toContain('is-active')
    })
  })

  describe('slots', () => {
    it('renders query-panel slot content', () => {
      const wrapper = mount(PageP12Profile, {
        props: { ...baseProps, config: { ...mockConfig, showQueryPanel: true } },
        slots: {
          'query-panel': '<div class="custom-query">自定义查询区</div>'
        }
      })
      expect(wrapper.find('.custom-query').exists()).toBe(true)
      expect(wrapper.find('.custom-query').text()).toBe('自定义查询区')
    })

    it('renders action-bar slot content', () => {
      const wrapper = mount(PageP12Profile, {
        props: baseProps,
        slots: {
          'action-bar': '<div class="custom-action">编辑资料</div>'
        }
      })
      expect(wrapper.find('.custom-action').exists()).toBe(true)
    })

    it('renders main-content slot content', () => {
      const wrapper = mount(PageP12Profile, {
        props: baseProps,
        slots: {
          'main-content': '<div class="custom-main">基本信息表单</div>'
        }
      })
      expect(wrapper.find('.custom-main').exists()).toBe(true)
      expect(wrapper.find('.custom-main').text()).toBe('基本信息表单')
    })

    it('renders extra-area slot content', () => {
      const wrapper = mount(PageP12Profile, {
        props: baseProps,
        slots: {
          'extra-area': '<div class="custom-extra">额外内容</div>'
        }
      })
      expect(wrapper.find('.extra-area').exists()).toBe(true)
      expect(wrapper.find('.custom-extra').text()).toBe('额外内容')
    })

    it('does not render extra-area when slot not provided', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.extra-area').exists()).toBe(false)
    })
  })

  describe('custom form max width', () => {
    it('applies custom formMaxWidth from config', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, formMaxWidth: 800 }
      })
      const formWrapper = wrapper.find('.form-wrapper')
      const style = formWrapper.attributes('style')
      expect(style).toContain('800px')
    })

    it('accepts string formMaxWidth', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, formMaxWidth: '100%' }
      })
      const formWrapper = wrapper.find('.form-wrapper')
      const style = formWrapper.attributes('style')
      expect(style).toContain('100%')
    })
  })
})
