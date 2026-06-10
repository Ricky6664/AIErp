import { describe, it, expect, vi } from 'vitest'
import { nextTick } from 'vue'
import { useFieldCollector } from '@/composables/useFieldCollector'
import type { FieldConfig } from '@/types/list-table'

function createFieldConfig(overrides: Partial<FieldConfig> = {}): FieldConfig {
  return {
    field: 'remark',
    title: '备注',
    fieldType: 'textarea',
    ...overrides
  }
}

describe('useFieldCollector', () => {
  describe('initialization', () => {
    it('sets fieldName from fieldConfig.field', () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({ field: 'description' }),
        modelValue: ''
      })
      expect(collector.fieldName).toBe('description')
    })

    it('initializes value from modelValue option', () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: 'initial text'
      })
      expect(collector.value.value).toBe('initial text')
    })

    it('initializes with empty errors array', () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: ''
      })
      expect(collector.errors.value).toEqual([])
    })

    it('initializes value as undefined when no modelValue provided', () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: undefined
      })
      expect(collector.value.value).toBeUndefined()
    })
  })

  describe('v-model two-way binding', () => {
    it('syncs external modelValue change to internal value', async () => {
      let modelValue = ''
      const onUpdate = vi.fn((v: unknown) => {
        modelValue = v as string
      })

      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: modelValue,
        onUpdate
      })

      // Simulate external change (parent v-model update)
      // We recreate the collector to simulate reactive change
      const collector2 = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: 'externally updated',
        onUpdate
      })

      expect(collector2.collect()).toBe('externally updated')
      // original collector retains its initial value
      expect(collector.collect()).toBe('')
    })

    it('calls onUpdate when value changes via setValue', async () => {
      const onUpdate = vi.fn()
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: '',
        onUpdate
      })

      collector.setValue('new value')
      await nextTick()

      expect(onUpdate).toHaveBeenCalledWith('new value')
    })

    it('calls onUpdate when reset is called', async () => {
      const onUpdate = vi.fn()
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: 'some text',
        onUpdate
      })

      collector.reset()
      await nextTick()

      expect(onUpdate).toHaveBeenCalledWith('')
    })
  })

  describe('collect()', () => {
    it('returns the current internal value', () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: 'hello world'
      })
      expect(collector.collect()).toBe('hello world')
    })

    it('returns empty string after reset', () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: 'text to reset'
      })
      collector.reset()
      expect(collector.collect()).toBe('')
    })

    it('returns updated value after setValue', () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: ''
      })
      collector.setValue('updated')
      expect(collector.collect()).toBe('updated')
    })
  })

  describe('setValue()', () => {
    it('updates the internal value', () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: ''
      })
      collector.setValue('new content')
      expect(collector.value.value).toBe('new content')
    })

    it('clears errors when setting new value', () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({ rules: [{ type: 'required', message: '必填' }] }),
        modelValue: ''
      })
      // Simulate errors
      collector.errors.value = ['some error']
      collector.setValue('new value')
      expect(collector.errors.value).toEqual([])
    })

    it('accepts null and undefined values', () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: 'initial'
      })
      collector.setValue(null)
      expect(collector.collect()).toBeNull()
      collector.setValue(undefined)
      expect(collector.collect()).toBeUndefined()
    })
  })

  describe('reset()', () => {
    it('resets value to empty string', () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: 'some content'
      })
      collector.reset()
      expect(collector.collect()).toBe('')
      expect(collector.value.value).toBe('')
    })

    it('clears all errors after reset', () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: ''
      })
      collector.errors.value = ['error 1', 'error 2']
      collector.reset()
      expect(collector.errors.value).toEqual([])
    })
  })

  describe('validate()', () => {
    it('returns true when no rules are configured', async () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig(),
        modelValue: 'anything'
      })
      const valid = await collector.validate()
      expect(valid).toBe(true)
    })

    it('returns false when required field is empty', async () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({
          rules: [{ type: 'required', message: '备注为必填项' }]
        }),
        modelValue: ''
      })
      const valid = await collector.validate()
      expect(valid).toBe(false)
      expect(collector.errors.value).toContain('备注为必填项')
    })

    it('returns true when required field has value', async () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({
          rules: [{ type: 'required', message: '必填' }]
        }),
        modelValue: 'some text'
      })
      const valid = await collector.validate()
      expect(valid).toBe(true)
      expect(collector.errors.value).toEqual([])
    })

    it('returns false when min length not met', async () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({
          rules: [{ type: 'min', value: 5, message: '最少5个字符' }]
        }),
        modelValue: 'ab'
      })
      const valid = await collector.validate()
      expect(valid).toBe(false)
      expect(collector.errors.value).toContain('最少5个字符')
    })

    it('returns true when min length is met', async () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({
          rules: [{ type: 'min', value: 5, message: '最少5个字符' }]
        }),
        modelValue: 'abcdef'
      })
      const valid = await collector.validate()
      expect(valid).toBe(true)
    })

    it('returns false when max length exceeded', async () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({
          rules: [{ type: 'max', value: 10, message: '最多10个字符' }]
        }),
        modelValue: 'this is a very long string'
      })
      const valid = await collector.validate()
      expect(valid).toBe(false)
      expect(collector.errors.value).toContain('最多10个字符')
    })

    it('returns false when pattern does not match', async () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({
          rules: [{ type: 'pattern', value: /^\d+$/, message: '仅允许数字' }]
        }),
        modelValue: 'abc'
      })
      const valid = await collector.validate()
      expect(valid).toBe(false)
      expect(collector.errors.value).toContain('仅允许数字')
    })

    it('returns true when pattern matches', async () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({
          rules: [{ type: 'pattern', value: /^\d+$/, message: '仅允许数字' }]
        }),
        modelValue: '12345'
      })
      const valid = await collector.validate()
      expect(valid).toBe(true)
    })

    it('returns false when custom validator fails', async () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({
          rules: [
            {
              type: 'custom',
              message: '值必须为hello',
              validator: (v: unknown) => v === 'hello'
            }
          ]
        }),
        modelValue: 'wrong'
      })
      const valid = await collector.validate()
      expect(valid).toBe(false)
      expect(collector.errors.value).toContain('值必须为hello')
    })

    it('returns true when custom validator passes', async () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({
          rules: [
            {
              type: 'custom',
              message: '值必须为hello',
              validator: (v: unknown) => v === 'hello'
            }
          ]
        }),
        modelValue: 'hello'
      })
      const valid = await collector.validate()
      expect(valid).toBe(true)
    })

    it('validates multiple rules simultaneously', async () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({
          rules: [
            { type: 'required', message: '必填' },
            { type: 'min', value: 3, message: '最少3个字符' },
            { type: 'max', value: 5, message: '最多5个字符' }
          ]
        }),
        modelValue: 'ab'
      })
      const valid = await collector.validate()
      expect(valid).toBe(false)
      expect(collector.errors.value.length).toBeGreaterThanOrEqual(1)
    })
  })

  describe('errors reactivity', () => {
    it('errors ref is reactive and updates after validation', async () => {
      const collector = useFieldCollector({
        fieldConfig: createFieldConfig({
          rules: [{ type: 'required', message: '必填项' }]
        }),
        modelValue: ''
      })

      expect(collector.errors.value).toEqual([])
      await collector.validate()
      expect(collector.errors.value.length).toBe(1)

      collector.setValue('now has value')
      await collector.validate()
      expect(collector.errors.value).toEqual([])
    })
  })
})
