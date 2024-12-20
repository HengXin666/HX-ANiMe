import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "../stores/useUserStore";

const routes = [];
routes.push(
    {
        path: "/:pathMatch(.*)*",
        name: "NotFound",
        component: () => import("../views/status/404.vue"),
    },
    {
        path: "/forbidden",
        name: "Forbidden",
        component: () => import("../views/status/403.vue"),
    },
    {
        path: "/error",
        name: "Error",
        component: () => import("../views/status/500.vue"),
    },
);

// 读取login模块路由
const loginRouter = import.meta.glob("./login/index.js", { eager: true });
for (const path in loginRouter) {
    routes.push(...loginRouter[path].default);
}

// 读取main模块路由
const mainRouter = import.meta.glob("./main/index.js", { eager: true });
for (const path in mainRouter) {
    routes.push(...mainRouter[path].default);
}

// 定义一个路由对象
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
});

// 添加一个路由的全局前置守卫
router.beforeEach(async function (to, from, next) {
    // 判断是否是登录页面

    // TODO 测试使用
    // ElMessage.info("看看界面");
    // next();
    
    if (
        to.name === "Login" ||
        to.name === "NotFound" ||
        to.name === "Forbidden" ||
        to.name === "Error"
    ) {
        next();
        ElMessage.warning("请登录");
        return;
    }
    // 判断本地是否记录token值
    let store = useUserStore();
    let token = store.getToken;
    // 如果有token
    if (token && token !== "undefined") {
        // 判断是否已经加载数据
        let isLoaded = store.isLoaded;
        // 如果没有加载
        if (!isLoaded) {
            // 加载用户信息
            // await store.loadUser();
            
            // 设置加载完毕
            store.setLoaded(true);
        }
        next();
    } else {
        // 如果没有token值, 直接进入登录
        next({ name: "Login" });
        ElMessage.warning("在未登录时, 禁止访问其他页面!");
    }
});

export default router;
