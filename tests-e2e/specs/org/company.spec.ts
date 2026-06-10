import { test, expect } from '@playwright/test'
import { waitForTableLoad, verifyPageTitle } from '../../fixtures/helpers'

test.describe('组织架构 — 公司管理', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[placeholder*="用户名"], input[name*="username"]', 'admin')
    await page.fill('input[placeholder*="密码"], input[type="password"]', 'admin123')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()
    await page.waitForURL(/\/home|\/dashboard/, { timeout: 10000 })
  })

  test('公司列表页渲染: 表格和数据正常加载', async ({ page }) => {
    await page.goto('/org/company')
    await waitForTableLoad(page)
    await verifyPageTitle(page, '公司')
  })

  test('公司工作台渲染: 统计卡片和图表正常显示', async ({ page }) => {
    await page.goto('/org/workbench')
    await expect(page.locator('.kpi-card, .chart-area, [class*="kpi"]').first()).toBeVisible({ timeout: 10000 })
  })
})
