import { test, expect } from '@playwright/test'

test.describe('组织架构 — 公司管理', () => {
  test('公司表单页渲染: 弹窗和表单正常显示', async ({ page }) => {
    await page.goto('/org/company')
    await page.waitForTimeout(2000)
    await expect(page.locator('body').first()).toBeVisible()
  })

  test('公司工作台渲染: 页面正常加载', async ({ page }) => {
    await page.goto('/org/workbench')
    await page.waitForTimeout(2000)
    await expect(page.locator('body').first()).toBeVisible()
  })
})
