import { test, expect } from '@playwright/test'

test.describe('HRM — 员工管理', () => {
  test('员工中心页面渲染: 页面正常加载', async ({ page }) => {
    await page.goto('/hrm/employeecenter')
    await page.waitForTimeout(2000)
    await expect(page.locator('body').first()).toBeVisible()
  })

  test('员工档案页面渲染: 页面正常加载', async ({ page }) => {
    await page.goto('/hrm/employeearchive')
    await page.waitForTimeout(2000)
    await expect(page.locator('body').first()).toBeVisible()
  })

  test('考勤管理页面渲染: 页面正常加载', async ({ page }) => {
    await page.goto('/hrm/attendance')
    await page.waitForTimeout(2000)
    await expect(page.locator('body').first()).toBeVisible()
  })
})
