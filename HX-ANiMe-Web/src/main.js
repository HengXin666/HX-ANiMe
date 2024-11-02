import { createApp } from 'vue'
import { createPinia } from "pinia";

import App from './App.vue'
import router from './router'
import ElementPlus from "element-plus";
import zhCn from "element-plus/es/locale/lang/zh-cn"; //配置 Element Plus 的国际化

// CSS 导入
import "element-plus/dist/index.css";
import './assets/main.css'

// 导入图标
import * as ElementPlusIconsVue from "@element-plus/icons-vue";

const app = createApp(App)

// 配置 Element Plus 的国际化
app.use(ElementPlus, {
	locale: zhCn,
});
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component);
}
// 注册全局图标
app.use(createPinia());

app.use(router)
app.mount('#app')

// 安装HTTP中间件
import installHttp from "./plugins/http";
installHttp(router);

// 安装ElIcon
import installElIcon from "./plugins/el-icon";
installElIcon(app);