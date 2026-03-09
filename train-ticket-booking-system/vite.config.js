import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  server: {
    port: 3000,
    host: 'localhost',  // 避免 IPv6 问题
    proxy: {
      '/mockApi': {
        target: 'http://127.0.0.1:4523/m1/7060669-6780902-default',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/mockApi/, '')
      },
      '/api': {
        target: 'http://127.0.0.1:10087',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')

      },
    }
  },
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
