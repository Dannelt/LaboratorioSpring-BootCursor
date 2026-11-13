import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: { host: 'localhost', port: 5173, strictPort: true },
  test: { environment: 'jsdom', globals: true, restoreMocks: true, setupFiles: ['./src/testSetup.ts'] },
})
