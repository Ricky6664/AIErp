import { describe, it, expect, vi } from 'vitest'
import { useMutation } from '@/composables/useMutation'

interface TestInput {
  name: string
  description?: string
  id?: string
  version?: number
}

interface TestResult {
  id: string
  name: string
  version: number
}

function createSuccessResult(): TestResult {
  return { id: '123', name: 'test', version: 1 }
}

describe('useMutation', () => {
  describe('initialization', () => {
    it('initializes with default state', () => {
      const { state } = useMutation()
      expect(state.loading).toBe(false)
      expect(state.error).toBeNull()
      expect(state.data).toBeNull()
    })

    it('isSuccess is false initially', () => {
      const { isSuccess } = useMutation()
      expect(isSuccess.value).toBe(false)
    })

    it('accepts create and update functions', () => {
      const create = vi.fn()
      const update = vi.fn()
      const { state } = useMutation({ create, update })
      expect(state.loading).toBe(false)
    })
  })

  describe('mutate — create (POST)', () => {
    it('calls create function and returns result', async () => {
      const result = createSuccessResult()
      const create = vi.fn().mockResolvedValue(result)
      const { mutate, state } = useMutation<TestResult, TestInput>({
        create,
        autoDetectMode: false
      })

      const data = await mutate({ name: 'test' })

      expect(create).toHaveBeenCalledTimes(1)
      expect(create).toHaveBeenCalledWith({ name: 'test' })
      expect(data).toEqual(result)
      expect(state.data).toEqual(result)
      expect(state.error).toBeNull()
    })

    it('sets loading=true during create', async () => {
      const create = vi.fn().mockImplementation(() => new Promise((r) => setTimeout(r, 10)))
      const { mutate, state } = useMutation<TestResult, TestInput>({
        create,
        autoDetectMode: false
      })

      const promise = mutate({ name: 'test' })
      expect(state.loading).toBe(true)

      await promise
      expect(state.loading).toBe(false)
    })

    it('clears error on new create attempt', async () => {
      const create = vi
        .fn()
        .mockRejectedValueOnce(new Error('fail'))
        .mockResolvedValueOnce(createSuccessResult())
      const { mutate, state } = useMutation<TestResult, TestInput>({
        create,
        autoDetectMode: false
      })

      await mutate({ name: 'test' })
      expect(state.error).not.toBeNull()

      await mutate({ name: 'test2' })
      expect(state.error).toBeNull()
      expect(state.data).not.toBeNull()
    })
  })

  describe('mutate — update (PUT)', () => {
    it('calls update function when data has id and autoDetectMode=true', async () => {
      const update = vi.fn().mockResolvedValue(createSuccessResult())
      const create = vi.fn()
      const { mutate } = useMutation<TestResult, TestInput>({
        create,
        update,
        autoDetectMode: true
      })

      await mutate({ id: '123', name: 'updated' })

      expect(update).toHaveBeenCalledTimes(1)
      expect(create).not.toHaveBeenCalled()
    })

    it('calls create when data has no id and autoDetectMode=true', async () => {
      const create = vi.fn().mockResolvedValue(createSuccessResult())
      const update = vi.fn()
      const { mutate } = useMutation<TestResult, TestInput>({
        create,
        update,
        autoDetectMode: true
      })

      await mutate({ name: 'new' })

      expect(create).toHaveBeenCalledTimes(1)
      expect(update).not.toHaveBeenCalled()
    })

    it('calls update when autoDetectMode=false and update provided', async () => {
      const update = vi.fn().mockResolvedValue(createSuccessResult())
      const { mutate } = useMutation<TestResult, TestInput>({
        update,
        autoDetectMode: false
      })

      await mutate({ name: 'updated' })

      expect(update).toHaveBeenCalledTimes(1)
    })
  })

  describe('error handling', () => {
    it('catches exception and sets state.error', async () => {
      const create = vi.fn().mockRejectedValue(new Error('Network error'))
      const { mutate, state } = useMutation<TestResult, TestInput>({
        create,
        autoDetectMode: false
      })

      const data = await mutate({ name: 'test' })

      expect(data).toBeNull()
      expect(state.error).toBeInstanceOf(Error)
      expect(state.error!.message).toBe('Network error')
    })

    it('converts non-Error throws to Error objects', async () => {
      const create = vi.fn().mockRejectedValue('string error')
      const { mutate, state } = useMutation<TestResult, TestInput>({
        create,
        autoDetectMode: false
      })

      await mutate({ name: 'test' })

      expect(state.error).toBeInstanceOf(Error)
      expect(state.error!.message).toBe('string error')
    })

    it('isSuccess is false after error', async () => {
      const create = vi.fn().mockRejectedValue(new Error('fail'))
      const { mutate, isSuccess } = useMutation<TestResult, TestInput>({
        create,
        autoDetectMode: false
      })

      await mutate({ name: 'test' })
      expect(isSuccess.value).toBe(false)
    })
  })

  describe('callbacks', () => {
    it('calls onSuccess with result data', async () => {
      const result = createSuccessResult()
      const create = vi.fn().mockResolvedValue(result)
      const onSuccess = vi.fn()
      const { mutate } = useMutation<TestResult, TestInput>({
        create,
        onSuccess,
        autoDetectMode: false
      })

      await mutate({ name: 'test' })

      expect(onSuccess).toHaveBeenCalledTimes(1)
      expect(onSuccess).toHaveBeenCalledWith(result)
    })

    it('calls onError with error on failure', async () => {
      const error = new Error('fail')
      const create = vi.fn().mockRejectedValue(error)
      const onError = vi.fn()
      const { mutate } = useMutation<TestResult, TestInput>({
        create,
        onError,
        autoDetectMode: false
      })

      await mutate({ name: 'test' })

      expect(onError).toHaveBeenCalledTimes(1)
      expect(onError).toHaveBeenCalledWith(error)
    })

    it('does NOT call onSuccess when create throws', async () => {
      const create = vi.fn().mockRejectedValue(new Error('fail'))
      const onSuccess = vi.fn()
      const { mutate } = useMutation<TestResult, TestInput>({
        create,
        onSuccess,
        autoDetectMode: false
      })

      await mutate({ name: 'test' })
      expect(onSuccess).not.toHaveBeenCalled()
    })
  })

  describe('reset', () => {
    it('resets state to initial values', async () => {
      const create = vi.fn().mockResolvedValue(createSuccessResult())
      const { mutate, state, isSuccess, reset } = useMutation<TestResult, TestInput>({
        create,
        autoDetectMode: false
      })

      await mutate({ name: 'test' })
      expect(state.data).not.toBeNull()
      expect(isSuccess.value).toBe(true)

      reset()

      expect(state.loading).toBe(false)
      expect(state.error).toBeNull()
      expect(state.data).toBeNull()
      expect(isSuccess.value).toBe(false)
    })

    it('resets after error', async () => {
      const create = vi.fn().mockRejectedValue(new Error('fail'))
      const { mutate, state, reset } = useMutation<TestResult, TestInput>({
        create,
        autoDetectMode: false
      })

      await mutate({ name: 'test' })
      expect(state.error).not.toBeNull()

      reset()
      expect(state.error).toBeNull()
      expect(state.loading).toBe(false)
    })
  })

  describe('mutateWithVersion', () => {
    it('passes version through to update', async () => {
      const update = vi.fn().mockResolvedValue({ id: '123', name: 'test', version: 2 })
      const { mutateWithVersion } = useMutation<TestResult, TestInput>({
        update,
        autoDetectMode: false
      })

      await mutateWithVersion({ id: '123', name: 'test', version: 1 })

      expect(update).toHaveBeenCalledWith({ id: '123', name: 'test', version: 1 })
    })

    it('returns null when no create or update provided', async () => {
      const { mutateWithVersion } = useMutation<TestResult, TestInput>({})

      const result = await mutateWithVersion({ name: 'test', version: 1 })

      expect(result).toBeNull()
    })
  })

  describe('edge cases', () => {
    it('throws when neither create nor update provided', async () => {
      const { mutate, state } = useMutation<TestResult, TestInput>({})

      const result = await mutate({ name: 'test' })

      expect(result).toBeNull()
      expect(state.error).toBeInstanceOf(Error)
      expect(state.error!.message).toContain('create 或 update')
    })

    it('isSuccess=false when data is null or undefined', async () => {
      const create = vi.fn().mockResolvedValue(null)
      const { mutate, isSuccess } = useMutation<TestResult | null, TestInput>({
        create,
        autoDetectMode: false
      })

      await mutate({ name: 'test' })
      expect(isSuccess.value).toBe(false)
    })

    it('loading is false in finally block even on success', async () => {
      const create = vi.fn().mockResolvedValue(createSuccessResult())
      const { mutate, state } = useMutation<TestResult, TestInput>({
        create,
        autoDetectMode: false
      })

      await mutate({ name: 'test' })
      expect(state.loading).toBe(false)
    })

    it('multiple mutate calls work sequentially', async () => {
      const create = vi
        .fn()
        .mockResolvedValueOnce({ id: '1', name: 'a', version: 1 })
        .mockResolvedValueOnce({ id: '2', name: 'b', version: 1 })
      const { mutate, state } = useMutation<TestResult, TestInput>({
        create,
        autoDetectMode: false
      })

      await mutate({ name: 'a' })
      expect(state.data).toEqual({ id: '1', name: 'a', version: 1 })

      await mutate({ name: 'b' })
      expect(state.data).toEqual({ id: '2', name: 'b', version: 1 })

      expect(create).toHaveBeenCalledTimes(2)
    })
  })
})
