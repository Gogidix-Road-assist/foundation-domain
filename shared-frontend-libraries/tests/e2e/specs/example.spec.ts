import { test, expect } from '@playwright/test';

test('homepage has title', async ({ page }) => {
  await page.goto('/');
  await expect(page).toHaveTitle(/Rapid Assist/);
});

test('theme toggle works', async ({ page }) => {
  await page.goto('/');
  const themeToggle = page.getByTestId('theme-toggle');
  await themeToggle.click();
  await expect(page.locator('body')).toHaveClass(/dark/);
});
