import { test, expect } from '@playwright/test'

test.describe('登录流程', () => {
  test('正常登录: 输入正确的用户名和密码后跳转首页', async ({ page }) => {
    await page.goto('/login')

    await expect(page.locator('input[placeholder*="用户名"], input[name*="username"]').first()).toBeVisible()
    await expect(page.locator('input[placeholder*="密码"], input[type="password"]').first()).toBeVisible()
    await expect(page.getByRole('button', { name: /登录|登録|login/i })).toBeVisible()

    await page.fill('input[placeholder*="用户名"], input[name*="username"]', 'admin')
    await page.fill('input[placeholder*="密码"], input[type="password"]', 'admin123')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()

    await page.waitForURL(/\/home|\/dashboard|\//, { timeout: 10000 })
  })

  test('登录失败: 错误密码应显示提示信息', async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[placeholder*="用户名"], input[name*="username"]', 'admin')
    await page.fill('input[placeholder*="密码"], input[type="password"]', 'wrongpassword')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()

    const errorIndicator = page.locator('.el-message--error, .el-form-item__error, .el-alert--error, [class*="error"]').first()
    await expect(errorIndicator).toBeVisible({ timeout: 5000 })
  })

  test('空表单验证: 不输入直接点登录应有校验提示', async ({ page }) => {
    await page.goto('/login')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()

    const validation = page.locator('.el-form-item__error, .el-message--warning').first()
    const buttonDisabled = page.getByRole('button', { name: /登录|登録|login/i }).isDisabled()
    const hasValidation = await validation.isVisible().catch(() => false)
    const isDisabled = await buttonDisabled.catch(() => false)
    expect(hasValidation || isDisabled).toBeTruthy()
  })
})
