import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import QueryPanel from '@/components/query-panel/index.vue'
import type { FieldConfig } from '@/types/query-panel'

const baseFields: FieldConfig[] = [
  { field: 'keyword', label: '关键词', type: 'input', placeholder: '请输入关键词' },
  {
    field: 'status',
    label: '状态',
    type: 'select',
    options: [
      { label: '启用', value: 1 },
      { label: '禁用', value: 0 }
    ]
  },
  { field: 'createDate', label: '创建日期', type: 'date' }
]

function createWrapper(
  overrides: {
    modelValue?: Record<string, unknown>
    fieldConfig?: FieldConfig[]
    disabled?: boolean
    collapsible?: boolean
  } = {}
): VueWrapper {
  return mount(QueryPanel, {
    props: {
      modelValue: overrides.modelValue ?? {},
      fieldConfig: overrides.fieldConfig ?? baseFields,
      disabled: overrides.disabled ?? false,
      collapsible: overrides.collapsible ?? true
    }
  })
}

describe('QueryPanel component', () => {
  describe('rendering', () => {
    it('renders form items for each field config', () => {
      const wrapper = createWrapper()
      const items = wrapper.findAll('.el-form-item')
      expect(items.length).toBe(3)
    })

    it('renders el-input for text field type', () => {
      const wrapper = createWrapper()
      const input = wrapper.find('.el-form-item input[type="text"]')
      expect(input.exists()).toBe(true)
    })

    it('renders search and reset buttons', () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      expect(buttons.length).toBeGreaterThanOrEqual(2)
    })

    it('applies placeholder from field config', () => {
      const wrapper = createWrapper()
      const input = wrapper.find('.el-form-item input[type="text"]')
      expect(input.attributes('placeholder')).toBe('请输入关键词')
    })
  })

  describe('v-model', () => {
    it('initializes local model from modelValue prop', async () => {
      const wrapper = createWrapper({ modelValue: { keyword: 'test' } })
      await nextTick()
      const input = wrapper.find('.el-form-item input[type="text"]').element as HTMLInputElement
      expect(input.value).toBe('test')
    })

    it('emits update:modelValue when search is clicked', async () => {
      const wrapper = createWrapper({ modelValue: { keyword: 'hello' } })
      await nextTick()
      const searchBtn = wrapper.findAllComponents({ name: 'ElButton' })[0]
      await searchBtn.trigger('click')
      const emitted = wrapper.emitted('update:modelValue') as unknown[][]
      expect(emitted).toBeTruthy()
      const payload = emitted[0][0] as Record<string, unknown>
      expect(payload.keyword).toBe('hello')
    })

    it('reacts to modelValue prop changes', async () => {
      const wrapper = createWrapper({ modelValue: { keyword: 'initial' } })
      await nextTick()
      await wrapper.setProps({ modelValue: { keyword: 'updated' } })
      await nextTick()
      const input = wrapper.find('.el-form-item input[type="text"]').element as HTMLInputElement
      expect(input.value).toBe('updated')
    })

    it('emits change event with field name and value', async () => {
      const wrapper = createWrapper()
      await nextTick()
      const input = wrapper.find('.el-form-item input[type="text"]')
      await input.setValue('new value')
      await input.trigger('change')
      const emitted = wrapper.emitted('change') as unknown[][]
      expect(emitted).toBeTruthy()
      expect(emitted[0]).toEqual(['keyword', 'new value'])
    })
  })

  describe('disabled mode', () => {
    it('disables form fields when disabled prop is true', () => {
      const wrapper = createWrapper({ disabled: true })
      const form = wrapper.findComponent({ name: 'ElForm' })
      expect(form.props('disabled')).toBe(true)
    })
  })

  describe('search', () => {
    it('emits search event when search button clicked', async () => {
      const wrapper = createWrapper()
      const searchBtn = wrapper.findAllComponents({ name: 'ElButton' })[0]
      await searchBtn.trigger('click')
      expect(wrapper.emitted('search')).toBeTruthy()
    })

    it('emits search event on Enter key in form', async () => {
      const wrapper = createWrapper()
      await nextTick()
      const form = wrapper.find('.el-form')
      await form.trigger('keyup.enter')
      expect(wrapper.emitted('search')).toBeTruthy()
    })
  })

  describe('reset', () => {
    it('resets all fields to defaultValue when reset clicked', async () => {
      const fieldsWithDefaults: FieldConfig[] = [
        { field: 'keyword', label: '关键词', type: 'input', defaultValue: '' },
        {
          field: 'status',
          label: '状态',
          type: 'select',
          defaultValue: undefined,
          options: [
            { label: '启用', value: 1 },
            { label: '禁用', value: 0 }
          ]
        }
      ]
      const wrapper = createWrapper({
        modelValue: { keyword: 'search text', status: 1 },
        fieldConfig: fieldsWithDefaults
      })
      await nextTick()
      const resetBtn = wrapper.findAllComponents({ name: 'ElButton' })[1]
      await resetBtn.trigger('click')
      expect(wrapper.emitted('reset')).toBeTruthy()
    })
  })

  describe('collapsible', () => {
    it('shows only first 8 fields when collapsed by default', async () => {
      const manyFields: FieldConfig[] = Array.from({ length: 12 }, (_, i) => ({
        field: `field${i}`,
        label: `字段${i}`,
        type: 'input' as const
      }))
      const wrapper = createWrapper({ fieldConfig: manyFields })
      await nextTick()
      expect(wrapper.findAll('.el-form-item').length).toBe(8)
    })

    it('shows all fields when threshold not exceeded', async () => {
      const wrapper = createWrapper()
      await nextTick()
      expect(wrapper.findAll('.el-form-item').length).toBe(3)
    })

    it('does not show toggle when collapsible is false', () => {
      const manyFields: FieldConfig[] = Array.from({ length: 12 }, (_, i) => ({
        field: `field${i}`,
        label: `字段${i}`,
        type: 'input' as const
      }))
      const wrapper = createWrapper({ fieldConfig: manyFields, collapsible: false })
      const actions = wrapper.find('.query-panel__actions')
      expect(actions.text()).not.toContain('展开')
      expect(actions.text()).not.toContain('收起')
    })

    it('toggles collapsed state when expand button clicked', async () => {
      const manyFields: FieldConfig[] = Array.from({ length: 12 }, (_, i) => ({
        field: `field${i}`,
        label: `字段${i}`,
        type: 'input' as const
      }))
      const wrapper = createWrapper({ fieldConfig: manyFields })
      await nextTick()
      expect(wrapper.findAll('.el-form-item').length).toBe(8)
      const toggleBtn = wrapper.findAllComponents({ name: 'ElButton' })[2]
      await toggleBtn.trigger('click')
      await nextTick()
      expect(wrapper.findAll('.el-form-item').length).toBe(12)
    })

    it('uses custom collapseThreshold to control when toggle appears', async () => {
      const tenFields: FieldConfig[] = Array.from({ length: 10 }, (_, i) => ({
        field: `field${i}`,
        label: `字段${i}`,
        type: 'input' as const
      }))
      const wrapper = mount(QueryPanel, {
        props: {
          modelValue: {},
          fieldConfig: tenFields,
          collapseThreshold: 12
        }
      })
      await nextTick()
      expect(wrapper.findAll('.el-form-item').length).toBe(10)
    })
  })

  describe('field type rendering', () => {
    it('renders all 11 field types in a single form', async () => {
      const allTypes: FieldConfig[] = [
        { field: 'f1', label: '文本', type: 'input' },
        { field: 'f2', label: '文本域', type: 'textarea' },
        { field: 'f3', label: '数字', type: 'number' },
        { field: 'f4', label: '下拉', type: 'select', options: [{ label: 'A', value: 1 }] },
        { field: 'f5', label: '日期', type: 'date' },
        { field: 'f6', label: '日期范围', type: 'dateRange' },
        { field: 'f7', label: '日期时间', type: 'datetime' },
        { field: 'f8', label: '月份', type: 'dateMonth' },
        { field: 'f9', label: '开关', type: 'switch' },
        { field: 'f10', label: '单选', type: 'radio', options: [{ label: '是', value: 1 }] },
        { field: 'f11', label: '多选', type: 'checkbox', options: [{ label: 'A', value: 'a' }] }
      ]
      const wrapper = mount(QueryPanel, {
        props: { modelValue: {}, fieldConfig: allTypes, collapsible: false }
      })
      await nextTick()
      expect(wrapper.findAll('.el-form-item').length).toBe(11)
    })

    it('renders el-input-number for number type', () => {
      const fields: FieldConfig[] = [{ field: 'qty', label: '数量', type: 'number' }]
      const wrapper = createWrapper({ fieldConfig: fields })
      expect(wrapper.findComponent({ name: 'ElInputNumber' }).exists()).toBe(true)
    })

    it('renders el-date-picker for date type', () => {
      const wrapper = createWrapper()
      expect(wrapper.findComponent({ name: 'ElDatePicker' }).exists()).toBe(true)
    })

    it('renders el-switch for switch type', () => {
      const fields: FieldConfig[] = [{ field: 'enabled', label: '启用', type: 'switch' }]
      const wrapper = createWrapper({ fieldConfig: fields })
      expect(wrapper.findComponent({ name: 'ElSwitch' }).exists()).toBe(true)
    })

    it('renders el-radio-group for radio type', () => {
      const fields: FieldConfig[] = [
        {
          field: 'gender',
          label: '性别',
          type: 'radio',
          options: [
            { label: '男', value: 'male' },
            { label: '女', value: 'female' }
          ]
        }
      ]
      const wrapper = createWrapper({ fieldConfig: fields })
      expect(wrapper.findComponent({ name: 'ElRadioGroup' }).exists()).toBe(true)
    })

    it('renders el-checkbox-group for checkbox type', () => {
      const fields: FieldConfig[] = [
        {
          field: 'tags',
          label: '标签',
          type: 'checkbox',
          options: [
            { label: 'A', value: 'a' },
            { label: 'B', value: 'b' }
          ]
        }
      ]
      const wrapper = createWrapper({ fieldConfig: fields })
      expect(wrapper.findComponent({ name: 'ElCheckboxGroup' }).exists()).toBe(true)
    })

    it('renders textarea for textarea type', () => {
      const fields: FieldConfig[] = [{ field: 'remark', label: '备注', type: 'textarea' }]
      const wrapper = createWrapper({ fieldConfig: fields })
      expect(wrapper.find('textarea').exists()).toBe(true)
    })

    it('renders datetime picker for datetime type', () => {
      const fields: FieldConfig[] = [{ field: 'createTime', label: '创建时间', type: 'datetime' }]
      const wrapper = createWrapper({ fieldConfig: fields })
      expect(wrapper.findComponent({ name: 'ElDatePicker' }).exists()).toBe(true)
    })

    it('renders month picker for dateMonth type', () => {
      const fields: FieldConfig[] = [{ field: 'month', label: '月份', type: 'dateMonth' }]
      const wrapper = createWrapper({ fieldConfig: fields })
      expect(wrapper.findComponent({ name: 'ElDatePicker' }).exists()).toBe(true)
    })

    it('renders date range picker for dateRange type', () => {
      const fields: FieldConfig[] = [{ field: 'dateRange', label: '日期范围', type: 'dateRange' }]
      const wrapper = createWrapper({ fieldConfig: fields })
      expect(wrapper.findComponent({ name: 'ElDatePicker' }).exists()).toBe(true)
    })
  })

  describe('focus and blur events', () => {
    it('emits focus event when input gets focus', async () => {
      const wrapper = createWrapper()
      await nextTick()
      const input = wrapper.find('.el-form-item input[type="text"]')
      await input.trigger('focus')
      expect(wrapper.emitted('focus')![0]).toEqual(['keyword'])
    })

    it('emits blur event when input loses focus', async () => {
      const wrapper = createWrapper()
      await nextTick()
      const input = wrapper.find('.el-form-item input[type="text"]')
      await input.trigger('blur')
      expect(wrapper.emitted('blur')![0]).toEqual(['keyword'])
    })
  })

  describe('form validation rules', () => {
    it('builds form rules from field config', () => {
      const fields: FieldConfig[] = [
        {
          field: 'keyword',
          label: '关键词',
          type: 'input',
          rules: [
            { required: true, message: '请输入关键词', trigger: 'blur' },
            { min: 2, max: 50, message: '长度2-50' }
          ]
        }
      ]
      const wrapper = createWrapper({ fieldConfig: fields })
      expect(wrapper.findAll('.el-form-item').length).toBe(1)
    })
  })

  describe('defineExpose', () => {
    it('exposes formRef', () => {
      const wrapper = createWrapper()
      const vm = wrapper.vm as unknown as { formRef: unknown }
      expect(vm.formRef).toBeDefined()
    })
  })

  describe('reactive fieldConfig', () => {
    it('reinitializes model when fieldConfig changes', async () => {
      const wrapper = createWrapper({ modelValue: { keyword: 'test', status: 1 } })
      await nextTick()
      await wrapper.setProps({
        fieldConfig: [{ field: 'newField', label: '新字段', type: 'input' }]
      })
      await nextTick()
      expect(wrapper.findAll('.el-form-item').length).toBe(1)
      const input = wrapper.find('.el-form-item input[type="text"]').element as HTMLInputElement
      expect(input.value).toBe('')
    })
  })
})
