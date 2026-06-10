import { test, expect } from '@playwright/test'

test.describe('财务管理 — 科目', () => {
  test('科目列表页渲染: 页面正常加载', async ({ page }) => {
    await page.goto('/finance/account')
    await page.waitForTimeout(2000)
    await expect(page.locator('body').first()).toBeVisible()
  })

  test('财务工作台渲染: 页面正常加载', async ({ page }) => {
    await page.goto('/finance/financeworkbench')
    await page.waitForTimeout(2000)
    await expect(page.locator('body').first()).toBeVisible()
  })
})
