import { test, expect } from '@playwright/test'
import { waitForTableLoad, verifyPageTitle } from '../../fixtures/helpers'

test.describe('仓库管理', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[placeholder*="用户名"], input[name*="username"]', 'admin')
    await page.fill('input[placeholder*="密码"], input[type="password"]', 'admin123')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()
    await page.waitForURL(/\/home|\/dashboard/, { timeout: 10000 })
  })

  test('仓库列表页渲染: 表格数据正常加载', async ({ page }) => {
    await page.goto('/warehouse/warehouse')
    await waitForTableLoad(page)
    await verifyPageTitle(page, '仓库')
  })

  test('仓库工作台渲染: KPI卡片和图表显示', async ({ page }) => {
    await page.goto('/warehouse/workbench')
    await expect(page.locator('.kpi-card, .chart-area, [class*="kpi"], [class*="chart"]').first()).toBeVisible({ timeout: 10000 })
  })
})
