import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import ErpFieldRenderer from '@/components/basic-input/ErpFieldRenderer.vue'
import ErpInput from '@/components/basic-input/index.vue'
import ErpTextarea from '@/components/basic-input/ErpTextarea.vue'
import type { ErpFieldRendererProps } from '@/types/basic-input'
import type { FieldLinkageRule } from '@/types/list-table'

interface ErpFieldRendererVM {
  validate: () => Promise<boolean>
  reset: () => void
}

function vm(wrapper: VueWrapper): ErpFieldRendererVM {
  return wrapper.vm as unknown as ErpFieldRendererVM
}

function createWrapper(overrides: Partial<ErpFieldRendererProps> = {}): VueWrapper {
  return mount(ErpFieldRenderer, {
    props: {
      modelValue: '',
      disabled: false,
      placeholder: '请输入',
      ...overrides
    },
    global: {
      stubs: {
        'el-skeleton': true,
        'el-input': true
      }
    }
  })
}

function findInput(wrapper: VueWrapper): VueWrapper {
  return wrapper.findComponent(ErpInput)
}

function findTextarea(wrapper: VueWrapper): VueWrapper {
  return wrapper.findComponent(ErpTextarea)
}

describe('ErpFieldRenderer component', () => {
  describe('rendering', () => {
    it('renders wrapper element', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.erp-field-renderer').exists()).toBe(true)
    })

    it('renders loading skeleton when loading is true', () => {
      const wrapper = createWrapper({ loading: true })
      expect(wrapper.find('.erp-field-renderer__loading').exists()).toBe(true)
    })

    it('does not render loading skeleton when loading is false', () => {
      const wrapper = createWrapper({ loading: false })
      expect(wrapper.find('.erp-field-renderer__loading').exists()).toBe(false)
    })

    it('has loading class when loading is true', () => {
      const wrapper = createWrapper({ loading: true })
      expect(wrapper.find('.erp-field-renderer--loading').exists()).toBe(true)
    })
  })

  describe('props', () => {
    it('accepts modelValue prop', () => {
      const wrapper = createWrapper({ modelValue: 'test-value' })
      expect(wrapper.props('modelValue')).toBe('test-value')
    })

    it('accepts fieldConfig prop', () => {
      const fieldConfig = { field: 'test', fieldType: 'text', title: '测试' }
      const wrapper = createWrapper({ fieldConfig })
      expect(wrapper.props('fieldConfig')).toEqual(fieldConfig)
    })

    it('accepts disabled prop', () => {
      const wrapper = createWrapper({ disabled: true })
      expect(wrapper.props('disabled')).toBe(true)
    })

    it('accepts placeholder prop', () => {
      const wrapper = createWrapper({ placeholder: '自定义占位' })
      expect(wrapper.props('placeholder')).toBe('自定义占位')
    })

    it('accepts loading prop', () => {
      const wrapper = createWrapper({ loading: true })
      expect(wrapper.props('loading')).toBe(true)
    })

    it('accepts size prop', () => {
      const wrapper = createWrapper({ size: 'small' })
      expect(wrapper.props('size')).toBe('small')
    })

    it('has default props', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('disabled')).toBe(false)
      expect(wrapper.props('loading')).toBe(false)
      expect(wrapper.props('placeholder')).toBe('请输入')
      expect(wrapper.props('size')).toBe('default')
    })
  })

  describe('v-model bidirectional binding', () => {
    it('emits update:modelValue when child ErpInput updates', async () => {
      const wrapper = createWrapper({ modelValue: 'initial' })
      const child = findInput(wrapper)
      expect(child.exists()).toBe(true)
      child.vm.$emit('update:modelValue', 'new-value')
      await nextTick()
      expect(wrapper.emitted('update:modelValue')).toBeTruthy()
      expect(wrapper.emitted('update:modelValue')?.[0]).toEqual(['new-value'])
    })

    it('emits update:modelValue when textarea child updates', async () => {
      const wrapper = createWrapper({
        modelValue: 'initial',
        fieldConfig: { field: 'desc', fieldType: 'textarea' }
      })
      const child = findTextarea(wrapper)
      expect(child.exists()).toBe(true)
      child.vm.$emit('update:modelValue', 'multiline-value')
      await nextTick()
      expect(wrapper.emitted('update:modelValue')).toBeTruthy()
      expect(wrapper.emitted('update:modelValue')?.[0]).toEqual(['multiline-value'])
    })
  })

  describe('events', () => {
    it('emits change event when child emits change', async () => {
      const wrapper = createWrapper({ modelValue: 'test' })
      const child = findInput(wrapper)
      child.vm.$emit('change', 'updated')
      await nextTick()
      expect(wrapper.emitted('change')).toBeTruthy()
      expect(wrapper.emitted('change')?.[0]).toEqual(['updated'])
    })

    it('emits focus event when child emits focus', async () => {
      const wrapper = createWrapper()
      const child = findInput(wrapper)
      const mockEvent = new FocusEvent('focus')
      child.vm.$emit('focus', mockEvent)
      await nextTick()
      expect(wrapper.emitted('focus')).toBeTruthy()
    })

    it('emits blur event when child emits blur', async () => {
      const wrapper = createWrapper()
      const child = findInput(wrapper)
      const mockEvent = new FocusEvent('blur')
      child.vm.$emit('blur', mockEvent)
      await nextTick()
      expect(wrapper.emitted('blur')).toBeTruthy()
    })

    it('emits linkage event when child emits linkage', async () => {
      const wrapper = createWrapper({
        modelValue: 'trigger',
        fieldConfig: { field: 'test', fieldType: 'text' }
      })
      const child = findInput(wrapper)
      const linkageEvent = {
        field: 'test',
        value: 'trigger',
        linkages: [{ triggerField: 'test', targetField: 'other', action: 'show' as const }]
      }
      child.vm.$emit('linkage', linkageEvent)
      await nextTick()
      expect(wrapper.emitted('linkage')).toBeTruthy()
    })
  })

  describe('fieldType to component mapping', () => {
    it('renders ErpInput when fieldType is text', () => {
      const wrapper = createWrapper({
        fieldConfig: { field: 'name', fieldType: 'text' }
      })
      expect(findInput(wrapper).exists()).toBe(true)
      expect(findTextarea(wrapper).exists()).toBe(false)
    })

    it('renders ErpInput as default when no fieldConfig', () => {
      const wrapper = createWrapper()
      expect(findInput(wrapper).exists()).toBe(true)
      expect(findTextarea(wrapper).exists()).toBe(false)
    })

    it('renders ErpTextarea when fieldType is textarea', () => {
      const wrapper = createWrapper({
        fieldConfig: { field: 'desc', fieldType: 'textarea' }
      })
      expect(findTextarea(wrapper).exists()).toBe(true)
      expect(findInput(wrapper).exists()).toBe(false)
    })

    it('renders ErpInput as fallback for unknown fieldType', () => {
      const wrapper = createWrapper({
        fieldConfig: { field: 'custom', fieldType: 'unknown-type' }
      })
      expect(findInput(wrapper).exists()).toBe(true)
    })

    it('passes fieldConfig to child ErpInput', () => {
      const fieldConfig = { field: 'name', fieldType: 'text', title: '姓名', required: true }
      const wrapper = createWrapper({ fieldConfig })
      const child = findInput(wrapper)
      expect(child.props('fieldConfig')).toEqual(fieldConfig)
    })
  })

  describe('disabled mode', () => {
    it('passes disabled prop to child component', () => {
      const wrapper = createWrapper({ disabled: true })
      const child = findInput(wrapper)
      expect(child.props('disabled')).toBe(true)
    })

    it('passes disabled false to child component', () => {
      const wrapper = createWrapper({ disabled: false })
      const child = findInput(wrapper)
      expect(child.props('disabled')).toBe(false)
    })
  })

  describe('field config integration', () => {
    it('passes fieldConfig.rules to child component', () => {
      const rules = [{ type: 'required' as const, message: '必填' }]
      const fieldConfig = { field: 'name', fieldType: 'text', rules }
      const wrapper = createWrapper({ fieldConfig })
      const child = findInput(wrapper)
      expect(child.props('fieldConfig').rules).toEqual(rules)
    })

    it('passes fieldConfig.linkages to child component', () => {
      const linkages: FieldLinkageRule[] = [
        { triggerField: 'type', targetField: 'name', action: 'show' }
      ]
      const fieldConfig = { field: 'name', fieldType: 'text', linkages }
      const wrapper = createWrapper({ fieldConfig })
      const child = findInput(wrapper)
      expect(child.props('fieldConfig').linkages).toEqual(linkages)
    })

    it('passes fieldConfig with readonly to child', () => {
      const fieldConfig = { field: 'name', fieldType: 'text', readonly: true }
      const wrapper = createWrapper({ fieldConfig })
      const child = findInput(wrapper)
      expect(child.props('fieldConfig').readonly).toBe(true)
    })
  })

  describe('slot passthrough', () => {
    it('passes prefix slot content', () => {
      const wrapper = mount(ErpFieldRenderer, {
        props: { modelValue: '', fieldConfig: { field: 'test', fieldType: 'text' } },
        slots: { prefix: '<span class="prefix-icon">A</span>' },
        global: { stubs: { 'el-input': true, 'el-skeleton': true } }
      })
      expect(findInput(wrapper).exists()).toBe(true)
    })

    it('passes suffix slot content', () => {
      const wrapper = mount(ErpFieldRenderer, {
        props: { modelValue: '', fieldConfig: { field: 'test', fieldType: 'text' } },
        slots: { suffix: '<span class="suffix-unit">元</span>' },
        global: { stubs: { 'el-input': true, 'el-skeleton': true } }
      })
      expect(findInput(wrapper).exists()).toBe(true)
    })

    it('passes default slot content', () => {
      const wrapper = mount(ErpFieldRenderer, {
        props: { modelValue: '', fieldConfig: { field: 'test', fieldType: 'text' } },
        slots: { default: '<span class="custom">content</span>' },
        global: { stubs: { 'el-input': true, 'el-skeleton': true } }
      })
      expect(findInput(wrapper).exists()).toBe(true)
    })
  })

  describe('exposed methods', () => {
    it('exposes validate method', () => {
      const wrapper = createWrapper()
      expect(typeof vm(wrapper).validate).toBe('function')
    })

    it('exposes reset method', () => {
      const wrapper = createWrapper()
      expect(typeof vm(wrapper).reset).toBe('function')
    })

    it('validate returns true without errors', async () => {
      const wrapper = createWrapper()
      const result = await vm(wrapper).validate()
      expect(result).toBe(true)
    })

    it('reset does not throw', () => {
      const wrapper = createWrapper()
      expect(() => vm(wrapper).reset()).not.toThrow()
    })
  })

  describe('reactive fieldType switching', () => {
    it('switches component when fieldConfig.fieldType changes', async () => {
      const wrapper = createWrapper({
        fieldConfig: { field: 'test', fieldType: 'text' }
      })
      expect(findInput(wrapper).exists()).toBe(true)

      await wrapper.setProps({
        fieldConfig: { field: 'test', fieldType: 'textarea' }
      })
      await nextTick()

      expect(findTextarea(wrapper).exists()).toBe(true)
      expect(findInput(wrapper).exists()).toBe(false)
    })
  })

  describe('visible prop', () => {
    it('renders normally when visible is true (default)', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.erp-field-renderer').exists()).toBe(true)
    })

    it('renders nothing when visible is false', () => {
      const wrapper = createWrapper({ visible: false })
      expect(wrapper.find('.erp-field-renderer').exists()).toBe(false)
    })

    it('validate returns true when visible is false', async () => {
      const wrapper = createWrapper({ visible: false })
      const result = await vm(wrapper).validate()
      expect(result).toBe(true)
    })

    it('reset does not throw when visible is false', () => {
      const wrapper = createWrapper({ visible: false })
      expect(() => vm(wrapper).reset()).not.toThrow()
    })

    it('visible prop defaults to true', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('visible')).toBe(true)
    })

    it('does not render child component when visible is false', () => {
      const wrapper = createWrapper({
        visible: false,
        fieldConfig: { field: 'name', fieldType: 'text' }
      })
      expect(findInput(wrapper).exists()).toBe(false)
    })
  })
})
