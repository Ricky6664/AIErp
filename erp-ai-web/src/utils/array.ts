interface TreeNode {
  id: string | number
  parentId: string | number | null
  children?: TreeNode[]
  [key: string]: any
}

export function arrayToTree<T extends TreeNode>(list: T[], rootId?: string | number | null): T[] {
  if (!list.length) return []

  const map = new Map<string | number, T>()
  const result: T[] = []

  for (const item of list) {
    map.set(item.id, { ...item, children: [] } as unknown as T)
  }

  const isRoot = (parentId: string | number | null): boolean => {
    if (rootId !== undefined) return parentId === rootId
    return parentId === null || parentId === undefined || parentId === 0
  }

  for (const item of list) {
    const node = map.get(item.id)!
    const pid = item.parentId

    if (isRoot(pid)) {
      result.push(node)
    } else if (pid !== null && pid !== undefined && map.has(pid)) {
      const parent = map.get(pid)!
      parent.children = parent.children || []
      parent.children.push(node)
    } else {
      result.push(node)
    }
  }

  if (!result.length) {
    console.warn('arrayToTree: no root nodes found in the data')
  }

  return result
}

export function treeToArray<T extends Record<string, any>>(
  tree: T[],
  childrenKey: string = 'children'
): T[] {
  const result: T[] = []
  const stack: T[] = [...tree]

  while (stack.length) {
    const node = stack.pop()!
    const { [childrenKey]: children, ...rest } = node
    result.push(rest as unknown as T)
    if (Array.isArray(children) && children.length) {
      for (let i = children.length - 1; i >= 0; i--) {
        stack.push(children[i])
      }
    }
  }

  return result
}

export function uniqueArray<T>(arr: T[], keyOrFn?: keyof T | ((item: T) => any)): T[] {
  if (!arr.length) return []

  const isPrimitive = (v: unknown): boolean =>
    typeof v === 'string' || typeof v === 'number' || typeof v === 'boolean'

  if (arr.every((item) => isPrimitive(item))) {
    return [...new Set(arr)]
  }

  if (!keyOrFn) {
    const seen = new Set<string>()
    return arr.filter((item) => {
      const key = JSON.stringify(item)
      if (seen.has(key)) return false
      seen.add(key)
      return true
    })
  }

  const seen = new Set<any>()
  const getter = typeof keyOrFn === 'function' ? keyOrFn : (item: T) => item[keyOrFn]

  return arr.filter((item) => {
    const key = getter(item)
    if (seen.has(key)) return false
    seen.add(key)
    return true
  })
}

export function flatten<T>(arr: T[], depth: number = Infinity): T[] {
  if (!Array.isArray(arr)) return [arr]
  return arr.flat(depth) as T[]
}

export function groupBy<T>(
  arr: T[],
  keyOrFn: keyof T | ((item: T) => string)
): Record<string, T[]> {
  const result: Record<string, T[]> = {}
  const getter = typeof keyOrFn === 'function' ? keyOrFn : (item: T) => String(item[keyOrFn])

  for (const item of arr) {
    const key = getter(item)
    if (!result[key]) {
      result[key] = []
    }
    result[key].push(item)
  }

  return result
}
