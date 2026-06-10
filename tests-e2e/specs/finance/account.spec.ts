import { test, expect } from '@playwright/test'
import { waitForTableLoad, verifyPageTitle } from '../../fixtures/helpers'

test.describe('财务管理 — 科目', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[placeholder*="用户名"], input[name*="username"]', 'admin')
    await page.fill('input[placeholder*="密码"], input[type="password"]', 'admin123')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()
    await page.waitForURL(/\/home|\/dashboard/, { timeout: 10000 })
  })

  test('科目列表页渲染: 表格数据正常加载', async ({ page }) => {
    await page.goto('/finance/account')
    await waitForTableLoad(page)
    await verifyPageTitle(page, '科目')
  })
})
