<template>
	<el-container>
		<el-header class="head">
			<el-menu 
				:default-active="activeIndex" 
				class="el-menu-head" 
				mode="horizontal" 
				:ellipsis="false" 
				@select="handleSelect"
			>
				<el-menu-item index="0">
					<img width="100" src="@/views/img/logo/HX-ANiMe.png" alt="Logo"></img>
				</el-menu-item>
				<el-menu-item index="1">
					Home
				</el-menu-item>
			</el-menu>

			<div class="switch-dark">
				<el-switch v-model="isDark" @change="toggleDark" size="large">
					<template #active-action>
						<el-icon><Moon color="{{ changeTheme }}"/></el-icon>
					</template>
					<template #inactive-action>
						<el-icon><Sunny color="{{ changeTheme }}"/></el-icon>
					</template>
				</el-switch>
			</div>
		</el-header>
		
		<!-- 内容区域 -->
		<el-main>
			<router-view />
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

import { ref } from 'vue'
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
// 更新当前标签, 都会触发这个
const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
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
.el-menu--horizontal > .el-menu-item:nth-child(1) {
	margin-right: 0;
}

.head {
	display: flex;
	justify-content: space-between; /* 分散对齐 */
	align-items: center; /* 垂直居中 */
	position: relative; /* 为了绝对定位 */

	.switch-dark {
		position: absolute;
		top: 10px; /* 调整距离顶部的距离 */
		right: 10px; /* 调整距离右边的距离 */
	}
}
</style>