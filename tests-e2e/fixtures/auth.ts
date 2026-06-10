import { test as base, expect } from '@playwright/test'

export async function performLogin(page: any) {
  await page.goto('/login')
  await page.waitForSelector('.el-input__inner', { timeout: 10000 })
  const inputs = page.locator('.el-input__inner')
  const count = await inputs.count()
  if (count >= 2) {
    await inputs.nth(0).fill('admin')
    await inputs.nth(1).fill('admin123')
  }
  await page.getByRole('button', { name: /登录|登録|login|Login/i }).click()
  await page.waitForURL(/\/home|\/dashboard|\/org|\/warehouse/, { timeout: 10000 })
}

export const test = base.extend({
  authenticatedPage: async ({ page }, use) => {
    await performLogin(page)
    await use(page)
  }
})

export { expect }
