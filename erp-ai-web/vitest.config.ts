import { defineConfig } from 'vitest/config'
import { resolve } from 'path'

export default defineConfig({
  test: {
    include: ['src/types/__tests__/**/*.test-d.ts'],
    typecheck: {
      enabled: true,
      include: ['src/types/__tests__/**/*.test-d.ts']
    }
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  }
})
