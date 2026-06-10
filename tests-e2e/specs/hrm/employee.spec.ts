import { test, expect } from '@playwright/test'
import { waitForTableLoad, verifyPageTitle } from '../../fixtures/helpers'

test.describe('HRM — 员工管理', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[placeholder*="用户名"], input[name*="username"]', 'admin')
    await page.fill('input[placeholder*="密码"], input[type="password"]', 'admin123')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()
    await page.waitForURL(/\/home|\/dashboard/, { timeout: 10000 })
  })

  test('员工中心页面渲染: 基本结构正常', async ({ page }) => {
    await page.goto('/hrm/employeecenter')
    await expect(page.locator('body').first()).toBeVisible()
  })

  test('员工档案页面渲染: 表单或列表正常显示', async ({ page }) => {
    await page.goto('/hrm/employeearchive')
    await waitForTableLoad(page)
  })
})
