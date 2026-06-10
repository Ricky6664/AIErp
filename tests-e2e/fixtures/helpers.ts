import { Page, expect } from '@playwright/test'

export async function waitForTableLoad(page: Page) {
  await page.waitForSelector('.vxe-table--body, .el-table__body', { timeout: 10000 })
}

export async function clickTableRow(page: Page, rowIndex: number = 0) {
  const rows = page.locator('.vxe-table--body tr, .el-table__body tr')
  await rows.nth(rowIndex).click()
}

export async function fillFormField(page: Page, label: string, value: string) {
  const formItem = page.locator('.el-form-item', { hasText: label })
  const input = formItem.locator('input, textarea').first()
  await input.fill(value)
}

export async function submitForm(page: Page) {
  await page.click('button:has-text("保存")')
  await page.waitForTimeout(1000)
}

export async function verifySuccessMessage(page: Page) {
  await expect(page.locator('.el-message--success, .el-notification--success').first()).toBeVisible({ timeout: 5000 })
}

export async function verifyPageTitle(page: Page, title: string) {
  await expect(page.locator('h1, .page-title, .el-breadcrumb').filter({ hasText: title }).first()).toBeVisible({ timeout: 5000 })
}
