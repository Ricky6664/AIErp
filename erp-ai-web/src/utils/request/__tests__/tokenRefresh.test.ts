import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'

// --- Hoisted mocks (evaluated before all imports) ---
const mockRefreshPost = vi.hoisted(() => vi.fn())
const mockRouterReplace = vi.hoisted(() => vi.fn())
const mockRouterCurrentRoute = vi.hoisted(() => ({
  value: { path: '/dashboard', fullPath: '/dashboard?tab=1' }
}))
const mockStore = vi.hoisted(() => ({
  token: '' as string,
  refreshToken: '' as string,
  userInfo: null as any,
  permissions: [] as string[],
  roles: [] as string[],
  menuTree: [] as any[]
}))

vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    warning: vi.fn(),
    error: vi.fn(),
    info: vi.fn()
  }
}))

vi.mock('vue-router', () => ({
  useRouter: () => ({
    replace: mockRouterReplace,
    currentRoute: mockRouterCurrentRoute
  })
}))

vi.mock('@/stores/modules/user', () => ({
  useUserStore: () => mockStore
}))

vi.mock('@/router/constants', () => ({
  TOKEN_KEY: 'satoken',
  REFRESH_TOKEN_KEY: 'refresh_token'
}))

// Mock axios: return controlled instances
vi.mock('axios', async () => {
  const actualAxios = await vi.importActual<typeof import('axios')>('axios')
  const mockAxiosFn = vi.fn().mockResolvedValue({ data: { code: 0, data: 'ok' } })
  return {
    ...actualAxios,
    default: Object.assign(mockAxiosFn, {
      create: vi.fn(() => ({
        post: mockRefreshPost,
        interceptors: { request: { use: vi.fn() }, response: { use: vi.fn() } }
      })),
      post: vi.fn(),
      get: vi.fn(),
      interceptors: { request: { use: vi.fn() }, response: { use: vi.fn() } }
    })
  }
})

// Helper to reset module-level state in tokenRefresh.ts
// We can't easily reset module-level `let` bindings, so we use the module's
// exported function and test observable effects.

function makeConfig(overrides: Record<string, any> = {}): any {
  return {
    url: '/api/some-resource',
    method: 'GET',
    headers: {
      Authorization: 'Bearer old-token',
      satoken: 'old-token'
    },
    ...overrides
  }
}

describe('Token Refresh Logic - Verification Report', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockStore.token = ''
    mockStore.refreshToken = ''
    mockStore.userInfo = null
    mockStore.permissions = []
    mockStore.roles = []
    mockStore.menuTree = []
    mockRouterReplace.mockClear()
    localStorage.clear()
    mockRefreshPost.mockReset()
  })

  afterEach(() => {
    localStorage.clear()
  })

  // ============================================================
  // 1. Happy Path: accessToken expired → refresh → replay
  // ============================================================
  describe('Scenario 1: Normal token refresh succeeds', () => {
    it('should refresh token and update localStorage and Pinia store', async () => {
      const { handleTokenRefresh } = await import('../tokenRefresh')
      localStorage.setItem('refresh_token', 'valid-refresh-token')

      const newToken = 'new-access-token-abc'
      const newRefreshToken = 'new-refresh-token-xyz'

      mockRefreshPost.mockResolvedValueOnce({
        data: {
          code: 0,
          data: { token: newToken, refreshToken: newRefreshToken }
        }
      })

      const config = makeConfig()

      await handleTokenRefresh(config)

      // Verify localStorage
      expect(localStorage.getItem('satoken')).toBe(newToken)
      expect(localStorage.getItem('refresh_token')).toBe(newRefreshToken)

      // Verify Pinia store
      expect(mockStore.token).toBe(newToken)
      expect(mockStore.refreshToken).toBe(newRefreshToken)

      // Verify request headers updated
      expect(config.headers.Authorization).toBe(`Bearer ${newToken}`)
      expect(config.headers.satoken).toBe(newToken)
    })
  })

  // ============================================================
  // 2. Refresh request itself fails → no infinite loop
  // ============================================================
  describe('Scenario 2: Refresh endpoint itself returns 401', () => {
    it('should NOT retry refresh and should redirect to login', async () => {
      const { handleTokenRefresh } = await import('../tokenRefresh')

      const refreshConfig = makeConfig({ url: '/api/auth/token/refresh' })

      await expect(handleTokenRefresh(refreshConfig)).rejects.toThrow('刷新Token失败，请重新登录')
      expect(mockRouterReplace).toHaveBeenCalled()
      expect(mockStore.token).toBe('')
      expect(mockStore.refreshToken).toBe('')
    })
  })

  // ============================================================
  // 3. refreshToken expired → clear all → redirect to login
  // ============================================================
  describe('Scenario 3: refreshToken expired', () => {
    it('should clear all tokens and redirect to login with redirect param', async () => {
      const { handleTokenRefresh } = await import('../tokenRefresh')
      localStorage.setItem('refresh_token', 'expired-refresh-token')

      mockRefreshPost.mockRejectedValueOnce(new Error('Network Error'))

      const config = makeConfig({ url: '/api/protected-data' })

      await expect(handleTokenRefresh(config)).rejects.toThrow('Token刷新失败')

      expect(mockStore.token).toBe('')
      expect(mockStore.refreshToken).toBe('')
      expect(mockStore.userInfo).toBeNull()
      expect(localStorage.getItem('satoken')).toBeNull()
      expect(localStorage.getItem('refresh_token')).toBeNull()
      expect(mockRouterReplace).toHaveBeenCalled()
      const callArgs = mockRouterReplace.mock.calls[0][0]
      expect(callArgs.path).toBe('/login')
      expect(callArgs.query).toHaveProperty('redirect')
    })
  })

  // ============================================================
  // 4. No refresh token in localStorage
  // ============================================================
  describe('Scenario 4: No refresh token available', () => {
    it('should fail and redirect to login immediately', async () => {
      const { handleTokenRefresh } = await import('../tokenRefresh')
      // No refresh_token in localStorage

      const config = makeConfig()

      await expect(handleTokenRefresh(config)).rejects.toThrow('Token刷新失败')
      expect(mockRouterReplace).toHaveBeenCalled()
    })
  })

  // ============================================================
  // 5. Concurrent requests → single refresh → all succeed
  // ============================================================
  describe('Scenario 5: Concurrent requests trigger single refresh', () => {
    it('should refresh only once and replay all queued requests', async () => {
      // Re-import to get fresh module state
      const { handleTokenRefresh } = await import('../tokenRefresh')
      localStorage.setItem('refresh_token', 'valid-refresh-token')

      const newToken = 'fresh-token-concurrent'
      const newRefreshToken = 'fresh-refresh-concurrent'

      // Use a deferred promise so refresh does NOT resolve synchronously,
      // allowing other concurrent requests to enter the queue
      let resolveRefresh: (value: any) => void
      const deferredPromise = new Promise((resolve) => {
        resolveRefresh = resolve
      })
      mockRefreshPost.mockReturnValueOnce(deferredPromise)

      const configs = Array.from({ length: 5 }, (_, i) => makeConfig({ url: `/api/resource-${i}` }))

      // Fire all 5 concurrently
      const resultPromises = configs.map((c) => handleTokenRefresh(c))

      // Small delay to allow all to enter queue
      await new Promise((r) => setTimeout(r, 50))

      // Now resolve the refresh
      resolveRefresh!({
        data: {
          code: 0,
          data: { token: newToken, refreshToken: newRefreshToken }
        }
      })

      const results = await Promise.allSettled(resultPromises)

      const fulfilled = results.filter((r) => r.status === 'fulfilled')
      expect(fulfilled.length).toBe(5)

      // Only one refresh call was made
      expect(mockRefreshPost).toHaveBeenCalledTimes(1)

      // Store updated
      expect(mockStore.token).toBe(newToken)
      expect(localStorage.getItem('satoken')).toBe(newToken)
    })
  })

  // ============================================================
  // 6. Refresh failure clears all queued requests
  // ============================================================
  describe('Scenario 6: Pending requests rejected on refresh failure', () => {
    it('should reject all queued requests when refresh fails', async () => {
      const { handleTokenRefresh } = await import('../tokenRefresh')
      localStorage.setItem('refresh_token', 'expired-refresh-token')

      mockRefreshPost.mockRejectedValueOnce(new Error('Refresh failed'))

      // Fire 3 concurrent requests
      const configs = Array.from({ length: 3 }, (_, i) => makeConfig({ url: `/api/resource-${i}` }))

      const results = await Promise.allSettled(configs.map((c) => handleTokenRefresh(c)))

      const rejected = results.filter((r) => r.status === 'rejected')
      expect(rejected.length).toBe(3)

      // All should mention expired session
      for (const r of rejected) {
        expect((r as PromiseRejectedResult).reason.message).toMatch(/登录已过期|Token刷新失败/)
      }

      // User redirected to login
      expect(mockRouterReplace).toHaveBeenCalled()
    })
  })

  // ============================================================
  // 7. Headers properly updated after refresh
  // ============================================================
  describe('Scenario 7: Request headers updated with new token', () => {
    it('should set both Authorization and satoken headers on the config', async () => {
      const { handleTokenRefresh } = await import('../tokenRefresh')
      localStorage.setItem('refresh_token', 'valid-refresh-token')

      mockRefreshPost.mockResolvedValueOnce({
        data: {
          code: 0,
          data: { token: 'abc-new', refreshToken: 'def-new' }
        }
      })

      const config = makeConfig()
      config.headers.Authorization = 'Bearer expired-token'
      config.headers.satoken = 'expired-token'

      await handleTokenRefresh(config)

      expect(config.headers.Authorization).toBe('Bearer abc-new')
      expect(config.headers.satoken).toBe('abc-new')
    })
  })

  // ============================================================
  // 8. Boundary: Login page should not trigger redirect loop
  // ============================================================
  describe('Scenario 8: Already on login page — no redirect', () => {
    it('should not redirect when already on login page', async () => {
      const { handleTokenRefresh } = await import('../tokenRefresh')
      localStorage.setItem('refresh_token', 'expired-refresh-token')

      mockRefreshPost.mockRejectedValueOnce(new Error('Refresh failed'))
      mockRouterCurrentRoute.value = { path: '/login', fullPath: '/login' }

      const config = makeConfig()

      await expect(handleTokenRefresh(config)).rejects.toThrow('Token刷新失败')

      // Should NOT call router.replace since we're already on login
      expect(mockRouterReplace).not.toHaveBeenCalled()
    })
  })

  // ============================================================
  // 9. Boundary: Config without URL handled gracefully
  // ============================================================
  describe('Scenario 9: Config with undefined url', () => {
    it('should treat missing url as non-refresh request (no infinite loop)', async () => {
      const { handleTokenRefresh } = await import('../tokenRefresh')
      localStorage.setItem('refresh_token', 'valid-refresh-token')

      mockRefreshPost.mockResolvedValueOnce({
        data: {
          code: 0,
          data: { token: 'token-x', refreshToken: 'rt-x' }
        }
      })

      const config = makeConfig({ url: undefined })

      await handleTokenRefresh(config)

      // Should succeed — treated as normal request refresh
      expect(mockStore.token).toBe('token-x')
      expect(mockStore.refreshToken).toBe('rt-x')
    })
  })
})
