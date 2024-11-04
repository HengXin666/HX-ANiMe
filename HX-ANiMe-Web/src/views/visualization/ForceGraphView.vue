<template>
    <div>
        <div ref="chart" style="height: calc(100vh - 60px);"></div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onBeforeUnmount, defineProps } from 'vue';
import * as echarts from 'echarts';
import { ElSwitch, ElButton } from 'element-plus';

/**
<div style="margin-bottom: 10px; z-index: 1; position: relative;">
    <el-switch v-model="showAvatars" active-text="显示头像" inactive-text="隐藏头像"></el-switch>
    <el-switch v-model="showNames" active-text="显示名称" inactive-text="隐藏名称"></el-switch>
    <el-button @click="resetView">回到中心</el-button>
</div>
*/

const props = defineProps({
    layoutThemeColor: String, // 接收主题颜色
});

// 引用图表元素
const chart = ref<HTMLElement | null>(null);
// 控制是否显示头像和名称
const showAvatars = ref(true);
const showNames = ref(true);

// ECharts 实例
const myChart = ref<echarts.ECharts | null>(null);

/**
 * 后端存储架构 (到时候字段名统一一下嗷)
 * 
 * 结点表:
 *  唯一自增id
 *  类型:
 *  - anime 结点
 *      - animeName (原名)
 *      - animeNameByCN (中文名)
 *      - img (番剧图片)
 *      - describe (描述)
 * 
 *  - kyara(角色) 结点
 *      - kyaraName (原名)
 *      - kyaraNameByCN (中文名)
 *      - img (角色图片)
 *      - describe (描述)

 *  - CV结点
 *      - CVName (原名)
 *      - CVNameByCN (中文名)
 *      - img (图片)
 *      - describe (描述)
 * 
 * 结点备注表:
 *  // 本表为灵活的数据增量适配, 因为是mysql而不是芒果db
 *  唯一自增id, 结点表id, 备注key, 备注val
 *  // 如果前端需要添加而外的项, 则先判断是否有这个项的key, 有则改(覆盖); 否则才插入. 
 * 
 * 边集表:
 *  key, fromId, toId
 */

// 图表数据
const webkitDep = {
    // 节点数据
    nodes: [
        { name: 'CV 1', category: 'CV' },
        { name: 'Anime 1', category: 'Anime' },
        { name: 'Character 1', category: 'Character' },
    ],
    // 图例
    categories: [
        { name: 'CV' },
        { name: 'Anime' },
        { name: 'Character' },
    ],
    // 边集数组
    links: [
        { source: 'CV 1', target: 'Anime 1' },
        { source: 'Anime 1', target: 'Character 1' },
    ],
};


// 创建节点数据
async function createNodeData() {
    return Promise.all(webkitDep.nodes.map(async (node, idx) => {
        return {
            ...node,
            // 根据是否存在头像选择符号
            symbolSize: [40, 40],
            itemStyle: {
                image: undefined,
                borderColor: '#fff',
                borderWidth: 0,
            },
            label: {
                // show: showNames.value,
                formatter: `{b}`,
                position: 'bottom',
            },
        };
    }));
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
                data: ['CV', 'Anime', 'Character'], // 图例数据
            },
            series: [
                {
                    type: 'graph', // 图表类型为图
                    layout: 'force', // 力导向布局
                    animation: true, // 开启动画
                    draggable: true, // 允许拖动节点
                    data: nodeData,  // 指定数据
                    categories: webkitDep.categories, // 指定分类
                    force: {
                        edgeLength: 100, // 边的长度
                        repulsion: 100, // 排斥力
                        gravity: 0.1, // 重力
                    },
                    edges: webkitDep.links, // 边的数据
                    roam: 'scale', // 只允许缩放
                    edgeSymbol: ['circle', 'arrow'], // 箭头样式
                    // 连线样式
                    lineStyle: {
                        color: props.layoutThemeColor,
                        width: 2,
                    },
                },
            ],
        };

        myChart.value.setOption(option); // 设置图表选项

        myChart.value.hideLoading(); // 隐藏加载动画

        // 观察显示名称的变化
        // watch(showNames, () => {
        //     myChart.value?.setOption({
        //         series: [{
        //             label: {
        //                 show: showNames.value,
        //                 formatter: `{b}`, // 标签格式
        //             },
        //         }],
        //     });
        // });

        // 观察显示头像的变化
        // watch(showAvatars, async () => {
        //     const nodeData = await createNodeData();
        //     myChart.value?.setOption({
        //         series: [{
        //             data: nodeData,
        //         }],
        //     });
        // });

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
</script>

<style lang="scss">
</style>
