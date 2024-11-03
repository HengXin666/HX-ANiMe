<template>
	<el-container class="outside-container">
		<el-header class="head">
			<el-menu :default-active="activeIndex" class="el-menu" mode="horizontal" :ellipsis="false"
				@select="handleSelect">
				<el-menu-item index="0">
					<router-link to="/info">
						<img width="140" src="@/views/img/logo/HX-ANiMe.png" alt="Logo" />
					</router-link>
				</el-menu-item>
				<el-menu-item index="1">
					<router-link to="/dashboard">Home</router-link>
				</el-menu-item>
				<el-menu-item index="2">
					<router-link to="/force-graph">番剧可视化</router-link>
				</el-menu-item>
			</el-menu>

			<div class="row-wrapper">
				<el-row :gutter="60">
					<el-col :span="6">
						<el-switch v-model="isDark" @change="toggleDark" size="large">
							<template #active-action>
								<el-icon>
									<Moon color="{{ changeTheme }}" />
								</el-icon>
							</template>
							<template #inactive-action>
								<el-icon>
									<Sunny color="{{ changeTheme }}" />
								</el-icon>
							</template>
						</el-switch>
					</el-col>
					<el-col :span="6">
						<el-avatar src="https://null.com"> user </el-avatar>
					</el-col>
				</el-row>
			</div>
		</el-header>

		<!-- 内容区域 -->
		<el-main>
			<router-view style="height: max-content;" :layout-theme-color="layoutThemeColor" />
		</el-main>
	</el-container>
</template>

<script setup lang="ts">
/** 切换主题
	<div style="margin: 10px">
		<span v-for="item in themeColors" :key="item.themeName" :style="{
			background: item.color,
			width: '20px',
			height: '20px',
			display: 'inline-block',
			marginRight: '10px',
			cursor: 'pointer',
			border: '1px solid #333',
			borderRadius: '10%',
		}" @click="changeThemeColor(item.color)"></span>
	</div>
 */

import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router';
import { useStorage } from '@vueuse/core';
import { useElementPlusTheme } from 'use-element-plus-theme';
import { useDark, useToggle } from '@vueuse/core';
import 'element-plus/theme-chalk/dark/css-vars.css' // 暗黑模式 css

const layoutThemeColor = useStorage('layout-theme-color', '#990099');  // 默认主题色
const { changeTheme } = useElementPlusTheme(layoutThemeColor.value);  // 初始化主题色

const isDark = useDark();  // 检测当前是否为深色模式
const toggleDark = useToggle(isDark);  // 用于切换深色和浅色模式

// 初始化页面索引
const activeIndex = ref('1')
const route = useRoute(); // 获取当前路由

// 在组件挂载后更新 activeIndex
onMounted(() => {
  switch (route.path) {
    case '/info':
      activeIndex.value = '0';
      break;
    case '/dashboard':
      activeIndex.value = '1';
      break;
    case '/force-graph':
      activeIndex.value = '2';
      break;
    default:
      activeIndex.value = '1'; // 默认选中 Home
  }
});

// 更新当前标签, 都会触发这个
const handleSelect = (key: string, keyPath: string[]) => {
	// console.log(key, keyPath)
}

// 主题颜色
const themeColors = [
	{ color: '#1b2a47', themeName: '道奇蓝' },
	{ color: '#722ed1', themeName: '深紫罗兰色' },
	{ color: '#eb2f96', themeName: '深粉色' },
	{ color: '#f5222d', themeName: '猩红色' },
	{ color: '#fa541c', themeName: '橙红色' },
	{ color: '#13c2c2', themeName: '绿宝石' },
	{ color: '#52c41a', themeName: '酸橙绿' },
];

const changeThemeColor = (color: string) => {
	layoutThemeColor.value = color;  // 保存主题色
	changeTheme(color);  // 修改 Element Plus 组件主题色
};

</script>

<style lang="scss">
/* 设置靠左 */
.el-menu--horizontal>.el-menu-item:nth-child(1) {
	margin-right: 0;
}

.el-menu--horizontal>.el-menu-item.is-active {
	.el-menu-right {
		border-bottom: 0px solid var(--el-menu-active-color);
		color: var(--el-menu-active-color) !important;
	}
}

.outside-container{
	height: 100vh;
}

.el-main{
	align-items: stretch;
	margin: 0;
	padding: 0;
}

.head {
	margin: 0;
	padding: 0;
	width: 100%;
	display: flex;
	justify-content: space-between;
	/* 分散对齐 */
	align-items: center;
	/* 垂直居中 */
	position: relative;

	/* 设置边框 */
	border-bottom: 1px solid var(--el-border-color);
	background-color: #282828;

	.el-menu {
		/* 菜单背景颜色 */
		--el-menu-bg-color: #282828;
		/* 菜单文字颜色 */
		--el-menu-text-color: #f0f0f0;

		/* 菜单选择后的文字颜色 */
		--active-text-color: var(--el-menu-active-color);

		/* 自定义鼠标悬浮时候的项的样式 */
		.el-menu-item {
			&:hover {
				background-color: #282828; /* 不需要变色 */
			}
		}
	}

	.row-wrapper {
		overflow-x: hidden;
	}

	/* 去掉router-link的下划线 */
	a {
		text-decoration: none;
	}

	.router-link-active {
		text-decoration: none;
	}

}
</style>