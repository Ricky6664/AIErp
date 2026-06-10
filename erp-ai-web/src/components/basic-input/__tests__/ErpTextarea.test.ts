import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import ErpTextarea from '@/components/basic-input/ErpTextarea.vue'
import type { ErpTextareaProps } from '@/types/basic-input'
import type { FieldLinkageRule } from '@/types/list-table'

interface ErpTextareaVM {
  validate: () => Promise<boolean>
  reset: () => void
}

function vm(wrapper: VueWrapper): ErpTextareaVM {
  return wrapper.vm as unknown as ErpTextareaVM
}

function createWrapper(overrides: Partial<ErpTextareaProps> = {}): VueWrapper {
  return mount(ErpTextarea, {
    props: {
      modelValue: '',
      disabled: false,
      placeholder: '请多行输入',
      ...overrides
    }
  })
}

describe('ErpTextarea component', () => {
  describe('rendering', () => {
    it('renders a textarea element', () => {
      const wrapper = createWrapper()
      const textarea = wrapper.find('textarea')
      expect(textarea.exists()).toBe(true)
    })

    it('renders with default placeholder', () => {
      const wrapper = createWrapper()
      const textarea = wrapper.find('textarea')
      expect(textarea.attributes('placeholder')).toBe('请多行输入')
    })

    it('renders with custom placeholder from props', () => {
      const wrapper = createWrapper({ placeholder: '请输入备注' })
      const textarea = wrapper.find('textarea')
      expect(textarea.attributes('placeholder')).toBe('请输入备注')
    })

    it('renders with default rows', () => {
      const wrapper = createWrapper()
      const textarea = wrapper.find('textarea')
      expect(textarea.attributes('rows')).toBe('3')
    })

    it('renders with custom rows from props', () => {
      const wrapper = createWrapper({ rows: 5 })
      const textarea = wrapper.find('textarea')
      expect(textarea.attributes('rows')).toBe('5')
    })

    it('renders header with fieldConfig title', () => {
      const wrapper = createWrapper({
        fieldConfig: { title: '备注', field: 'remark', label: '备注', fieldType: 'textarea' }
      })
      const header = wrapper.find('.basic-input__header')
      expect(header.exists()).toBe(true)
      expect(header.find('.basic-input__label').text()).toBe('备注')
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

    it('does not render textarea when loading', () => {
      const wrapper = createWrapper({ loading: true, modelValue: 'test' })
      expect(wrapper.find('textarea').exists()).toBe(false)
    })

    it('applies size modifier class', () => {
      const wrapper = createWrapper({ size: 'small' })
      expect(wrapper.find('.basic-input--small').exists()).toBe(true)
    })

    it('applies resize-none class when resize is none', () => {
      const wrapper = createWrapper({ resize: 'none' })
      expect(wrapper.find('.basic-input--resize-none').exists()).toBe(true)
    })
  })

  describe('v-model binding', () => {
    it('accepts modelValue prop', () => {
      const wrapper = createWrapper({ modelValue: '测试多行文本' })
      const textarea = wrapper.find('textarea')
      expect((textarea.element as HTMLTextAreaElement).value).toBe('测试多行文本')
    })

    it('emits update:modelValue on input change', async () => {
      const wrapper = createWrapper()
      const textarea = wrapper.find('textarea')
      await textarea.setValue('新值')
      const emitted = wrapper.emitted('update:modelValue')
      expect(emitted).toBeTruthy()
      expect(emitted![emitted!.length - 1]).toEqual(['新值'])
    })

    it('emits change event on blur when value changed', async () => {
      const wrapper = createWrapper({ modelValue: '旧值' })
      const textarea = wrapper.find('textarea')
      await textarea.setValue('新值')
      await textarea.trigger('blur')
      const emitted = wrapper.emitted('change')
      expect(emitted).toBeTruthy()
      expect(emitted![emitted!.length - 1]).toEqual(['新值'])
    })

    it('does not emit change event on blur when value unchanged', async () => {
      const wrapper = createWrapper({ modelValue: '相同值' })
      const textarea = wrapper.find('textarea')
      await textarea.trigger('blur')
      expect(wrapper.emitted('change')).toBeFalsy()
    })

    it('reflects prop changes reactively', async () => {
      const wrapper = createWrapper({ modelValue: '初始值' })
      await wrapper.setProps({ modelValue: '更新值' })
      const textarea = wrapper.find('textarea')
      expect((textarea.element as HTMLTextAreaElement).value).toBe('更新值')
    })

    it('clears error messages on input change', async () => {
      const wrapper = createWrapper({
        modelValue: '',
        rules: [{ required: true, message: '必填' }]
      })
      await vm(wrapper).validate()
      await nextTick()
      expect(wrapper.find('.basic-input__error-item').exists()).toBe(true)
      const textarea = wrapper.find('textarea')
      await textarea.setValue('new')
      await nextTick()
      expect(wrapper.find('.basic-input__error-item').exists()).toBe(false)
    })

    it('handles multi-line text content', async () => {
      const wrapper = createWrapper()
      const textarea = wrapper.find('textarea')
      await textarea.setValue('第一行\n第二行\n第三行')
      const emitted = wrapper.emitted('update:modelValue')
      expect(emitted).toBeTruthy()
      expect(emitted![emitted!.length - 1]).toEqual(['第一行\n第二行\n第三行'])
    })
  })

  describe('disabled state', () => {
    it('applies disabled attribute when disabled prop is true', () => {
      const wrapper = createWrapper({ disabled: true })
      const textarea = wrapper.find('textarea')
      expect(textarea.attributes('disabled')).toBeDefined()
    })

    it('does not apply disabled attribute when disabled prop is false', () => {
      const wrapper = createWrapper({ disabled: false })
      const textarea = wrapper.find('textarea')
      expect(textarea.attributes('disabled')).toBeUndefined()
    })

    it('applies disabled modifier class', () => {
      const wrapper = createWrapper({ disabled: true })
      expect(wrapper.find('.basic-input--disabled').exists()).toBe(true)
    })
  })

  describe('events', () => {
    it('emits focus event on textarea focus', async () => {
      const wrapper = createWrapper()
      const elInput = wrapper.findComponent({ name: 'ElInput' })
      await elInput.vm.$emit('focus', new FocusEvent('focus'))
      await nextTick()
      expect(wrapper.emitted('focus')).toBeTruthy()
    })

    it('emits blur event on textarea blur', async () => {
      const wrapper = createWrapper()
      const elInput = wrapper.findComponent({ name: 'ElInput' })
      await elInput.vm.$emit('blur', new FocusEvent('blur'))
      await nextTick()
      expect(wrapper.emitted('blur')).toBeTruthy()
    })
  })

  describe('linkage watching', () => {
    const mockLinkages: FieldLinkageRule[] = [
      {
        triggerField: 'remark',
        targetField: 'subRemark',
        action: 'show',
        condition: (v: unknown) => v === 'A'
      },
      {
        triggerField: 'remark',
        targetField: 'note',
        action: 'disable',
        condition: undefined
      }
    ]

    it('emits linkage event when fieldConfig has linkages and value changes', async () => {
      const wrapper = createWrapper({
        modelValue: '',
        fieldConfig: {
          field: 'remark',
          label: '备注',
          fieldType: 'textarea',
          linkages: mockLinkages
        }
      })
      const textarea = wrapper.find('textarea')
      await textarea.setValue('A')
      await nextTick()
      const emitted = wrapper.emitted('linkage')
      expect(emitted).toBeTruthy()
    })

    it('does not emit linkage when no fieldConfig provided', async () => {
      const wrapper = createWrapper({ modelValue: '' })
      const textarea = wrapper.find('textarea')
      await textarea.setValue('any')
      await nextTick()
      expect(wrapper.emitted('linkage')).toBeFalsy()
    })
  })

  describe('slots', () => {
    it('renders prefix slot content', () => {
      const wrapper = mount(ErpTextarea, {
        props: { modelValue: '' },
        slots: { prefix: '<span class="prefix-slot">前缀</span>' }
      })
      expect(wrapper.find('.prefix-slot').exists()).toBe(true)
    })

    it('renders suffix slot content', () => {
      const wrapper = mount(ErpTextarea, {
        props: { modelValue: '' },
        slots: { suffix: '<span class="suffix-slot">后缀</span>' }
      })
      expect(wrapper.find('.suffix-slot').exists()).toBe(true)
    })

    it('renders append slot content', () => {
      const wrapper = mount(ErpTextarea, {
        props: { modelValue: '' },
        slots: { append: '<span class="append-slot">追加</span>' }
      })
      expect(wrapper.find('.append-slot').exists()).toBe(true)
    })
  })

  describe('error display', () => {
    it('renders error messages for validation failures', async () => {
      const wrapper = createWrapper({
        modelValue: '',
        rules: [
          { required: true, message: '必填项' },
          { min: 10, message: '至少10个字符' }
        ]
      })
      await vm(wrapper).validate()
      await nextTick()
      const errors = wrapper.findAll('.basic-input__error-item')
      expect(errors.length).toBeGreaterThanOrEqual(1)
    })

    it('renders no footer when no errors', () => {
      const wrapper = createWrapper({ modelValue: 'test content' })
      expect(wrapper.find('.basic-input__footer').exists()).toBe(false)
    })
  })

  describe('exposed methods', () => {
    it('exposes validate method that returns true for valid input', async () => {
      const wrapper = createWrapper({ modelValue: '有值的内容' })
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

    it('exposes validate method that checks maxLength rule', async () => {
      const wrapper = createWrapper({
        modelValue: '1234567890',
        rules: [{ max: 5, message: '最多5个字符' }]
      } as Partial<ErpTextareaProps>)
      const result = await vm(wrapper).validate()
      expect(result).toBe(false)
    })

    it('exposes reset method that clears value', async () => {
      const wrapper = createWrapper({ modelValue: '待清空内容' })
      vm(wrapper).reset()
      await nextTick()
      const emitted = wrapper.emitted('update:modelValue')
      expect(emitted).toBeTruthy()
      expect(emitted![emitted!.length - 1]).toEqual([''])
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
      } as Partial<ErpTextareaProps>)
      const result = await vm(wrapper).validate()
      expect(result).toBe(true)
    })

    it('validate fails with custom validator when condition not met', async () => {
      const wrapper = createWrapper({
        modelValue: 'a',
        rules: [{ validator: (v: unknown) => typeof v === 'string' && v.length >= 3 }]
      } as Partial<ErpTextareaProps>)
      const result = await vm(wrapper).validate()
      expect(result).toBe(false)
    })
  })

  describe('resize behavior', () => {
    it('defaults to vertical resize', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.basic-input--resize-vertical').exists()).toBe(true)
    })

    it('applies both resize class', () => {
      const wrapper = createWrapper({ resize: 'both' })
      expect(wrapper.find('.basic-input--resize-vertical').exists()).toBe(false)
    })

    it('applies none resize class', () => {
      const wrapper = createWrapper({ resize: 'none' })
      expect(wrapper.find('.basic-input--resize-none').exists()).toBe(true)
    })
  })
})
