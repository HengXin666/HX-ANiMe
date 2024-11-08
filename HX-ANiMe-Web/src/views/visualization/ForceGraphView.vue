<template>
    <div>
        <div ref="chart" style="height: calc(100vh - 60px);"></div>

        <!-- 固定在右下角的按钮组 -->
        <div style="position: fixed; right: 30px; bottom: 10px;">
            <el-tooltip content="重置位置" placement="top">
                <el-button circle title="重置" size="large" :icon="RefreshRight" @click="resetPosition" style="margin-bottom: 10px;" />
            </el-tooltip>
            <el-tooltip content="添加结点" placement="top">
                <el-button circle title="添加" size="large" :icon="Plus" @click="openDialog" style="margin-bottom: 10px;" />
            </el-tooltip>
            <el-tooltip content="设置" placement="top">
                <el-button circle title="设置" size="large" :icon="Setting" @click="openSettings" style="margin-bottom: 10px;" />
            </el-tooltip>
        </div>

        <!-- 可拖动的弹出窗口 -->
        <el-dialog
            v-model="dialogVisible"
            title="添加结点"
            :draggable="true"
            width="500px"
            @close="resetForm"
        >
            <div>
                <!-- 结点名称输入框 -->
                <el-form :model="nodeForm" label-width="100px">
                    <el-form-item label="结点名称">
                        <el-input v-model="nodeForm.name" placeholder="请输入结点名称"></el-input>
                    </el-form-item>

                    <!-- 结点类型下拉选择框或输入框 -->
                    <el-form-item label="结点类型">
                        <el-select v-model="nodeForm.categories" placeholder="请选择结点类型" filterable allow-create>
                            <el-option
                                v-for="item in nodeTypes"
                                :key="item"
                                :label="item"
                                :value="item"
                            ></el-option>
                        </el-select>
                    </el-form-item>

                    <!-- 结点图片上传 -->
                    <el-form-item label="结点图片">
                        <el-upload
                            class="upload-demo"
                            action=""
                            :on-change="handleFileChange"
                            :show-file-list="false"
                            :before-upload="beforeUpload"
                        >
                            <el-button size="small" type="primary">点击上传图片</el-button>
                        </el-upload>
                        <div v-if="nodeForm.image" style="margin-top: 10px;">
                            <img :src="nodeForm.image" alt="结点图片" style="width: 100px; height: auto;" />
                        </div>
                    </el-form-item>

                    <!-- 从网络导入 -->
                    <el-form-item label="网络图片">
                        <el-input v-model="nodeForm.imageUrl" placeholder="请输入网络图片地址"></el-input>
                        <el-button @click="importFromUrl" style="margin-top: 5px;">导入图片</el-button>
                    </el-form-item>
                </el-form>
            </div>

            <!-- 底部按钮组 -->
            <template #footer>
                <el-button @click="closeDialog">取消</el-button>
                <el-button type="primary" @click="confirmAddNode">确认</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onBeforeUnmount, defineProps, markRaw } from 'vue';
import * as echarts from 'echarts';
import { useSettingStore } from '@/stores/useSettingsStore';
import { RefreshRight, Plus, Setting } from '@element-plus/icons-vue';
import { ElButton, ElMessage } from 'element-plus';

// 断言结点类型
interface Node {
    id: number;
    name: string;
    category: string;
    img: string;
    describe: string;
};

const resetPosition = () => {
    // 重置视图到中心
    if (myChart.value) {
        // myChart.value.dispatchAction({
        //     type: 'dataZoom',
        //     start: 0,
        //     end: 100,
        // });
        myChart.value.dispatchAction({
            type: 'restore',
        });
    }
};

const openSettings = () => {
    // 打开设置的逻辑
    console.log("打开设置")
};

/**
<div style="margin-bottom: 10px; z-index: 1; position: relative;">
    <el-switch v-model="showAvatars" active-text="显示头像" inactive-text="隐藏头像"></el-switch>
    <el-switch v-model="showNames" active-text="显示名称" inactive-text="隐藏名称"></el-switch>
    <el-button @click="resetView">回到中心</el-button>
</div>
*/

const settingStore = useSettingStore();
const layoutThemeColor = settingStore.theme.color;  // 默认主题色

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
 *  |(内键)节点id|用户id(外键)|用户表id(外键)|图例id(外键)|名称|imgUrl|描述|
 *  - 注: 名称附带样式, 这样如果需要什么注音的也可以搞
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
// 数据加载, 分片加载, 自定义http协议, 如果为ing则继续请求(此时带上最大的结点id/边id), 直达为end, 则关闭请求.
const webkitDep = {
    // 节点数据
    nodes: [
        { id: 1, name: 'CV 1', category: 'CV', img: 'src/views/img/logo/logo.png', describe: '这个是CV', },
        { id: 2, name: 'Anime 1', category: 'Anime', img: '', describe: '这个是アニメ', },
        { id: 3, name: 'Character 1', category: 'Character', img: '', describe: '这个是角色', },
    ],
    // 图例
    categories: [
        { id: 1, name: 'CV', color: '#990099' },
        { id: 2, name: 'Anime', color: '#ff9' },
        { id: 3, name: 'Character', color: 'yellow' },
    ],
    // 边集数组
    links: [
        { id: 1, source: '1', target: '2' },
        { id: 2, source: '2', target: '3' },
    ],
};


// 创建节点数据
// https://echarts.apache.org/zh/option.html#series-graph.type
// https://www.hangge.com/blog/cache/detail_3130.html
const createNodeData = async () => {
    return Promise.all(webkitDep.nodes.map(async (node, idx) => {
        return {
            id: node.id,
            name: node.name,
            category: node.category,
            // 根据是否存在头像选择符号
            symbol: node.img !== '' ? 'image://' + node.img : 'circle',
            symbolSize: [48, 48],
            itemStyle: {
                borderColor: '#fff',
                borderWidth: 0,
            },
            label: {
                normal: {
                    show: null, // showNames.value,
                    formatter: `{b}\n${node.describe}`,
                    color: '#f9',
                    position: 'bottom',
                }
            },
        };
    }));
}

// const createLegendData = async () => {
//     return Promise.all(webkitDep.categories.map(async (legend, idx) => {
//         return {
//             name: legend.name,
//             itemStyle: {
//                 color: legend.color
//             }
//         };
//     }));
// };

// 在组件挂载后初始化图表
onMounted(async () => {
    if (chart.value) {
        // https://juejin.cn/post/7130211001235931167 解决legend残留问题
        myChart.value = markRaw(echarts.init(chart.value)) // 初始化图表
        myChart.value.showLoading(); // 显示加载动画

        const nodeData = await createNodeData(); // 获取节点数据

        // 图表配置
        const option = {
            color: webkitDep.categories.map(it => it.color), // 图例颜色
            legend: {
                data: webkitDep.categories.map(it => it.name), // 图例数据
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
                        edgeLength: [50, 200], // 边的长度
                        repulsion: 100, // 排斥力
                        gravity: 0.025, // 向中心的引力因子
                    },
                    edges: webkitDep.links, // 边的数据
                    roam: true, // ('scale')只允许缩放
                    edgeSymbol: ['circle', 'arrow'], // 箭头样式
                    // 连线样式
                    lineStyle: {
                        color: layoutThemeColor,
                        width: 2,
                    },
                },
            ],
        };

        myChart.value.hideLoading(); // 隐藏加载动画
        myChart.value.setOption(option); // 设置图表选项

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
const addNode = async (node: Node) => {
    const newNode = {
        id: '1xx', // 不对
        name: `新节点 ${webkitDep.nodes.length + 1}`,
        category: 'Character',
        image: 'default.png', // 使用默认图片
    };

    // webkitDep.nodes.push(newNode);
    // webkitDep.links.push({  source: newNode.name, target: 'CV 1' });

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

// === Begin === 添加结点逻辑 === Begin ===
const dialogVisible = ref(false);
const nodeForm = ref({
    // 结点名称
    name: "",
    // 图例
    categories: "",
    // 图片Url
    image: "",       // 本地图片预览
    imageUrl: "",    // 网络图片地址
});

// 结点类型选项
const nodeTypes = webkitDep.categories.map(it => it.name);

// 打开弹窗
const openDialog = () => {
    dialogVisible.value = true;
};

// 重置表单
const resetForm = () => {
    nodeForm.value = { name: "", categories: "", image: "", imageUrl: "" };
};

// 关闭弹窗
const closeDialog = () => {
    dialogVisible.value = false;
    resetForm();
};

// 确认添加结点
const confirmAddNode = () => {
    if (!nodeForm.value.name) {
        ElMessage.error("结点名称不能为空");
        return;
    }
    // 在这里可以将数据提交到后端或在图表中添加节点
    console.log("添加结点数据：", nodeForm.value);
    // http 需要获得结点的唯一id
    closeDialog();
};

// 处理图片文件上传
const handleFileChange = (file: File) => {
    const reader = new FileReader();
    reader.onload = (event) => {
        nodeForm.value.image = event.target?.result as string;
    };
    // reader.readAsDataURL(file.raw);
};

// 限制文件类型和大小
const beforeUpload = (file: File) => {
    const isImage = file.type.startsWith("image/");
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isImage) {
        ElMessage.error("只能上传图片文件");
    }
    if (!isLt2M) {
        ElMessage.error("上传图片大小不能超过 2MB");
    }
    return isImage && isLt2M;
};

// 从网络导入图片
const importFromUrl = () => {
    if (nodeForm.value.imageUrl) {
        nodeForm.value.image = nodeForm.value.imageUrl;
    } else {
        ElMessage.warning("请输入有效的图片地址");
    }
};

// === End === 添加结点逻辑 === End ===

// 支持可导出: https://echarts.apache.org/zh/api.html#echartsInstance.renderToSVGString
</script>

<style lang="scss"></style>
