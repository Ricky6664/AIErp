export function deepClone<T>(obj: T, visited = new WeakMap<object, any>()): T {
  if (obj === null || obj === undefined || typeof obj !== 'object') return obj

  if (visited.has(obj as object)) return visited.get(obj as object)

  if (obj instanceof Date) return new Date(obj.getTime()) as unknown as T
  if (obj instanceof RegExp) return new RegExp(obj.source, obj.flags) as unknown as T

  if (obj instanceof Map) {
    const clone = new Map()
    visited.set(obj as object, clone)
    for (const [k, v] of obj) {
      clone.set(deepClone(k, visited), deepClone(v, visited))
    }
    return clone as unknown as T
  }

  if (obj instanceof Set) {
    const clone = new Set()
    visited.set(obj as object, clone)
    for (const v of obj) {
      clone.add(deepClone(v, visited))
    }
    return clone as unknown as T
  }

  if (Array.isArray(obj)) {
    const clone: any[] = []
    visited.set(obj as object, clone)
    for (const item of obj) {
      clone.push(deepClone(item, visited))
    }
    return clone as unknown as T
  }

  const clone: Record<string, any> = {}
  visited.set(obj as object, clone)
  for (const key of Object.keys(obj as object)) {
    clone[key] = deepClone((obj as Record<string, any>)[key], visited)
  }
  return clone as unknown as T
}

export function deepMerge<T extends Record<string, any>>(target: T, ...sources: Partial<T>[]): T {
  const result = deepClone(target)
  const visited = new WeakMap<object, object>()

  function merge(targetObj: Record<string, any>, sourceObj: Record<string, any>) {
    if (visited.has(sourceObj)) return

    for (const key of Object.keys(sourceObj)) {
      const sourceVal = sourceObj[key]
      if (sourceVal === undefined) continue

      if (Array.isArray(sourceVal)) {
        targetObj[key] = deepClone(sourceVal)
      } else if (
        sourceVal !== null &&
        typeof sourceVal === 'object' &&
        !(sourceVal instanceof Date) &&
        !(sourceVal instanceof RegExp) &&
        !(sourceVal instanceof Map) &&
        !(sourceVal instanceof Set)
      ) {
        if (
          targetObj[key] === null ||
          targetObj[key] === undefined ||
          typeof targetObj[key] !== 'object' ||
          Array.isArray(targetObj[key])
        ) {
          targetObj[key] = {}
        }
        merge(targetObj[key], sourceVal)
      } else {
        targetObj[key] = deepClone(sourceVal)
      }
    }
  }

  for (const source of sources) {
    merge(result, source as Record<string, any>)
  }

  return result
}

export function pick<T extends Record<string, any>, K extends keyof T>(
  obj: T,
  keys: K[]
): Pick<T, K> {
  const result = {} as Pick<T, K>
  for (const key of keys) {
    if (key in obj) {
      result[key] = obj[key]
    }
  }
  return result
}

export function omit<T extends Record<string, any>, K extends keyof T>(
  obj: T,
  keys: K[]
): Omit<T, K> {
  const keySet = new Set(keys)
  const result = {} as Omit<T, K>
  for (const key of Object.keys(obj)) {
    if (!keySet.has(key as K)) {
      ;(result as Record<string, any>)[key] = obj[key]
    }
  }
  return result
}
