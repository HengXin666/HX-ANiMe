<template>
    <div>
        <div ref="chart" style="height: calc(100vh - 60px);"></div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';
import { ElSwitch, ElButton } from 'element-plus';

/**
<div style="margin-bottom: 10px; z-index: 1; position: relative;">
    <el-switch v-model="showAvatars" active-text="显示头像" inactive-text="隐藏头像"></el-switch>
    <el-switch v-model="showNames" active-text="显示名称" inactive-text="隐藏名称"></el-switch>
    <el-button @click="resetView">回到中心</el-button>
</div>
 */

// 引用图表元素
const chart = ref<HTMLElement | null>(null);
// 控制是否显示头像和名称
const showAvatars = ref(true);
const showNames = ref(true);

// ECharts 实例
const myChart = ref<echarts.ECharts | null>(null);

// 图表数据
const webkitDep = {
    nodes: [
        { name: 'CV 1', category: 'CV', image: 'cv1.png' },
        { name: 'Anime 1', category: 'Anime', image: 'anime1.png' },
        { name: 'Character 1', category: 'Character', image: 'char1.png' },
    ],
    categories: [
        { name: 'CV' },
        { name: 'Anime' },
        { name: 'Character' },
    ],
    links: [
        { source: 'CV 1', target: 'Anime 1' },
        { source: 'Anime 1', target: 'Character 1' },
    ],
};

// 检查图片是否存在
async function checkImage(src: string) {
    return new Promise<boolean>((resolve) => {
        const img = new Image();
        img.src = src;
        img.onload = () => resolve(true);
        img.onerror = () => resolve(false);
    });
}

// 创建节点数据
async function createNodeData() {
    return Promise.all(webkitDep.nodes.map(async (node, idx) => {
        const imageExists = await checkImage(node.image);
        return {
            ...node,
            id: idx,
            // 根据是否存在头像选择符号
            symbol: imageExists && showAvatars.value ? 'image' : 'circle',
            symbolSize: imageExists && showAvatars.value ? [40, 40] : [20, 20],
            itemStyle: {
                image: imageExists && showAvatars.value ? node.image : undefined,
                borderColor: '#fff',
                borderWidth: 2,
            },
            label: {
                show: showNames.value,
                formatter: `{b}`,
                position: 'bottom',
            },
        };
    }));
}

// 添加新节点
function addNode() {
    const newNode = {
        name: `新节点 ${webkitDep.nodes.length + 1}`,
        category: 'Character',
        image: 'default.png', // 使用默认图片
    };

    webkitDep.nodes.push(newNode);
    webkitDep.links.push({ source: newNode.name, target: 'CV 1' });

    createNodeData().then(nodeData => {
        if (myChart.value) {
            myChart.value.setOption({
                series: [{
                    data: nodeData,
                    links: webkitDep.links,
                }],
            });
        }
    });
}

// 重置视图到中心
function resetView() {
    if (myChart.value) {
        myChart.value.dispatchAction({
            type: 'dataZoom',
            start: 0,
            end: 100,
        });
        myChart.value.dispatchAction({
            type: 'restore',
        });
    }
}

// 在组件挂载后初始化图表
onMounted(async () => {
    if (chart.value) {
        myChart.value = echarts.init(chart.value); // 初始化图表
        myChart.value.showLoading(); // 显示加载动画

        const nodeData = await createNodeData(); // 获取节点数据

        // 图表配置
        const option = {
            legend: {
                data: ['CV', 'Anime', 'Character'] // 图例数据
            },
            series: [
                {
                    type: 'graph', // 图表类型为图
                    layout: 'force', // 力导向布局
                    animation: true,
                    draggable: true, // 允许拖动节点
                    data: nodeData,
                    categories: webkitDep.categories,
                    force: {
                        edgeLength: 50, // 边的长度
                        repulsion: 100, // 排斥力
                        gravity: 0.2, // 重力
                    },
                    edges: webkitDep.links, // 边的数据
                    label: {
                        position: 'right',
                        formatter: '{b}', // 标签格式
                    },
                },
            ],
            zoom: 1, // 默认缩放级别
        };

        myChart.value.setOption(option); // 设置图表选项
        myChart.value.hideLoading(); // 隐藏加载动画

        // 观察显示名称的变化
        watch(showNames, () => {
            myChart.value?.setOption({
                series: [{
                    label: {
                        show: showNames.value,
                        formatter: `{b}`, // 标签格式
                    },
                }],
            });
        });

        // 观察显示头像的变化
        watch(showAvatars, async () => {
            const nodeData = await createNodeData();
            myChart.value?.setOption({
                series: [{
                    data: nodeData,
                }],
            });
        });

        // 窗口调整时图表自适应
        window.addEventListener('resize', () => {
            myChart.value?.resize();
        });

        // 在组件销毁时移除事件监听器
        onBeforeUnmount(() => {
            window.removeEventListener('resize', () => {
                myChart.value?.resize();
            });
        });
    }
});
</script>

<style lang="scss">
</style>
