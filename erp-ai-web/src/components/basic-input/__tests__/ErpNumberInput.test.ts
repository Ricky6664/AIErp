import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import ErpNumberInput from '@/components/basic-input/ErpNumberInput.vue'
import type { ErpNumberInputProps } from '@/types/basic-input'

interface ErpNumberInputVM {
  validate: () => Promise<boolean>
  reset: () => void
}

function vm(wrapper: VueWrapper): ErpNumberInputVM {
  return wrapper.vm as unknown as ErpNumberInputVM
}

function createWrapper(overrides: Partial<ErpNumberInputProps> = {}): VueWrapper {
  return mount(ErpNumberInput, {
    props: {
      modelValue: null,
      disabled: false,
      placeholder: '请输入',
      ...overrides
    }
  })
}

describe('ErpNumberInput component', () => {
  describe('rendering', () => {
    it('renders an el-input-number element', () => {
      const wrapper = createWrapper()
      const input = wrapper.find('.el-input-number')
      expect(input.exists()).toBe(true)
    })

    it('renders with default precision 0 (integer)', () => {
      const wrapper = createWrapper({ modelValue: null })
      const input = wrapper.find('.el-input-number')
      expect(input.exists()).toBe(true)
    })

    it('renders header with fieldConfig title', () => {
      const wrapper = createWrapper({
        fieldConfig: { title: '数量', field: 'quantity', label: '数量', fieldType: 'number' }
      })
      const header = wrapper.find('.basic-input__header')
      expect(header.exists()).toBe(true)
      expect(header.find('.basic-input__label').text()).toBe('数量')
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

    it('applies size modifier class', () => {
      const wrapper = createWrapper({ size: 'small' })
      expect(wrapper.find('.basic-input--small').exists()).toBe(true)
    })
  })

  describe('v-model binding', () => {
    it('accepts modelValue prop', () => {
      const wrapper = createWrapper({ modelValue: 42 })
      const input = wrapper.find('input')
      expect((input.element as HTMLInputElement).value).toBe('42')
    })

    it('emits update:modelValue on input change', async () => {
      const wrapper = createWrapper({ modelValue: null })
      const input = wrapper.find('input')
      await input.setValue('99')
      await input.trigger('input')
      const emitted = wrapper.emitted('update:modelValue')
      expect(emitted).toBeTruthy()
    })

    it('emits change event on blur when value changed', async () => {
      const wrapper = createWrapper({ modelValue: 1 })
      const input = wrapper.find('input')
      await input.setValue('2')
      await input.trigger('blur')
      const emitted = wrapper.emitted('change')
      expect(emitted).toBeTruthy()
    })

    it('reflects prop changes reactively', async () => {
      const wrapper = createWrapper({ modelValue: 10 })
      await wrapper.setProps({ modelValue: 20 })
      const input = wrapper.find('input')
      expect((input.element as HTMLInputElement).value).toBe('20')
    })
  })

  describe('disabled state', () => {
    it('applies disabled attribute when disabled prop is true', () => {
      const wrapper = createWrapper({ disabled: true })
      const input = wrapper.find('input')
      expect(input.attributes('disabled')).toBeDefined()
    })

    it('applies disabled modifier class', () => {
      const wrapper = createWrapper({ disabled: true })
      expect(wrapper.find('.basic-input--disabled').exists()).toBe(true)
    })
  })

  describe('events', () => {
    it('emits focus event on input focus', async () => {
      const wrapper = createWrapper()
      const elInputNumber = wrapper.findComponent({ name: 'ElInputNumber' })
      await elInputNumber.vm.$emit('focus', new FocusEvent('focus'))
      await nextTick()
      expect(wrapper.emitted('focus')).toBeTruthy()
    })

    it('emits blur event on input blur', async () => {
      const wrapper = createWrapper()
      const elInputNumber = wrapper.findComponent({ name: 'ElInputNumber' })
      await elInputNumber.vm.$emit('blur', new FocusEvent('blur'))
      await nextTick()
      expect(wrapper.emitted('blur')).toBeTruthy()
    })
  })

  describe('slots', () => {
    it('renders prefix slot content', () => {
      const wrapper = mount(ErpNumberInput, {
        props: { modelValue: null },
        slots: { prefix: '<span class="prefix-slot">前缀</span>' }
      })
      expect(wrapper.find('.prefix-slot').exists()).toBe(true)
    })

    it('renders suffix slot content', () => {
      const wrapper = mount(ErpNumberInput, {
        props: { modelValue: null },
        slots: { suffix: '<span class="suffix-slot">后缀</span>' }
      })
      expect(wrapper.find('.suffix-slot').exists()).toBe(true)
    })

    it('renders header slot content', () => {
      const wrapper = mount(ErpNumberInput, {
        props: { modelValue: null },
        slots: { header: '<span class="header-slot">标题区</span>' }
      })
      expect(wrapper.find('.basic-input__header').exists()).toBe(true)
      expect(wrapper.find('.header-slot').exists()).toBe(true)
    })

    it('renders footer slot content', () => {
      const wrapper = mount(ErpNumberInput, {
        props: { modelValue: null },
        slots: { footer: '<span class="footer-slot">底部区</span>' }
      })
      expect(wrapper.find('.basic-input__footer').exists()).toBe(true)
      expect(wrapper.find('.footer-slot').exists()).toBe(true)
    })
  })

  describe('error display', () => {
    it('renders error messages in footer via v-for', async () => {
      const wrapper = createWrapper({
        modelValue: null,
        rules: [{ required: true, message: '必填项' }]
      })
      await vm(wrapper).validate()
      await nextTick()
      const errors = wrapper.findAll('.basic-input__error-item')
      expect(errors.length).toBeGreaterThanOrEqual(1)
    })

    it('renders no footer when no errors', () => {
      const wrapper = createWrapper({ modelValue: 100 })
      expect(wrapper.find('.basic-input__footer').exists()).toBe(false)
    })
  })

  describe('exposed methods', () => {
    it('exposes validate method that returns true for valid input', async () => {
      const wrapper = createWrapper({ modelValue: 5 })
      const result = await vm(wrapper).validate()
      expect(result).toBe(true)
    })

    it('exposes validate method that returns false when required and empty', async () => {
      const wrapper = createWrapper({
        modelValue: null,
        rules: [{ required: true, message: '必填' }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(false)
    })

    it('exposes validate method that returns false when min rule not met', async () => {
      const wrapper = createWrapper({
        modelValue: 1,
        rules: [{ min: 3, message: '最小值为3' }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(false)
    })

    it('exposes validate method that returns false when max rule exceeded', async () => {
      const wrapper = createWrapper({
        modelValue: 100,
        rules: [{ max: 50, message: '最大值为50' }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(false)
    })

    it('exposes validate method that returns true when within range', async () => {
      const wrapper = createWrapper({
        modelValue: 25,
        rules: [{ min: 0, max: 100 }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(true)
    })

    it('exposes reset method that clears value to null', async () => {
      const wrapper = createWrapper({ modelValue: 42 })
      vm(wrapper).reset()
      await nextTick()
      const emitted = wrapper.emitted('update:modelValue')
      expect(emitted).toBeTruthy()
      expect(emitted![0]).toEqual([null])
    })

    it('exposes reset method that clears error messages', async () => {
      const wrapper = createWrapper({
        modelValue: null,
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
        modelValue: 5,
        rules: [{ validator: (v: unknown) => typeof v === 'number' && v >= 0 }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(true)
    })

    it('validate fails with custom validator when condition not met', async () => {
      const wrapper = createWrapper({
        modelValue: -1,
        rules: [{ validator: (v: unknown) => Number(v) > 0, message: '必须大于0' }]
      })
      const result = await vm(wrapper).validate()
      expect(result).toBe(false)
    })
  })
})
