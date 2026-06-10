import { defineConfig } from 'vitest/config'
import { fileURLToPath } from 'url'
import { resolve, dirname } from 'path'
import vue from '@vitejs/plugin-vue'

const __dirname = dirname(fileURLToPath(import.meta.url))

export default defineConfig({
  plugins: [vue()],
  test: {
    include: ['src/**/*.test.ts'],
    environment: 'happy-dom',
    globals: true,
    setupFiles: ['src/test-setup.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text-summary', 'lcov'],
      thresholds: {
        lines: 70,
        branches: 60
      }
    }
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  }
})
