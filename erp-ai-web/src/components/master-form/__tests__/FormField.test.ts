import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import FormField from '../FormField.vue'
import type { FormFieldConfig } from '@/types/master-form'

function makeFieldConfig(overrides: Partial<FormFieldConfig> = {}): FormFieldConfig {
  return {
    field: 'testField',
    fieldType: 'text',
    label: '测试字段',
    ...overrides
  }
}

describe('FormField', () => {
  it('renders text input for fieldType "text"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'hello',
        fieldConfig: makeFieldConfig({ fieldType: 'text' })
      }
    })
    const input = wrapper.find('input')
    expect(input.exists()).toBe(true)
    expect((input.element as HTMLInputElement).value).toBe('hello')
  })

  it('renders number input for fieldType "number"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 42,
        fieldConfig: makeFieldConfig({ fieldType: 'number' })
      }
    })
    expect(wrapper.findComponent({ name: 'ElInputNumber' }).exists()).toBe(true)
  })

  it('renders textarea for fieldType "textarea"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'multiline',
        fieldConfig: makeFieldConfig({ fieldType: 'textarea' })
      }
    })
    const textarea = wrapper.find('textarea')
    expect(textarea.exists()).toBe(true)
  })

  it('renders password input for fieldType "password"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'secret',
        fieldConfig: makeFieldConfig({ fieldType: 'password' })
      }
    })
    const input = wrapper.find('input[type="password"]')
    expect(input.exists()).toBe(true)
  })

  it('renders select for fieldType "select"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'opt1',
        fieldConfig: makeFieldConfig({
          fieldType: 'select',
          options: [
            { label: '选项1', value: 'opt1' },
            { label: '选项2', value: 'opt2' }
          ]
        })
      }
    })
    expect(wrapper.findComponent({ name: 'ElSelect' }).exists()).toBe(true)
  })

  it('renders switch for fieldType "switch"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: true,
        fieldConfig: makeFieldConfig({ fieldType: 'switch' })
      }
    })
    expect(wrapper.findComponent({ name: 'ElSwitch' }).exists()).toBe(true)
  })

  it('renders radio group for fieldType "radio"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'a',
        fieldConfig: makeFieldConfig({
          fieldType: 'radio',
          options: [
            { label: 'A', value: 'a' },
            { label: 'B', value: 'b' }
          ]
        })
      }
    })
    expect(wrapper.findComponent({ name: 'ElRadioGroup' }).exists()).toBe(true)
  })

  it('renders checkbox group for fieldType "checkbox"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: ['a'],
        fieldConfig: makeFieldConfig({
          fieldType: 'checkbox',
          options: [
            { label: 'A', value: 'a' },
            { label: 'B', value: 'b' }
          ]
        })
      }
    })
    expect(wrapper.findComponent({ name: 'ElCheckboxGroup' }).exists()).toBe(true)
  })

  it('renders date picker for fieldType "date"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: '2026-01-01',
        fieldConfig: makeFieldConfig({ fieldType: 'date' })
      }
    })
    expect(wrapper.findComponent({ name: 'ElDatePicker' }).exists()).toBe(true)
  })

  it('renders datetime picker for fieldType "datetime"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: '2026-01-01 12:00:00',
        fieldConfig: makeFieldConfig({ fieldType: 'datetime' })
      }
    })
    expect(wrapper.findComponent({ name: 'ElDatePicker' }).exists()).toBe(true)
  })

  it('renders time picker for fieldType "time"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: '12:00:00',
        fieldConfig: makeFieldConfig({ fieldType: 'time' })
      }
    })
    expect(wrapper.findComponent({ name: 'ElTimePicker' }).exists()).toBe(true)
  })

  it('renders color picker for fieldType "color"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: '#ff0000',
        fieldConfig: makeFieldConfig({ fieldType: 'color' })
      }
    })
    expect(wrapper.findComponent({ name: 'ElColorPicker' }).exists()).toBe(true)
  })

  it('renders rate for fieldType "rate"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 3,
        fieldConfig: makeFieldConfig({ fieldType: 'rate' })
      }
    })
    expect(wrapper.findComponent({ name: 'ElRate' }).exists()).toBe(true)
  })

  it('renders slider for fieldType "slider"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 50,
        fieldConfig: makeFieldConfig({ fieldType: 'slider' })
      }
    })
    expect(wrapper.findComponent({ name: 'ElSlider' }).exists()).toBe(true)
  })

  it('emits update:modelValue on input change', async () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'old',
        fieldConfig: makeFieldConfig({ fieldType: 'text' })
      }
    })
    const input = wrapper.find('input')
    await input.setValue('new')
    const emitted = wrapper.emitted('update:modelValue')
    expect(emitted).toBeTruthy()
    expect(emitted![0]).toEqual(['new'])
  })

  it('emits change event on value change', async () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'old',
        fieldConfig: makeFieldConfig({ fieldType: 'text' })
      }
    })
    const input = wrapper.find('input')
    await input.setValue('new')
    await input.trigger('change')
    expect(wrapper.emitted('change')).toBeTruthy()
  })

  it('applies disabled state correctly', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'test',
        fieldConfig: makeFieldConfig({ fieldType: 'text' }),
        disabled: true
      }
    })
    const input = wrapper.find('input')
    expect((input.element as HTMLInputElement).disabled).toBe(true)
  })

  it('applies readonly mode from fieldConfig', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'test',
        fieldConfig: makeFieldConfig({ fieldType: 'text', readonly: true })
      }
    })
    const input = wrapper.find('input')
    expect((input.element as HTMLInputElement).disabled).toBe(true)
  })

  it('uses custom placeholder when provided', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: '',
        fieldConfig: makeFieldConfig({ fieldType: 'text' }),
        placeholder: '自定义占位'
      }
    })
    const input = wrapper.find('input')
    expect((input.element as HTMLInputElement).placeholder).toBe('自定义占位')
  })

  it('uses fieldConfig placeholder when prop placeholder not provided', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: '',
        fieldConfig: makeFieldConfig({
          fieldType: 'text',
          placeholder: '字段级占位'
        })
      }
    })
    const input = wrapper.find('input')
    expect((input.element as HTMLInputElement).placeholder).toBe('字段级占位')
  })

  it('renders multi-select for fieldType "multi-select"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: ['opt1'],
        fieldConfig: makeFieldConfig({
          fieldType: 'multi-select',
          options: [
            { label: '选项1', value: 'opt1' },
            { label: '选项2', value: 'opt2' }
          ]
        })
      }
    })
    expect(wrapper.findComponent({ name: 'ElSelect' }).exists()).toBe(true)
  })

  it('renders tree-select for fieldType "tree-select"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'node1',
        fieldConfig: makeFieldConfig({
          fieldType: 'tree-select',
          options: [
            { label: '节点1', value: 'node1', children: [{ label: '子1', value: 'child1' }] }
          ]
        })
      }
    })
    expect(wrapper.findComponent({ name: 'ElTreeSelect' }).exists()).toBe(true)
  })

  it('renders cascader for fieldType "cascader"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: ['a', 'b'],
        fieldConfig: makeFieldConfig({
          fieldType: 'cascader',
          options: [{ label: 'A', value: 'a', children: [{ label: 'B', value: 'b' }] }]
        })
      }
    })
    expect(wrapper.findComponent({ name: 'ElCascader' }).exists()).toBe(true)
  })

  it('renders upload button for fieldType "upload"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: undefined,
        fieldConfig: makeFieldConfig({ fieldType: 'upload' })
      }
    })
    expect(wrapper.findComponent({ name: 'ElUpload' }).exists()).toBe(true)
  })

  it('renders image upload for fieldType "image"', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: undefined,
        fieldConfig: makeFieldConfig({ fieldType: 'image' })
      }
    })
    expect(wrapper.findComponent({ name: 'ElUpload' }).exists()).toBe(true)
  })

  it('falls back to text input for unknown fieldType', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'fallback',
        fieldConfig: makeFieldConfig({ fieldType: 'richtext' as never })
      }
    })
    const input = wrapper.find('input')
    expect(input.exists()).toBe(true)
  })

  it('uses default placeholder when none provided', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: '',
        fieldConfig: makeFieldConfig({ fieldType: 'text', placeholder: undefined })
      }
    })
    const input = wrapper.find('input')
    expect((input.element as HTMLInputElement).placeholder).toBe('请输入')
  })

  it('exposes resetField method that resets to defaultValue', async () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'current',
        fieldConfig: makeFieldConfig({ fieldType: 'text', defaultValue: 'defaultVal' })
      }
    })
    const vm = wrapper.vm as unknown as { resetField: () => void }
    vm.resetField()
    await nextTick()
    expect(wrapper.emitted('update:modelValue')).toBeTruthy()
    expect(wrapper.emitted('update:modelValue')![0]).toEqual(['defaultVal'])
  })

  it('exposes validate method returning true', async () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'val',
        fieldConfig: makeFieldConfig({ fieldType: 'text' })
      }
    })
    const vm = wrapper.vm as unknown as { validate: () => Promise<boolean> }
    const result = await vm.validate()
    expect(result).toBe(true)
  })

  it('accepts layoutConfig prop without error', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'val',
        fieldConfig: makeFieldConfig({ fieldType: 'text' }),
        layoutConfig: { mode: 'vertical', labelWidth: '120px' }
      }
    })
    expect(wrapper.find('input').exists()).toBe(true)
  })

  it('renders input and is interactive', () => {
    const wrapper = mount(FormField, {
      props: {
        modelValue: 'test',
        fieldConfig: makeFieldConfig({ fieldType: 'text' })
      }
    })
    const input = wrapper.find('input')
    expect(input.exists()).toBe(true)
    // Verify the component renders without errors and the input is accessible
    expect((input.element as HTMLInputElement).disabled).toBe(false)
  })
})
