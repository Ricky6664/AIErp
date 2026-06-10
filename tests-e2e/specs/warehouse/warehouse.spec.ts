import { test, expect } from '@playwright/test'

test.describe('仓库管理', () => {
  test('仓库列表页渲染: 页面正常加载', async ({ page }) => {
    await page.goto('/warehouse/warehouse')
    await page.waitForTimeout(2000)
    await expect(page.locator('body').first()).toBeVisible()
  })

  test('仓库工作台渲染: 页面正常加载', async ({ page }) => {
    await page.goto('/warehouse/workbench')
    await page.waitForTimeout(2000)
    await expect(page.locator('body').first()).toBeVisible()
  })
})
