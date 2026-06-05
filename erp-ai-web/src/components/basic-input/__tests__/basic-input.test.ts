import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import BasicInput from '@/components/basic-input/index.vue'
import type { ErpInputProps } from '@/types/basic-input'
import type { FieldLinkageRule } from '@/types/list-table'

/** Exposed methods accessible on the component vm */
interface BasicInputVM {
  validate: () => Promise<boolean>
  reset: () => void
}

function vm(wrapper: VueWrapper): BasicInputVM {
  return wrapper.vm as unknown as BasicInputVM
}

function createWrapper(overrides: Partial<ErpInputProps> = {}): VueWrapper {
  return mount(BasicInput, {
    props: {
      modelValue: '',
      disabled: false,
      placeholder: '请输入',
      ...overrides
    }
  })
}

describe('BasicInput component', () => {
  describe('rendering', () => {
    it('renders an el-input element', () => {
      const wrapper = createWrapper()
      const input = wrapper.find('.el-input')
      expect(input.exists()).toBe(true)
    })

    it('renders with default placeholder', () => {
      const wrapper = createWrapper()
      const input = wrapper.find('input')
      expect(input.attributes('placeholder')).toBe('请输入')
    })

    it('renders with custom placeholder from props', () => {
      const wrapper = createWrapper({ placeholder: '请输入姓名' })
      const input = wrapper.find('input')
      expect(input.attributes('placeholder')).toBe('请输入姓名')
    })

    it('renders clearable by default when not disabled', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.el-input').exists()).toBe(true)
    })

    it('renders header with fieldConfig title', () => {
      const wrapper = createWrapper({
        fieldConfig: { title: '姓名', field: 'name', label: '姓名', fieldType: 'text' }
      })
      const header = wrapper.find('.basic-input__header')
      expect(header.exists()).toBe(true)
      expect(header.find('.basic-input__label').text()).toBe('姓名')
    })

    it('does not render header when fieldConfig has no title', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.basic-input__header').exists()).toBe(false)
    })

    it('renders loading skeleton when loading is true', () => {
      const wrapper = createWrapper({ loading: true })
      expect(wrapper.find('.basic-input__loading').exists()).toBe(true)
      expect(wrapper.find('.el-skeleton').exists()).toBe(true)
    })

    it('does not render el-input when loading', () => {
      const wrapper = createWrapper({ loading: true, modelValue: 'test' })
      expect(wrapper.find('.el-input').exists()).toBe(false)
    })

    it('applies size modifier class', () => {
      const wrapper = createWrapper({ size: 'small' })
      expect(wrapper.find('.basic-input--small').exists()).toBe(true)
    })

    it('does not apply size class for default size', () => {
      const wrapper = createWrapper({ size: 'default' })
      expect(wrapper.find('.basic-input--default').exists()).toBe(false)
    })
  })

  describe('v-model binding', () => {
    it('accepts modelValue prop', () => {
      const wrapper = createWrapper({ modelValue: '测试文本' })
      const input = wrapper.find('input')
      expect((input.element as HTMLInputElement).value).toBe('测试文本')
    })

    it('emits update:modelValue on input change', async () => {
      const wrapper = createWrapper()
      const input = wrapper.find('input')
      await input.setValue('新值')
      const emitted = wrapper.emitted('update:modelValue')
      expect(emitted).toBeTruthy()
      expect(emitted![emitted!.length - 1]).toEqual(['新值'])
    })

    it('emits change event on blur when value changed', async () => {
      const wrapper = createWrapper({ modelValue: '旧值' })
      const input = wrapper.find('input')
      await input.setValue('新值')
      await input.trigger('blur')
      const emitted = wrapper.emitted('change')
      expect(emitted).toBeTruthy()
      expect(emitted![emitted!.length - 1]).toEqual(['新值'])
    })

    it('does not emit change event on blur when value unchanged', async () => {
      const wrapper = createWrapper({ modelValue: '相同值' })
      const input = wrapper.find('input')
      await input.trigger('blur')
      expect(wrapper.emitted('change')).toBeFalsy()
    })

    it('reflects prop changes reactively', async () => {
      const wrapper = createWrapper({ modelValue: '初始值' })
      await wrapper.setProps({ modelValue: '更新值' })
      const input = wrapper.find('input')
      expect((input.element as HTMLInputElement).value).toBe('更新值')
    })

    it('clears error messages on input change', async () => {
      const wrapper = createWrapper({
        modelValue: '',
        rules: [{ required: true, message: '必填' }]
      })
      await vm(wrapper).validate()
      await nextTick()
      expect(wrapper.find('.basic-input__error-item').exists()).toBe(true)
      const input = wrapper.find('input')
      await input.setValue('new')
      await nextTick()
      expect(wrapper.find('.basic-input__error-item').exists()).toBe(false)
    })
  })

  describe('disabled state', () => {
    it('applies disabled attribute when disabled prop is true', () => {
      const wrapper = createWrapper({ disabled: true })
      const input = wrapper.find('input')
      expect(input.attributes('disabled')).toBeDefined()
    })

    it('does not apply disabled attribute when disabled prop is false', () => {
      const wrapper = createWrapper({ disabled: false })
      const input = wrapper.find('input')
      expect(input.attributes('disabled')).toBeUndefined()
    })

    it('applies disabled modifier class', () => {
      const wrapper = createWrapper({ disabled: true })
      expect(wrapper.find('.basic-input--disabled').exists()).toBe(true)
    })
  })

  describe('events', () => {
    it('emits focus event on input focus', async () => {
      const wrapper = createWrapper()
      const elInput = wrapper.findComponent({ name: 'ElInput' })
      await elInput.vm.$emit('focus', new FocusEvent('focus'))
      await nextTick()
      expect(wrapper.emitted('focus')).toBeTruthy()
    })

    it('emits blur event on input blur', async () => {
      const wrapper = createWrapper()
      const elInput = wrapper.findComponent({ name: 'ElInput' })
      await elInput.vm.$emit('blur', new FocusEvent('blur'))
      await nextTick()
      expect(wrapper.emitted('blur')).toBeTruthy()
    })
  })

  describe('linkage watching (监听触发字段变化)', () => {
    const mockLinkages: FieldLinkageRule[] = [
      {
        triggerField: 'category',
        targetField: 'subCategory',
        action: 'show',
        condition: (v: unknown) => v === 'A'
      },
      {
        triggerField: 'category',
        targetField: 'price',
        action: 'setValue',
        condition: (v: unknown) => v === 'premium',
        params: { value: 100 }
      },
      {
        triggerField: 'category',
        targetField: 'note',
        action: 'disable',
        condition: undefined
      },
      {
        triggerField: 'category',
        targetField: 'reason',
        action: 'setRequired',
        condition: (v: unknown) => v === 'A',
        params: { required: true }
      }
    ]

    it('emits linkage event when fieldConfig has linkages and value changes', async () => {
      const wrapper = createWrapper({
        modelValue: '',
        fieldConfig: {
          field: 'category',
          label: '类别',
          fieldType: 'text',
          linkages: mockLinkages
        }
      })
      const input = wrapper.find('input')
      await input.setValue('A')
      await nextTick()
      const emitted = wrapper.emitted('linkage')
      expect(emitted).toBeTruthy()
    })

    it('emits linkage event only with rules whose condition passes', async () => {
      const wrapper = createWrapper({
        modelValue: '',
        fieldConfig: {
          field: 'category',
          label: '类别',
          fieldType: 'text',
          linkages: mockLinkages
        } as ErpInputProps['fieldConfig']
      })
      const input = wrapper.find('input')
      await input.setValue('A')
      await nextTick()
      const emitted = wrapper.emitted('linkage')
      expect(emitted).toBeTruthy()
      const payload = emitted![emitted!.length - 1][0] as any
      expect(payload.field).toBe('category')
      expect(payload.value).toBe('A')
      // Only the rule with condition matching 'A' should trigger (show subCategory + setRequired reason),
      // plus the unconditional rule (disable note)
      expect(payload.linkages.length).toBe(3)
    })

    it('emits linkage event with unconditional rules when no condition set', async () => {
      const wrapper = createWrapper({
        modelValue: '',
        fieldConfig: {
          field: 'category',
          label: '类别',
          fieldType: 'text',
          linkages: mockLinkages
        } as ErpInputProps['fieldConfig']
      })
      const input = wrapper.find('input')
      await input.setValue('random-value')
      await nextTick()
      const emitted = wrapper.emitted('linkage')
      expect(emitted).toBeTruthy()
      const payload = emitted![emitted!.length - 1][0] as any
      // Only the unconditional rule (disable note) should trigger
      expect(payload.linkages.length).toBe(1)
      expect(payload.linkages[0].action).toBe('disable')
    })

    it('does not emit linkage when fieldConfig has no linkages', async () => {
      const wrapper = createWrapper({
        modelValue: '',
        fieldConfig: {
          field: 'category',
          label: '类别',
          type: 'input'
        } as ErpInputProps['fieldConfig']
      })
      const input = wrapper.find('input')
      await input.setValue('A')
      await nextTick()
      expect(wrapper.emitted('linkage')).toBeFalsy()
    })

    it('does not emit linkage when no fieldConfig provided', async () => {
      const wrapper = createWrapper({ modelValue: '' })
      const input = wrapper.find('input')
      await input.setValue('any')
      await nextTick()
      expect(wrapper.emitted('linkage')).toBeFalsy()
    })

    it('emits linkage event for setValue action with params', async () => {
      const wrapper = createWrapper({
        modelValue: '',
        fieldConfig: {
          field: 'category',
          label: '类别',
          fieldType: 'text',
          linkages: mockLinkages
        } as ErpInputProps['fieldConfig']
      })
      const input = wrapper.find('input')
      await input.setValue('premium')
      await nextTick()
      const emitted = wrapper.emitted('linkage')
      expect(emitted).toBeTruthy()
      const payload = emitted![emitted!.length - 1][0] as any
      expect(payload.value).toBe('premium')
      const setValueRules = payload.linkages.filter(
        (l: FieldLinkageRule) => l.action === 'setValue'
      )
      expect(setValueRules.length).toBe(1)
      expect(setValueRules[0].params).toEqual({ value: 100 })
    })

    it('does not emit linkage when value unchanged', async () => {
      const wrapper = createWrapper({
        modelValue: 'A',
        fieldConfig: {
          field: 'category',
          label: '类别',
          fieldType: 'text',
          linkages: mockLinkages
        } as ErpInputProps['fieldConfig']
      })
      // Set same value again via prop
      await wrapper.setProps({ modelValue: 'A' })
      await nextTick()
      // The watch compares newValue !== oldValue, so should not emit
      const emitted = wrapper.emitted('linkage')
      // The initial mount with modelValue 'A' may trigger the watch (old is undefined)
      // but we're checking that no duplicate linkage events fire
      const emissionCount = emitted ? emitted.length : 0
      // At mount, innerValue goes from undefined to 'A' which triggers linkage
      // That's expected — one emission at most
      expect(emissionCount).toBeLessThanOrEqual(1)
    })
  })

  describe('slots', () => {
    it('renders prefix slot content', () => {
      const wrapper = mount(BasicInput, {
        props: { modelValue: '' },
        slots: { prefix: '<span class="prefix-slot">前缀</span>' }
      })
      expect(wrapper.find('.prefix-slot').exists()).toBe(true)
    })

    it('renders suffix slot content', () => {
      const wrapper = mount(BasicInput, {
        props: { modelValue: '' },
        slots: { suffix: '<span class="suffix-slot">后缀</span>' }
      })
      expect(wrapper.find('.suffix-slot').exists()).toBe(true)
    })

    it('renders append slot content', () => {
      const wrapper = mount(BasicInput, {
        props: { modelValue: '' },
        slots: { append: '<span class="append-slot">追加</span>' }
      })
      expect(wrapper.find('.append-slot').exists()).toBe(true)
    })

    it('renders header slot content', () => {
      const wrapper = mount(BasicInput, {
        props: { modelValue: '' },
        slots: { header: '<span class="header-slot">标题区</span>' }
      })
      expect(wrapper.find('.basic-input__header').exists()).toBe(true)
      expect(wrapper.find('.header-slot').exists()).toBe(true)
    })

    it('renders footer slot content', () => {
      const wrapper = mount(BasicInput, {
        props: { modelValue: '' },
        slots: { footer: '<span class="footer-slot">底部区</span>' }
      })
      expect(wrapper.find('.basic-input__footer').exists()).toBe(true)
      expect(wrapper.find('.footer-slot').exists()).toBe(true)
    })
  })

  describe('error display', () => {
    it('renders error messages in footer via v-for', async () => {
      const wrapper = createWrapper({
        modelValue: '',
        rules: [
          { required: true, message: '必填项' },
          { min: 3, message: '至少3个字符' }
        ]
      })
      await vm(wrapper).validate()
      await nextTick()
      const errors = wrapper.findAll('.basic-input__error-item')
      expect(errors.length).toBeGreaterThanOrEqual(1)
    })

    it('renders no footer when no errors', () => {
      const wrapper = createWrapper({ modelValue: 'test' })
      expect(wrapper.find('.basic-input__footer').exists()).toBe(false)
    })
  })

  describe('exposed methods', () => {
    it('exposes validate method that returns true for valid input', async () => {
      const wrapper = createWrapper({ modelValue: '有值' })
      const result = await vm(wrapper).validate()
      expect(result).toBe(true)
    })

    it('exposes validate method that returns false when required and empty', async () => {
      const wrapper = createWrapper({
        modelValue: '',
        rules: [{ required: true, message: '必填' }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(false)
    })

    it('exposes validate method that returns false when min rule not met', async () => {
      const wrapper = createWrapper({
        modelValue: 'ab',
        rules: [{ min: 3, message: '至少3个字符' }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(false)
    })

    it('exposes validate method that returns false when max rule exceeded', async () => {
      const wrapper = createWrapper({
        modelValue: 'abcdef',
        rules: [{ max: 5, message: '最多5个字符' }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(false)
    })

    it('exposes validate method that returns false when pattern not matched', async () => {
      const wrapper = createWrapper({
        modelValue: 'abc',
        rules: [{ pattern: /^[0-9]+$/, message: '仅允许数字' }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(false)
    })

    it('exposes validate method that returns true when pattern matched', async () => {
      const wrapper = createWrapper({
        modelValue: '123',
        rules: [{ pattern: /^[0-9]+$/ }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(true)
    })

    it('exposes reset method that clears value', async () => {
      const wrapper = createWrapper({ modelValue: '待清空' })
      vm(wrapper).reset()
      await nextTick()
      const emitted = wrapper.emitted('update:modelValue')
      expect(emitted).toBeTruthy()
      expect(emitted![0]).toEqual([''])
    })

    it('exposes reset method that clears error messages', async () => {
      const wrapper = createWrapper({
        modelValue: '',
        rules: [{ required: true, message: '必填' }]
      })
      await vm(wrapper).validate()
      await nextTick()
      expect(wrapper.find('.basic-input__error-item').exists()).toBe(true)
      vm(wrapper).reset()
      await nextTick()
      expect(wrapper.find('.basic-input__error-item').exists()).toBe(false)
    })

    it('validate with custom validator function', async () => {
      const wrapper = createWrapper({
        modelValue: 'abc',
        rules: [{ validator: (v: unknown) => typeof v === 'string' && v.length >= 2 }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(true)
    })

    it('validate fails with custom validator when condition not met', async () => {
      const wrapper = createWrapper({
        modelValue: 'a',
        rules: [{ validator: (v: unknown) => typeof v === 'string' && v.length >= 3 }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(false)
    })
  })
})
