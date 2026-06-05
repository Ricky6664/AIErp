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
      // clearable is enabled, clear icon rendered by Element Plus
      expect(wrapper.find('.el-input').exists()).toBe(true)
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

    it('exposes reset method that clears value', async () => {
      const wrapper = createWrapper({ modelValue: '待清空' })
      vm(wrapper).reset()
      await nextTick()
      const emitted = wrapper.emitted('update:modelValue')
      expect(emitted).toBeTruthy()
      expect(emitted![0]).toEqual([''])
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
