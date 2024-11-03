<template>
    <div style="width: 100%; height: 80%;">
        <div style="margin-bottom: 10px;">
            <el-switch v-model="showAvatars" active-text="显示头像" inactive-text="隐藏头像"></el-switch>
            <el-switch v-model="showNames" active-text="显示名称" inactive-text="隐藏名称"></el-switch>
            <el-button @click="resetView">回到中心</el-button>
        </div>
        <div style="width: 100%; height: 80%;">
            <div ref="chart" style="width: 100%; height: 80%;"></div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import * as echarts from 'echarts';
import { ElSwitch, ElButton } from 'element-plus';

const chart = ref<HTMLElement | null>(null);
const showAvatars = ref(true);
const showNames = ref(true);
const myChart = ref<echarts.ECharts | null>(null);

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

async function checkImage(src: string) {
    return new Promise<boolean>((resolve) => {
        const img = new Image();
        img.src = src;
        img.onload = () => resolve(true);
        img.onerror = () => resolve(false);
    });
}

async function createNodeData() {
    return Promise.all(webkitDep.nodes.map(async (node, idx) => {
        const imageExists = await checkImage(node.image);
        return {
            ...node,
            id: idx,
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

onMounted(async () => {
    if (chart.value) {
        myChart.value = echarts.init(chart.value);
        myChart.value.showLoading();

        const nodeData = await createNodeData();

        const option = {
            legend: {
                data: ['CV', 'Anime', 'Character']
            },
            series: [
                {
                    type: 'graph',
                    layout: 'force',
                    animation: true,
                    draggable: true,
                    data: nodeData,
                    categories: webkitDep.categories,
                    force: {
                        edgeLength: 50,
                        repulsion: 100,
                        gravity: 0.2,
                    },
                    edges: webkitDep.links,
                    label: {
                        position: 'right',
                        formatter: '{b}',
                    },
                },
            ],
            zoom: 1, // 默认缩放级别
            // 允许拖动
            draggable: true,
        };

        myChart.value.setOption(option);
        myChart.value.hideLoading();

        // setInterval(() => addNode(), 5000); // 每5秒增加一个节点

        watch(showNames, () => {
            myChart.value?.setOption({
                series: [{
                    label: {
                        show: showNames.value,
                        formatter: `{b}`,
                    },
                }],
            });
        });

        watch(showAvatars, async () => {
            const nodeData = await createNodeData();
            myChart.value?.setOption({
                series: [{
                    data: nodeData,
                }],
            });
        });

        window.addEventListener('resize', () => {
            myChart.value?.resize();
        });
    }
});
</script>

<style lang="scss">
</style>
