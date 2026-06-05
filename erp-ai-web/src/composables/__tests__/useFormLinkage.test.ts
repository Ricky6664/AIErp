import { describe, it, expect } from 'vitest'
import { reactive, nextTick } from 'vue'
import { useFormLinkage, buildConditionFn, parseLinkageJson } from '@/composables/useFormLinkage'
import type { FormFieldConfig } from '@/types/master-form'
import type { FieldLinkageRule, LinkageRuleConfig } from '@/types/list-table'

function makeFieldConfig(overrides: Partial<FormFieldConfig> = {}): FormFieldConfig {
  return {
    field: 'testField',
    fieldType: 'text',
    ...overrides
  }
}

function makeFieldConfigs(): FormFieldConfig[] {
  return [
    makeFieldConfig({
      field: 'category',
      fieldType: 'select',
      label: '类别',
      linkages: [
        {
          triggerField: 'category',
          targetField: 'subCategory',
          action: 'show',
          condition: (v: unknown) => v === 'A'
        },
        {
          triggerField: 'category',
          targetField: 'subCategory',
          action: 'hide',
          condition: (v: unknown) => v === 'B'
        },
        {
          triggerField: 'category',
          targetField: 'price',
          action: 'setValue',
          params: { value: 100 },
          condition: (v: unknown) => v === 'premium'
        },
        {
          triggerField: 'category',
          targetField: 'discount',
          action: 'disable',
          condition: (v: unknown) => v === 'locked'
        },
        {
          triggerField: 'category',
          targetField: 'discount',
          action: 'enable',
          condition: (v: unknown) => v === 'unlocked'
        }
      ]
    }),
    makeFieldConfig({
      field: 'subCategory',
      fieldType: 'select',
      label: '子类别',
      visible: false,
      linkages: [
        {
          triggerField: 'subCategory',
          targetField: 'note',
          action: 'setValue',
          params: { value: 'auto-filled' }
        }
      ]
    }),
    makeFieldConfig({ field: 'price', fieldType: 'number', label: '价格' }),
    makeFieldConfig({ field: 'discount', fieldType: 'number', label: '折扣' }),
    makeFieldConfig({ field: 'note', fieldType: 'textarea', label: '备注' })
  ]
}

describe('useFormLinkage', () => {
  describe('evaluateCondition', () => {
    it('returns true when no condition is set', () => {
      const { evaluateCondition } = useFormLinkage()
      const linkage: FieldLinkageRule = {
        triggerField: 'a',
        targetField: 'b',
        action: 'show'
      }
      expect(evaluateCondition(linkage, 'any')).toBe(true)
    })

    it('returns true when condition matches', () => {
      const { evaluateCondition } = useFormLinkage()
      const linkage: FieldLinkageRule = {
        triggerField: 'a',
        targetField: 'b',
        action: 'show',
        condition: (v: unknown) => v === 'expected'
      }
      expect(evaluateCondition(linkage, 'expected')).toBe(true)
    })

    it('returns false when condition does not match', () => {
      const { evaluateCondition } = useFormLinkage()
      const linkage: FieldLinkageRule = {
        triggerField: 'a',
        targetField: 'b',
        action: 'show',
        condition: (v: unknown) => v === 'expected'
      }
      expect(evaluateCondition(linkage, 'unexpected')).toBe(false)
    })

    it('returns false when condition throws', () => {
      const { evaluateCondition } = useFormLinkage()
      const linkage: FieldLinkageRule = {
        triggerField: 'a',
        targetField: 'b',
        action: 'show',
        condition: () => {
          throw new Error('bad')
        }
      }
      expect(evaluateCondition(linkage, 'x')).toBe(false)
    })
  })

  describe('processLinkages', () => {
    it('returns empty result for field with no linkages', () => {
      const { processLinkages } = useFormLinkage()
      const configs = [makeFieldConfig({ field: 'name', fieldType: 'text' })]
      const result = processLinkages('name', 'test', configs)
      expect(result.setValues).toHaveLength(0)
      expect(result.visibilityChanges).toHaveLength(0)
      expect(result.disabledChanges).toHaveLength(0)
      expect(result.setOptions).toHaveLength(0)
    })

    it('returns show visibility change when condition passes', () => {
      const { processLinkages } = useFormLinkage()
      const configs = makeFieldConfigs()
      const result = processLinkages('category', 'A', configs)
      expect(result.visibilityChanges).toContainEqual({
        field: 'subCategory',
        visible: true
      })
    })

    it('returns hide visibility change when condition passes', () => {
      const { processLinkages } = useFormLinkage()
      const configs = makeFieldConfigs()
      const result = processLinkages('category', 'B', configs)
      expect(result.visibilityChanges).toContainEqual({
        field: 'subCategory',
        visible: false
      })
    })

    it('returns setValue when condition passes', () => {
      const { processLinkages } = useFormLinkage()
      const configs = makeFieldConfigs()
      const result = processLinkages('category', 'premium', configs)
      expect(result.setValues).toContainEqual({
        field: 'price',
        value: 100
      })
    })

    it('returns disable when condition passes', () => {
      const { processLinkages } = useFormLinkage()
      const configs = makeFieldConfigs()
      const result = processLinkages('category', 'locked', configs)
      expect(result.disabledChanges).toContainEqual({
        field: 'discount',
        disabled: true
      })
    })

    it('returns enable when condition passes', () => {
      const { processLinkages } = useFormLinkage()
      const configs = makeFieldConfigs()
      const result = processLinkages('category', 'unlocked', configs)
      expect(result.disabledChanges).toContainEqual({
        field: 'discount',
        disabled: false
      })
    })

    it('does not include results for non-matching triggerField', () => {
      const { processLinkages } = useFormLinkage()
      const configs = makeFieldConfigs()
      const result = processLinkages('otherField', 'A', configs)
      expect(result.visibilityChanges).toHaveLength(0)
      expect(result.setValues).toHaveLength(0)
    })

    it('handles setOptions action', () => {
      const { processLinkages } = useFormLinkage()
      const configs: FormFieldConfig[] = [
        makeFieldConfig({
          field: 'type',
          fieldType: 'select',
          linkages: [
            {
              triggerField: 'type',
              targetField: 'detail',
              action: 'setOptions',
              params: { options: [{ label: '新选项', value: 'x' }] }
            }
          ]
        }),
        makeFieldConfig({ field: 'detail', fieldType: 'select' })
      ]
      const result = processLinkages('type', 'change', configs)
      expect(result.setOptions).toHaveLength(1)
      expect(result.setOptions[0].field).toBe('detail')
    })

    it('skips setOptions when params.options is not an array', () => {
      const { processLinkages } = useFormLinkage()
      const configs: FormFieldConfig[] = [
        makeFieldConfig({
          field: 'type',
          fieldType: 'select',
          linkages: [
            {
              triggerField: 'type',
              targetField: 'detail',
              action: 'setOptions',
              params: {}
            }
          ]
        }),
        makeFieldConfig({ field: 'detail', fieldType: 'select' })
      ]
      const result = processLinkages('type', 'change', configs)
      expect(result.setOptions).toHaveLength(0)
    })
  })

  describe('applyLinkageResult', () => {
    it('applies visibility changes', () => {
      const { state, applyLinkageResult } = useFormLinkage()
      const localData: Record<string, unknown> = {}
      applyLinkageResult(
        {
          visibilityChanges: [{ field: 'a', visible: false }],
          disabledChanges: [],
          setValues: [],
          setOptions: []
        },
        localData
      )
      expect(state.visibility['a']).toBe(false)
    })

    it('applies disabled changes', () => {
      const { state, applyLinkageResult } = useFormLinkage()
      const localData: Record<string, unknown> = {}
      applyLinkageResult(
        {
          visibilityChanges: [],
          disabledChanges: [{ field: 'a', disabled: true }],
          setValues: [],
          setOptions: []
        },
        localData
      )
      expect(state.disabled['a']).toBe(true)
    })

    it('applies setValues and returns changed fields', () => {
      const { applyLinkageResult } = useFormLinkage()
      const localData: Record<string, unknown> = {}
      const changed = applyLinkageResult(
        {
          visibilityChanges: [],
          disabledChanges: [],
          setValues: [{ field: 'x', value: 42 }],
          setOptions: []
        },
        localData
      )
      expect(localData['x']).toBe(42)
      expect(changed).toContain('x')
    })
  })

  describe('executeLinkages (full pipeline with cascade)', () => {
    it('processes show/hide and cascading setValue linkages', () => {
      const { executeLinkages, state } = useFormLinkage()
      const configs = makeFieldConfigs()
      const localData: Record<string, unknown> = {}

      // category 'A' → show subCategory
      executeLinkages('category', 'A', configs, localData)
      expect(state.visibility['subCategory']).toBe(true)

      // subCategory change → cascade setValue to 'note'
      executeLinkages('subCategory', 'any', configs, localData)
      expect(localData['note']).toBe('auto-filled')
    })

    it('prevents infinite recursion on circular linkages', () => {
      const { executeLinkages } = useFormLinkage()
      const configs: FormFieldConfig[] = [
        makeFieldConfig({
          field: 'a',
          fieldType: 'text',
          linkages: [
            {
              triggerField: 'a',
              targetField: 'b',
              action: 'setValue',
              params: { value: 'from-a' }
            }
          ]
        }),
        makeFieldConfig({
          field: 'b',
          fieldType: 'text',
          linkages: [
            {
              triggerField: 'b',
              targetField: 'a',
              action: 'setValue',
              params: { value: 'from-b' }
            }
          ]
        })
      ]
      const localData: Record<string, unknown> = {}
      // Should not hang
      executeLinkages('a', 'test', configs, localData)
      expect(true).toBe(true)
    })
  })

  describe('isFieldVisible', () => {
    it('returns false when fieldConfig.visible is false', () => {
      const { isFieldVisible } = useFormLinkage()
      const config = makeFieldConfig({ field: 'a', fieldType: 'text', visible: false })
      expect(isFieldVisible(config)).toBe(false)
    })

    it('returns false when linkage hides the field', () => {
      const { isFieldVisible, state } = useFormLinkage()
      state.visibility['a'] = false
      const config = makeFieldConfig({ field: 'a', fieldType: 'text', visible: true })
      expect(isFieldVisible(config)).toBe(false)
    })

    it('returns true when visible and not hidden by linkage', () => {
      const { isFieldVisible } = useFormLinkage()
      const config = makeFieldConfig({ field: 'a', fieldType: 'text', visible: true })
      expect(isFieldVisible(config)).toBe(true)
    })

    it('returns true by default', () => {
      const { isFieldVisible } = useFormLinkage()
      const config = makeFieldConfig({ field: 'a', fieldType: 'text' })
      expect(isFieldVisible(config)).toBe(true)
    })
  })

  describe('isFieldDisabled', () => {
    it('returns true when linkage disables the field', () => {
      const { isFieldDisabled, state } = useFormLinkage()
      state.disabled['a'] = true
      const config = makeFieldConfig({ field: 'a', fieldType: 'text' })
      expect(isFieldDisabled(config)).toBe(true)
    })

    it('returns false by default', () => {
      const { isFieldDisabled } = useFormLinkage()
      const config = makeFieldConfig({ field: 'a', fieldType: 'text' })
      expect(isFieldDisabled(config)).toBe(false)
    })
  })

  describe('reset', () => {
    it('clears all visibility and disabled state', () => {
      const { state, reset } = useFormLinkage()
      state.visibility['a'] = false
      state.disabled['b'] = true
      reset()
      expect(state.visibility['a']).toBeUndefined()
      expect(state.disabled['b']).toBeUndefined()
    })
  })

  describe('applyOptionsResult', () => {
    it('updates options on matching field config', () => {
      const { applyOptionsResult } = useFormLinkage()
      const configs: FormFieldConfig[] = [
        makeFieldConfig({
          field: 'detail',
          fieldType: 'select',
          options: [{ label: '旧', value: 'old' }]
        })
      ]
      applyOptionsResult(
        {
          visibilityChanges: [],
          disabledChanges: [],
          setValues: [],
          setOptions: [{ field: 'detail', options: [{ label: '新', value: 'new' }] }]
        },
        configs
      )
      expect(configs[0].options).toEqual([{ label: '新', value: 'new' }])
    })

    it('does nothing when target field not found', () => {
      const { applyOptionsResult } = useFormLinkage()
      const configs: FormFieldConfig[] = [makeFieldConfig({ field: 'a', fieldType: 'text' })]
      applyOptionsResult(
        {
          visibilityChanges: [],
          disabledChanges: [],
          setValues: [],
          setOptions: [{ field: 'nonexistent', options: [] }]
        },
        configs
      )
      // No error thrown
      expect(configs[0].options).toBeUndefined()
    })
  })
})

describe('watchFieldLinkages (监听触发字段变化)', () => {
  function makeLinkageConfigs(): FormFieldConfig[] {
    return [
      makeFieldConfig({
        field: 'category',
        fieldType: 'select',
        linkages: [
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
            action: 'disable'
          }
        ]
      }),
      makeFieldConfig({ field: 'subCategory', fieldType: 'select' }),
      makeFieldConfig({
        field: 'price',
        fieldType: 'number',
        linkages: [
          {
            triggerField: 'price',
            targetField: 'discount',
            action: 'setValue',
            params: { value: 10 }
          }
        ]
      }),
      makeFieldConfig({ field: 'discount', fieldType: 'number' }),
      makeFieldConfig({ field: 'note', fieldType: 'textarea' })
    ]
  }

  it('returns a stop function', () => {
    const { watchFieldLinkages } = useFormLinkage()
    const formData = reactive<Record<string, unknown>>({ category: '' })
    const stop = watchFieldLinkages(formData, [])
    expect(typeof stop).toBe('function')
    stop()
  })

  it('watches trigger field and executes linkages on value change', async () => {
    const { watchFieldLinkages, state } = useFormLinkage()
    const configs = makeLinkageConfigs()
    const formData = reactive<Record<string, unknown>>({ category: 'B' })
    const stop = watchFieldLinkages(formData, configs)
    expect(state.visibility['subCategory']).toBeUndefined()

    formData.category = 'A'
    await nextTick()

    expect(state.visibility['subCategory']).toBe(true)
    stop()
  })

  it('handles cascade linkages via setValue', async () => {
    const { watchFieldLinkages } = useFormLinkage()
    const configs = makeLinkageConfigs()
    const formData = reactive<Record<string, unknown>>({
      category: '',
      price: 0,
      discount: 0
    })
    const stop = watchFieldLinkages(formData, configs)

    formData.category = 'premium'
    await nextTick()

    expect(formData.price).toBe(100)
    expect(formData.discount).toBe(10)
    stop()
  })

  it('stop function prevents further linkage execution', async () => {
    const { watchFieldLinkages, state } = useFormLinkage()
    const configs: FormFieldConfig[] = [
      makeFieldConfig({
        field: 'type',
        fieldType: 'select',
        linkages: [{ triggerField: 'type', targetField: 'detail', action: 'show' }]
      }),
      makeFieldConfig({ field: 'detail', fieldType: 'text' })
    ]
    const formData = reactive<Record<string, unknown>>({ type: '' })
    const stop = watchFieldLinkages(formData, configs)

    formData.type = 'test'
    await nextTick()
    expect(state.visibility['detail']).toBe(true)

    stop()
    state.visibility['detail'] = false

    formData.type = 'another'
    await nextTick()
    expect(state.visibility['detail']).toBe(false)
  })

  it('ignores fields without linkages', () => {
    const { watchFieldLinkages } = useFormLinkage()
    const configs: FormFieldConfig[] = [makeFieldConfig({ field: 'name', fieldType: 'text' })]
    const formData = reactive<Record<string, unknown>>({ name: '' })
    const stop = watchFieldLinkages(formData, configs)
    expect(typeof stop).toBe('function')
    stop()
  })

  it('supports immediate option', () => {
    const { watchFieldLinkages, state } = useFormLinkage()
    const configs: FormFieldConfig[] = [
      makeFieldConfig({
        field: 'category',
        fieldType: 'select',
        linkages: [
          {
            triggerField: 'category',
            targetField: 'subCategory',
            action: 'show',
            condition: (v: unknown) => v === 'A'
          }
        ]
      }),
      makeFieldConfig({ field: 'subCategory', fieldType: 'select' })
    ]
    const formData = reactive<Record<string, unknown>>({ category: 'A' })
    const stop = watchFieldLinkages(formData, configs, { immediate: true })
    expect(state.visibility['subCategory']).toBe(true)
    stop()
  })
})

describe('buildConditionFn', () => {
  it('returns true for eq match', () => {
    const fn = buildConditionFn({ operator: 'eq', value: 'hello' })
    expect(fn('hello')).toBe(true)
    expect(fn('world')).toBe(false)
  })

  it('returns true for neq match', () => {
    const fn = buildConditionFn({ operator: 'neq', value: 'hello' })
    expect(fn('world')).toBe(true)
    expect(fn('hello')).toBe(false)
  })

  it('returns true for gt match', () => {
    const fn = buildConditionFn({ operator: 'gt', value: 100 })
    expect(fn(200)).toBe(true)
    expect(fn(50)).toBe(false)
    expect(fn(100)).toBe(false)
  })

  it('returns true for gte match', () => {
    const fn = buildConditionFn({ operator: 'gte', value: 100 })
    expect(fn(200)).toBe(true)
    expect(fn(100)).toBe(true)
    expect(fn(50)).toBe(false)
  })

  it('returns true for lt match', () => {
    const fn = buildConditionFn({ operator: 'lt', value: 100 })
    expect(fn(50)).toBe(true)
    expect(fn(100)).toBe(false)
    expect(fn(200)).toBe(false)
  })

  it('returns true for lte match', () => {
    const fn = buildConditionFn({ operator: 'lte', value: 100 })
    expect(fn(50)).toBe(true)
    expect(fn(100)).toBe(true)
    expect(fn(200)).toBe(false)
  })

  it('returns true for in match', () => {
    const fn = buildConditionFn({ operator: 'in', value: ['a', 'b', 'c'] })
    expect(fn('b')).toBe(true)
    expect(fn('d')).toBe(false)
  })

  it('returns true for notIn match', () => {
    const fn = buildConditionFn({ operator: 'notIn', value: ['a', 'b'] })
    expect(fn('c')).toBe(true)
    expect(fn('a')).toBe(false)
  })

  it('returns true for isEmpty match', () => {
    const fn = buildConditionFn({ operator: 'isEmpty' })
    expect(fn('')).toBe(true)
    expect(fn(null)).toBe(true)
    expect(fn(undefined)).toBe(true)
    expect(fn('hello')).toBe(false)
    expect(fn(0)).toBe(false)
  })

  it('returns true for isNotEmpty match', () => {
    const fn = buildConditionFn({ operator: 'isNotEmpty' })
    expect(fn('hello')).toBe(true)
    expect(fn(0)).toBe(true)
    expect(fn('')).toBe(false)
    expect(fn(null)).toBe(false)
    expect(fn(undefined)).toBe(false)
  })

  it('returns true for startsWith match', () => {
    const fn = buildConditionFn({ operator: 'startsWith', value: 'TEST-' })
    expect(fn('TEST-001')).toBe(true)
    expect(fn('NOTEST-001')).toBe(false)
  })

  it('returns true for endsWith match', () => {
    const fn = buildConditionFn({ operator: 'endsWith', value: '.pdf' })
    expect(fn('document.pdf')).toBe(true)
    expect(fn('document.doc')).toBe(false)
  })

  it('returns true for contains match', () => {
    const fn = buildConditionFn({ operator: 'contains', value: 'urgent' })
    expect(fn('this is urgent task')).toBe(true)
    expect(fn('this is normal')).toBe(false)
  })

  it('returns false for unknown operator', () => {
    const fn = buildConditionFn({ operator: 'unknown' as never })
    expect(fn('anything')).toBe(false)
  })
})

describe('parseLinkageJson', () => {
  const sampleRules: LinkageRuleConfig[] = [
    {
      triggerField: 'category',
      targetField: 'subCategory',
      action: 'show',
      condition: { operator: 'eq', value: 'A' }
    },
    {
      triggerField: 'category',
      targetField: 'price',
      action: 'setValue',
      condition: { operator: 'eq', value: 'premium' },
      params: { value: 100 }
    },
    {
      triggerField: 'type',
      targetField: 'detail',
      action: 'setOptions',
      params: { options: [{ label: '新选项', value: 'new' }] }
    }
  ]

  it('parses LinkageRuleConfig[] into FieldLinkageRule[]', () => {
    const result = parseLinkageJson(sampleRules)
    expect(result).toHaveLength(3)
    expect(result[0].triggerField).toBe('category')
    expect(result[0].targetField).toBe('subCategory')
    expect(result[0].action).toBe('show')
    expect(typeof result[0].condition).toBe('function')
  })

  it('converts condition config to executable function', () => {
    const result = parseLinkageJson(sampleRules)
    expect(result[0].condition!('A')).toBe(true)
    expect(result[0].condition!('B')).toBe(false)
  })

  it('preserves params', () => {
    const result = parseLinkageJson(sampleRules)
    expect(result[1].params).toEqual({ value: 100 })
  })

  it('returns rule with undefined condition when no condition config', () => {
    const result = parseLinkageJson(sampleRules)
    expect(result[2].condition).toBeUndefined()
  })

  it('throws error for non-array input', () => {
    expect(() => parseLinkageJson(null as unknown as LinkageRuleConfig[])).toThrow(
      '联动规则配置必须是数组'
    )
  })

  it('throws error for missing required fields', () => {
    const badRules: LinkageRuleConfig[] = [
      { triggerField: '', targetField: '', action: '' as never }
    ]
    expect(() => parseLinkageJson(badRules)).toThrow('联动规则缺少必填字段')
  })

  it('handles empty array', () => {
    const result = parseLinkageJson([])
    expect(result).toHaveLength(0)
  })
})
