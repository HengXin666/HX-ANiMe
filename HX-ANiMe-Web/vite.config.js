import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { fileURLToPath, URL } from 'url'; // 导入 fileURLToPath 和 URL

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: "0.0.0.0",
    proxy: {
      '/api': { // 本地开发环境通过代理实现跨域, 生产环境使用 nginx 转发
        changeOrigin: true,
        target: 'http://localhost:28205',
        rewrite: (path) => path.replace(/^\/api/, ""),
      }
    }    
  }
});
