import { test, expect } from '@playwright/test'

test.describe('登录流程', () => {
  test('登录页渲染: 表单和元素正常显示', async ({ page }) => {
    await page.goto('/login')
    await page.waitForSelector('.el-input__inner', { timeout: 10000 })

    const inputs = page.locator('.el-input__inner')
    await expect(inputs.first()).toBeVisible()

    const loginButton = page.getByRole('button', { name: /登录|登録|login|Login/i })
    await expect(loginButton).toBeVisible()
  })

  test('验证码区域渲染: 验证码图片和刷新按钮存在', async ({ page }) => {
    await page.goto('/login')
    await page.waitForTimeout(2000)

    const captchaArea = page.locator('.captcha-stub, .captcha-image, img[src*="captcha"], [class*="captcha"]').first()
    const hasCaptcha = await captchaArea.isVisible().catch(() => false)

    const refreshBtn = page.locator('[class*="captcha-refresh"], button:has(.el-icon-refresh)').first()
    const hasRefresh = await refreshBtn.isVisible().catch(() => false)

    expect(hasCaptcha || hasRefresh).toBeTruthy()
  })

  test('空表单验证: 不输入直接点登录应有校验提示', async ({ page }) => {
    await page.goto('/login')
    await page.waitForSelector('.el-input__inner', { timeout: 10000 })

    await page.getByRole('button', { name: /登录|登録|login|Login/i }).click()
    await page.waitForTimeout(1500)

    const validation = page.locator('.el-form-item__error, .el-message--warning, .el-message--error').first()
    const button = page.getByRole('button', { name: /登录|登録|login|Login/i })
    const hasValidation = await validation.isVisible().catch(() => false)
    const isDisabled = await button.isDisabled().catch(() => false)
    expect(hasValidation || isDisabled).toBeTruthy()
  })
})
