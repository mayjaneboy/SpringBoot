import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite' //自动导入vue中的组件
import Components from 'unplugin-vue-components/vite' //自动导入ui-组件 比如element-plus
import { ElementPlusResolver }  from "unplugin-vue-components/resolvers"; //对应组件库引入

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
      //element-plus按需导入
      AutoImport({
        resolvers: [ElementPlusResolver()],
      }),
      Components({
        resolvers: [
            //配置elementPlus采用sass样式配置系统
            ElementPlusResolver({importStyle:"sass"})
        ],
      }),
  ],
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/assets/css/index.scss" as *;`,
      },
    },
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})