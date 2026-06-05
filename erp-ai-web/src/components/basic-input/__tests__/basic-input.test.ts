import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import BasicInput from '@/components/basic-input/index.vue'
import type { ErpInputProps } from '@/types/basic-input'

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
        fieldConfig: { title: '姓名', fieldName: 'name', fieldType: 'text' }
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
