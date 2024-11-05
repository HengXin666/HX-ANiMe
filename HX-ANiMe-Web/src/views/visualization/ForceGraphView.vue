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
 * 还需要一个用户id, 和用户的图id(一个用户可能有多个表) | <-- 摊牌了, 不搞企业级, 不搞该死的分库分表..
 * 
 * 结点表:
 *  |(内键)节点id|用户id(外键)|用户表id(外键)|图例id(外键)|原名|译名|imgUrl|描述|
 * 
 * 结点备注表:
 *  // 本表为灵活的数据增量适配, 因为是mysql而不是芒果db
 *  // 如果前端需要添加而外的项, 则先判断是否有这个项的key, 有则改(覆盖); 否则才插入. 
 *  |唯一自增id|用户id(外键)|用户表id(外键)|结点id(外键)|备注key|备注val|
 * 
 * 边集表:
 *  |(内键)边id|用户id(外键)|用户表id(外键)|fromNodeId|toNodeId|
 * 
 * 图表图例表:
 *  |(内键)图例id|用户id(外键)|用户表id(外键)|图例名称|图例颜色|
 * 
 * 用户图表表:
 *  |(内键)用户表id|用户id(外键)|图表名称|图表图标url|图表内容|
 * 
 * 查询过程:
 *  1. 用户登录并且进入到该图例画面
 *  2. 触发图表查询: 根据用户id, 查询到对应的图id(存储到本地, 前端展示信息), 并且默认打开id最小的图(或者不打开?) (如何防止用户篡改id而查询到其他用户的数据?)
 *  3. 如果打开图, 则触发查找:
 *      1) 根据图id、用户id, 查询到该图所有的图例
 *      2) 根据图id、用户id, 查询该图的所有节点和边 (并且动态加载: 后端一次查询, 分片返回/ 多次查询(分页游标查询), 次次返回)
 *      3) 前端同时构建
 * 
 * 创建与增加过程:
 *  - 创建图表 (创建即可(弹出窗口, 填写标题(必填), 内容, url 可空))
 *      | 前端传输时候配上 用户id 和 图 id 即可
 * 
 *  - 创建图例 (创建即可(弹出窗口, 填写名称(必填), 选择颜色))
 *      | 前端传输时候配上 用户id 和 图 id 即可
 * 
 *  - 添加节点 (选择添加节点所属的图例, 结点名称, 其他可空)
 *      | 前端传输时候配上 用户id 和 图 id 即可
 * 
 *  - 添加边 (是否可以通过左键点击一个节点作为开始, 右键一个节点作为终止, 变成一个有向节点)
 *      | 从而得到 fromNodeId 和 toNodeId
 *      | 前端传输时候配上 用户id 和 图 id 即可
 *      - 是否可以通过py调用爬虫接口实现? 只需要番名即可
 *          - py是否是本地实现对接?
 * 
 * 删除过程:
 *  - 删除结点, 传输结点id
 *      | 前端传输时候配上 用户id 和 图 id 即可
 *      - 还有边集, 也直接从后端`join`删除, 防止前端数据造假
 * 
 * - 删除边, 传输边id
 *      | 前端传输时候配上 用户id 和 图 id 即可
 * 
 * - 删除图例, 需要保证当前图上没有人使用这个图例 (前后端双重验证)
 *      | 前端传输时候配上 用户id 和 图 id 即可
 * 
 * - 删除图表, 先是请确定, 然后是, 要求用户输入密码进行确认 (当且仅当图为空的时候允许直接删除 (推荐用户使用改名))
 *      | 前端传输时候配上 用户id 和 图 id 即可
 * 
 * 修改过程:
 *  - 把添加的修改一下就ok
 * 
 * 备注:
 *  mysql分页游标: https://segmentfault.com/a/1190000042064994
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
// https://echarts.apache.org/zh/option.html#series-graph.type
// https://www.hangge.com/blog/cache/detail_3130.html
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
