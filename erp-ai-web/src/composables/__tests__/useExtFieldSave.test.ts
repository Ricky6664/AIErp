import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useExtFieldSave, extractExtensionFields } from '@/composables/useExtFieldSave'
import { saveExtensionFields, updateExtensionFields } from '@/api/modules/fieldConfig'
import type { ExtFieldSaveResult } from '@/api/modules/fieldConfig'
import type { FieldConfig } from '@/types/list-table'

const mockSaveResponse = { id: 'ext-001', version: 1 }
const mockUpdateResponse = { id: 'ext-001', version: 2 }

vi.mock('@/api/modules/fieldConfig')

beforeEach(() => {
  vi.mocked(saveExtensionFields).mockResolvedValue(mockSaveResponse)
  vi.mocked(updateExtensionFields).mockResolvedValue(mockUpdateResponse)
})

function makeFieldConfig(overrides: Partial<FieldConfig> = {}): FieldConfig {
  return {
    field: 'ext_name',
    title: '扩展名称',
    fieldType: 'text',
    isExtension: true,
    ...overrides
  }
}

function makeMainFieldConfig(overrides: Partial<FieldConfig> = {}): FieldConfig {
  return {
    field: 'name',
    title: '名称',
    fieldType: 'text',
    ...overrides
  }
}

describe('extractExtensionFields', () => {
  it('returns only extension field values', () => {
    const formData = { name: 'main value', ext_name: 'ext value', ext_note: 'note' }
    const configs: FieldConfig[] = [
      makeMainFieldConfig({ field: 'name' }),
      makeFieldConfig({ field: 'ext_name' }),
      makeFieldConfig({ field: 'ext_note' })
    ]

    const result = extractExtensionFields(formData, configs)

    expect(result).toEqual({ ext_name: 'ext value', ext_note: 'note' })
  })

  it('returns empty object when no extension fields configured', () => {
    const formData = { name: 'test', age: 25 }
    const configs: FieldConfig[] = [
      makeMainFieldConfig({ field: 'name' }),
      makeMainFieldConfig({ field: 'age', fieldType: 'number' })
    ]

    const result = extractExtensionFields(formData, configs)

    expect(result).toEqual({})
  })

  it('returns empty object when form data has no matching extension keys', () => {
    const formData = { name: 'test' }
    const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

    const result = extractExtensionFields(formData, configs)

    expect(result).toEqual({})
  })

  it('handles empty field configs array', () => {
    const result = extractExtensionFields({ name: 'test' }, [])
    expect(result).toEqual({})
  })

  it('handles empty form data', () => {
    const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]
    const result = extractExtensionFields({}, configs)
    expect(result).toEqual({})
  })
})

describe('useExtFieldSave', () => {
  describe('initialization', () => {
    it('initializes with default state', () => {
      const { state } = useExtFieldSave()
      expect(state.loading).toBe(false)
      expect(state.error).toBeNull()
      expect(state.data).toBeNull()
    })

    it('isSuccess is false initially', () => {
      const { isSuccess } = useExtFieldSave()
      expect(isSuccess.value).toBe(false)
    })
  })

  describe('saveExtFields', () => {
    it('saves extension fields and returns result', async () => {
      const { saveExtFields, state } = useExtFieldSave()

      const formData = { name: 'main', ext_name: 'ext value' }
      const configs: FieldConfig[] = [
        makeMainFieldConfig({ field: 'name' }),
        makeFieldConfig({ field: 'ext_name' })
      ]

      const result = await saveExtFields(formData, configs, 'test_view', 'rec-001')

      expect(result).toEqual(mockSaveResponse)
      expect(state.data).toEqual(mockSaveResponse)
      expect(state.error).toBeNull()
    })

    it('returns null when no extension fields present', async () => {
      const { saveExtFields, state } = useExtFieldSave()

      const formData = { name: 'only main' }
      const configs: FieldConfig[] = [makeMainFieldConfig({ field: 'name' })]

      const result = await saveExtFields(formData, configs, 'test_view', 'rec-001')

      expect(result).toBeNull()
      expect(state.loading).toBe(false)
    })

    it('uses update when extRecordId provided', async () => {
      const { saveExtFields } = useExtFieldSave()

      const formData = { ext_name: 'updated' }
      const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

      const result = await saveExtFields(formData, configs, 'test_view', 'rec-001', 'ext-001')

      expect(result).toEqual(mockUpdateResponse)
      expect(updateExtensionFields).toHaveBeenCalledWith('ext-001', {
        viewCode: 'test_view',
        recordId: 'rec-001',
        extFields: { ext_name: 'updated' }
      })
    })

    it('sets loading=true during save', async () => {
      vi.mocked(saveExtensionFields).mockImplementationOnce(
        () => new Promise<ExtFieldSaveResult>((r) => setTimeout(r, 10))
      )
      const { saveExtFields, state } = useExtFieldSave()

      const formData = { ext_name: 'test' }
      const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

      const promise = saveExtFields(formData, configs, 'test_view', 'rec-001')
      expect(state.loading).toBe(true)

      await promise
      expect(state.loading).toBe(false)
    })
  })

  describe('error handling', () => {
    it('catches exception and sets state.error', async () => {
      vi.mocked(saveExtensionFields).mockRejectedValueOnce(new Error('Network error'))
      const { saveExtFields, state } = useExtFieldSave()

      const formData = { ext_name: 'test' }
      const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

      const result = await saveExtFields(formData, configs, 'test_view', 'rec-001')

      expect(result).toBeNull()
      expect(state.error).toBeInstanceOf(Error)
      expect(state.error!.message).toBe('Network error')
    })

    it('converts non-Error throws to Error objects', async () => {
      vi.mocked(saveExtensionFields).mockRejectedValueOnce('string error')
      const { saveExtFields, state } = useExtFieldSave()

      const formData = { ext_name: 'test' }
      const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

      await saveExtFields(formData, configs, 'test_view', 'rec-001')

      expect(state.error).toBeInstanceOf(Error)
      expect(state.error!.message).toBe('string error')
    })

    it('isSuccess is false after error', async () => {
      vi.mocked(saveExtensionFields).mockRejectedValueOnce(new Error('fail'))
      const { saveExtFields, isSuccess } = useExtFieldSave()

      const formData = { ext_name: 'test' }
      const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

      await saveExtFields(formData, configs, 'test_view', 'rec-001')
      expect(isSuccess.value).toBe(false)
    })
  })

  describe('callbacks', () => {
    it('calls onSuccess with result data', async () => {
      const onSuccess = vi.fn()
      const { saveExtFields } = useExtFieldSave({ onSuccess })

      const formData = { ext_name: 'test' }
      const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

      await saveExtFields(formData, configs, 'test_view', 'rec-001')

      expect(onSuccess).toHaveBeenCalledTimes(1)
      expect(onSuccess).toHaveBeenCalledWith(mockSaveResponse)
    })

    it('calls onError with error on failure', async () => {
      const error = new Error('fail')
      vi.mocked(saveExtensionFields).mockRejectedValueOnce(error)
      const onError = vi.fn()
      const { saveExtFields } = useExtFieldSave({ onError })

      const formData = { ext_name: 'test' }
      const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

      await saveExtFields(formData, configs, 'test_view', 'rec-001')

      expect(onError).toHaveBeenCalledTimes(1)
      expect(onError).toHaveBeenCalledWith(error)
    })

    it('does NOT call onSuccess when save throws', async () => {
      vi.mocked(saveExtensionFields).mockRejectedValueOnce(new Error('fail'))
      const onSuccess = vi.fn()
      const { saveExtFields } = useExtFieldSave({ onSuccess })

      const formData = { ext_name: 'test' }
      const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

      await saveExtFields(formData, configs, 'test_view', 'rec-001')
      expect(onSuccess).not.toHaveBeenCalled()
    })

    it('does NOT call onSuccess when no extension fields', async () => {
      const onSuccess = vi.fn()
      const { saveExtFields } = useExtFieldSave({ onSuccess })

      const formData = { name: 'main only' }
      const configs: FieldConfig[] = [makeMainFieldConfig({ field: 'name' })]

      await saveExtFields(formData, configs, 'test_view', 'rec-001')
      expect(onSuccess).not.toHaveBeenCalled()
    })
  })

  describe('reset', () => {
    it('resets state to initial values', async () => {
      const { saveExtFields, state, isSuccess, reset } = useExtFieldSave()

      const formData = { ext_name: 'test' }
      const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

      await saveExtFields(formData, configs, 'test_view', 'rec-001')
      expect(state.data).not.toBeNull()
      expect(isSuccess.value).toBe(true)

      reset()

      expect(state.loading).toBe(false)
      expect(state.error).toBeNull()
      expect(state.data).toBeNull()
      expect(isSuccess.value).toBe(false)
    })

    it('resets after error', async () => {
      vi.mocked(saveExtensionFields).mockRejectedValueOnce(new Error('fail'))
      const { saveExtFields, state, reset } = useExtFieldSave()

      const formData = { ext_name: 'test' }
      const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

      await saveExtFields(formData, configs, 'test_view', 'rec-001')
      expect(state.error).not.toBeNull()

      reset()
      expect(state.error).toBeNull()
      expect(state.loading).toBe(false)
    })
  })

  describe('edge cases', () => {
    it('handles null/undefined values in extension fields', async () => {
      const { saveExtFields, state } = useExtFieldSave()

      const formData = { ext_name: null, ext_note: undefined }
      const configs: FieldConfig[] = [
        makeFieldConfig({ field: 'ext_name' }),
        makeFieldConfig({ field: 'ext_note' })
      ]

      const result = await saveExtFields(formData, configs, 'test_view', 'rec-001')

      expect(result).toEqual(mockSaveResponse)
      expect(state.data).toEqual(mockSaveResponse)
    })

    it('multiple saves work sequentially', async () => {
      const { saveExtFields, state } = useExtFieldSave()
      const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

      await saveExtFields({ ext_name: 'a' }, configs, 'test_view', 'rec-001')
      expect(state.data).toEqual(mockSaveResponse)

      await saveExtFields({ ext_name: 'b' }, configs, 'test_view', 'rec-002')
      expect(state.data).toEqual(mockSaveResponse)
    })

    it('clears error on subsequent successful save', async () => {
      vi.mocked(saveExtensionFields)
        .mockRejectedValueOnce(new Error('fail'))
        .mockResolvedValueOnce(mockSaveResponse)

      const { saveExtFields, state } = useExtFieldSave()
      const configs: FieldConfig[] = [makeFieldConfig({ field: 'ext_name' })]

      await saveExtFields({ ext_name: 'test' }, configs, 'test_view', 'rec-001')
      expect(state.error).not.toBeNull()

      await saveExtFields({ ext_name: 'test2' }, configs, 'test_view', 'rec-001')
      expect(state.error).toBeNull()
      expect(state.data).not.toBeNull()
    })
  })
})
