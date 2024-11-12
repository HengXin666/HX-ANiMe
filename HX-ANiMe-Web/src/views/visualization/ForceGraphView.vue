<template>
    <div>
        <div ref="chart" style="height: calc(100vh - 60px);"></div>

        <!-- 固定在右下角的按钮组 -->
        <div style="position: fixed; right: 30px; bottom: 10px;">
            <el-tooltip content="重置位置" placement="top">
                <el-button circle title="重置" size="large" :icon="RefreshRight" @click="resetPosition"
                    style="margin-bottom: 10px;" />
            </el-tooltip>
            <el-tooltip content="添加结点" placement="top">
                <el-button circle title="添加" size="large" :icon="Plus" @click="openAddNodeDialog"
                    style="margin-bottom: 10px;" />
            </el-tooltip>
            <el-tooltip content="设置" placement="top">
                <el-button circle title="设置" size="large" :icon="Setting" @click="openSettings"
                    style="margin-bottom: 10px;" />
            </el-tooltip>
        </div>

        <!-- 可拖动的弹出窗口: 添加结点 -->
        <el-dialog v-model="addNodeDialogVisible" title="添加结点" :draggable="true" width="550px">
            <div class="dialog-add-node">
                <!-- 结点名称输入框 -->
                <el-form :model="nodeForm" label-width="80">
                    <el-form-item label="结点名称">
                        <el-input v-model="nodeForm.name" placeholder="请输入结点名称"></el-input>
                    </el-form-item>

                    <!-- 结点类型下拉选择框或输入框以及其颜色 -->
                    <el-form-item label="所属图例">
                        <div style="display: flex; align-items: center; width: 100%;">
                            <el-color-picker style="margin-right: 10px;" v-model="legendColor" show-alpha
                                :predefine="predefineColors" />
                            <el-select v-model="nodeForm.category" placeholder="请选择结点所属图例" filterable allow-create
                                @change="categoriesSelectChange">
                                <el-option v-for="item in categoriesNameList" :key="item" :label="item"
                                    :value="item"></el-option>
                            </el-select>
                        </div>
                    </el-form-item>

                    <!-- 输入框 -->
                    <el-form-item label="图片URL">
                        <el-input v-model="inputUrl" placeholder="输入图片URL或粘贴图片到此处" @change="updateImageUrl"
                            @paste="handlePaste" />
                    </el-form-item>

                    <el-form-item>
                        <el-tooltip content="输入URL和上传文件<span style='color: red; font-size: 16px;'>二选一</span>"
                            raw-content placement="left">
                            <!-- 结点图片上传 -->
                            <el-upload drag class="image-uploader" :limit="1" :auto-upload="false"
                                :show-file-list="false" :on-change="updateImage" :on-exceed="updateImageExceed"
                                :accept="'image/*'">
                                <!-- 图片显示框 -->
                                <el-image :src="imageUrl" fit="contain" class="image-preview" v-if="imageUrl" />
                                <div v-else class="image-preview empty">
                                    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                                    <div class="el-upload__text">
                                        拖拽图片到此处或 <em>点击上传</em>
                                    </div>
                                </div>
                            </el-upload>
                        </el-tooltip>
                    </el-form-item>

                    <el-form-item label="结点描述">
                        <el-input v-model="nodeForm.describe" placeholder="请输入结点描述"></el-input>
                    </el-form-item>
                </el-form>
            </div>

            <!-- 底部按钮组 -->
            <template #footer>
                <!-- 高级按钮靠左并带有图标 -->
                <el-button style="float: left; color: #409EFF; font-weight: bold;"
                    @click="ElMessage.warning('高级')">高级</el-button>

                <!-- 取消和确认按钮靠右 -->
                <el-button @click="closeAddNodeDialog">取消</el-button>
                <el-button type="primary" @click="confirmAddNode">确认</el-button>
            </template>
        </el-dialog>

        <!-- 可拖动的弹出窗口: 修改结点 -->
        <el-dialog v-model="nodeUpDataDialogVisible" title="修改结点" :draggable="true" width="550px"
            @close="closeNodeUpDataDialog">
            <div class="dialog-add-node">
                <!-- 结点名称输入框 -->
                <el-form :model="nodeForm" label-width="80">
                    <el-form-item label="结点名称">
                        <el-input v-model="nodeForm.name" placeholder="请输入结点名称"></el-input>
                    </el-form-item>

                    <!-- 结点类型下拉选择框或输入框以及其颜色 -->
                    <el-form-item label="所属图例">
                        <div style="display: flex; align-items: center; width: 100%;">
                            <el-color-picker style="margin-right: 10px;" v-model="legendColor" show-alpha
                                :predefine="predefineColors" />
                            <el-select v-model="nodeForm.category" placeholder="请选择结点所属图例" filterable allow-create
                                @change="categoriesSelectChange">
                                <el-option v-for="item in categoriesNameList" :key="item" :label="item"
                                    :value="item"></el-option>
                            </el-select>
                        </div>
                    </el-form-item>

                    <!-- 输入框 -->
                    <el-form-item label="图片URL">
                        <el-input v-model="inputUrl" placeholder="输入图片URL或粘贴图片到此处" @change="updateImageUrl"
                            @paste="handlePaste" />
                    </el-form-item>

                    <el-form-item>
                        <el-tooltip content="输入URL和上传文件<span style='color: red; font-size: 16px;'>二选一</span>"
                            raw-content placement="left">
                            <!-- 结点图片上传 -->
                            <el-upload drag class="image-uploader" :limit="1" :auto-upload="false"
                                :show-file-list="false" :on-change="updateImage" :on-exceed="updateImageExceed"
                                :accept="'image/*'">
                                <!-- 图片显示框 -->
                                <el-image :src="imageUrl" fit="contain" class="image-preview" v-if="imageUrl" />
                                <div v-else class="image-preview empty">
                                    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                                    <div class="el-upload__text">
                                        拖拽图片到此处或 <em>点击上传</em>
                                    </div>
                                </div>
                            </el-upload>
                        </el-tooltip>
                    </el-form-item>

                    <el-form-item label="结点描述">
                        <el-input v-model="nodeForm.describe" placeholder="请输入结点描述"></el-input>
                    </el-form-item>
                </el-form>
            </div>

            <!-- 底部按钮组 -->
            <template #footer>
                <!-- 取消和确认按钮靠右 -->
                <el-button @click="closeNodeUpDataDialog">取消</el-button>
                <el-button type="primary" @click="confirmUpDataNode">确认</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onBeforeUnmount, markRaw } from 'vue';
import { cloneDeep } from 'lodash';
import * as echarts from 'echarts';
import { ElButton, ElMessage, UploadRawFile, UploadUserFile } from 'element-plus';
import { useSettingStore } from '@/stores/useSettingsStore';
import { SyncArrayMap } from '@/types/syncArrayMap';
import { RefreshRight, Plus, Setting } from '@element-plus/icons-vue';
import { toHtml } from '@/utils/toHtml';

const settingStore = useSettingStore();
const layoutThemeColor = settingStore.theme.color;  // 默认主题色

// 引用图表元素
const chart = ref<HTMLElement | null>(null);

// ECharts 实例
const myChart = ref<echarts.ECharts | null>(null);

// === Begin === 图表数据 === Begin ===

type Node = {
    id: number;
    name: string;
    categoryId: number; 
    category: string;
    img: string;
    describe: string
};

type Category = {
    id: number;
    name: string;
    color: string
};

type Link = {
    id: number;
    source: string;
    target: string
};

// 图表数据
// 数据加载, 分片加载, 自定义http协议, 如果为ing则继续请求(此时带上最大的结点id/边id), 直达为end, 则关闭请求.
const categoriesData: Category[] = [
    { id: 1, name: 'CV', color: '#990099' },
    { id: 2, name: 'Anime', color: '#334455' },
    { id: 3, name: 'Character', color: '#D6B782' },
];

// TODO: 需要先加载 categoriesData, 然后再加载 nodesData
const nodesData: Node[] = [
    { id: 1, name: 'CV 1', categoryId: 1, category: 'CV', img: 'src/views/img/logo/logo.png', describe: '这个是CV' },
    { id: 2, name: 'Anime 1', categoryId: 2, category: 'Anime', img: '', describe: '这个是アニメ' },
    { id: 3, name: 'Character 1', categoryId: 3, category: 'Character', img: '', describe: '这个是角色1' },
    { id: 4, name: 'Character 2', categoryId: 3, category: 'Character', img: '', describe: '这个是角色2' },
];

const linksData: Link[] = [
    { id: 1, source: '1', target: '2' },
    { id: 2, source: '2', target: '3' },
    { id: 3, source: '3', target: '4' },
];

// 创建 SyncArrayMap 实例
const webkitDep = {
    // 节点数据
    nodes: new SyncArrayMap(nodesData),
    // 图例
    categories: new SyncArrayMap(categoriesData),
    // 边集数组
    links: new SyncArrayMap(linksData)
};

// === End === 图表数据 === End ===

// === Begin === 添加边逻辑 === Begin ===

// 边的起点和终点节点 ID
let startNodeId: number | null = null;
let endNodeId: number | null = null;

// === End === 添加边逻辑 === End ===

// 创建力导向图的节点数据
// https://echarts.apache.org/zh/option.html#series-graph.type
// https://www.hangge.com/blog/cache/detail_3130.html
const createForceNodeData = async () => {
    return Promise.all(webkitDep.nodes.getMapList().map(async (node, idx) => {
        return {
            id: node.id,
            name: node.name,
            category: node.category,
            // 根据是否存在头像选择符号
            symbol: node.img !== '' ? 'image://' + node.img : 'circle',
            ...(startNodeId && startNodeId === node.id ? { // 被选中
                symbolSize: [48, 48],
                itemStyle: {
                    borderColor: '#ff0000',  // 节点边框颜色
                    borderWidth: 2,          // 节点边框宽度
                    shadowBlur: 32,          // 阴影模糊半径, 数值越大越向外扩散
                    shadowColor: '#ff0000'   // 阴影颜色, 与边框颜色相同
                },
                label: {
                    normal: {
                        show: true,
                        formatter: `起点`,
                        color: '#ff0000',
                        fontSize: 18,
                        position: 'bottom',
                        fontWeight: 'bold',
                    }
                }
            } : { // 原生
                symbolSize: [48, 48],
                label: {
                    normal: {
                        // show: false,
                        formatter: `{b}\n${node.describe}`,
                        color: webkitDep.categories.getItemById(node.categoryId)?.color,
                        position: 'bottom',
                    }
                }
            }),
        };
    }));
};

// 通过力导向图创建静态图的数据
const createStaticNodeDataFromForce = async () => {
    // 获取当前图表的节点坐标
    const nodeCoordinate = myChart.value?.getModel().getSeriesByIndex(0).getData()._itemLayouts;
    return Promise.all(webkitDep.nodes.getMapList().map(async (node, idx) => {
        return {
            id: node.id,
            name: node.name,
            x: nodeCoordinate[idx][0],
            y: nodeCoordinate[idx][1],
            category: node.category,
            // 根据是否存在头像选择符号
            symbol: node.img !== '' ? 'image://' + node.img : 'circle',
            ...(startNodeId && startNodeId === node.id ? { // 被选中
                symbolSize: [48, 48],
                itemStyle: {
                    borderColor: '#ff0000',  // 节点边框颜色
                    borderWidth: 2,          // 节点边框宽度
                    shadowBlur: 32,          // 阴影模糊半径, 数值越大越向外扩散
                    shadowColor: '#ff0000'   // 阴影颜色, 与边框颜色相同
                },
                label: {
                    normal: {
                        show: true,
                        formatter: `起点`,
                        color: '#ff0000',
                        fontSize: 18,
                        position: 'bottom',
                        fontWeight: 'bold',
                    }
                }
            } : { // 原生
                symbolSize: [48, 48],
                label: {
                    normal: {
                        // show: false,
                        formatter: `{b}\n${node.describe}`,
                        color: webkitDep.categories.getItemById(node.categoryId)?.color,
                        position: 'bottom',
                    }
                }
            }),
        };
    }));
};

// 在组件挂载后初始化图表
onMounted(async () => {
    if (chart.value) {
        // https://juejin.cn/post/7130211001235931167 解决legend残留问题
        myChart.value = markRaw(echarts.init(chart.value)) // 初始化图表
        myChart.value.showLoading(); // 显示加载动画

        const nodeData = await createForceNodeData(); // 获取节点数据

        // 图表配置
        const option = {
            color: webkitDep.categories.getMapList().map(it => it.color), // 图例颜色
            legend: {
                data: webkitDep.categories.getMapList().map(it => it.name), // 图例数据
            },
            // 提示框组件
            tooltip: {
                trigger: "item",
                // 这里支持html! (使用函数就不支持 {b} 格式化了)
                formatter: (params: any) => {
                    if (params.dataType === "node") {
                        return params.data.id + "<br/>";
                    } else if (params.dataType === "edge") {
                        return params.data.source + " --> " + params.data.target;
                    } else {
                        return "不支持显示";
                    }
                },
                // 提示框浮层的背景颜色
                backgroundColor: "rgba(50, 50, 50, 0.5)",
                // 提示框浮层的边框颜色
                borderColor: layoutThemeColor,
                borderWidth: 1,
            },
            series: [
                {
                    type: 'graph', // 图表类型为图
                    layout: 'force', // 力导向布局
                    animation: true, // 开启动画
                    draggable: true, // 允许拖动节点
                    data: nodeData,  // 指定数据
                    categories: webkitDep.categories.getMapList(), // 指定分类
                    force: {
                        edgeLength: [50, 200], // 边的长度
                        repulsion: 100, // 排斥力
                        gravity: 0.025, // 向中心的引力因子
                    },
                    edges: webkitDep.links.getMapList(), // 边的数据
                    roam: true, // ('scale')只允许缩放
                    edgeSymbol: ['circle', 'arrow'], // 箭头样式
                    lineStyle: { // 连线样式
                        color: layoutThemeColor,
                        width: 2,
                        curveness: 0,   // 曲率
                        opacity: 0.75,  // 不透明度
                    },
                    label: {
                        show: false, // 默认隐藏标签
                    },
                    // 问题: 依旧无法显示: 连接附近的结点的标签 (只能手写了事件吗, 每一次O(n^2))
                    emphasis: {
                        focus: 'adjacency', // 高亮当前节点及其相邻节点和边
                        label: {
                            position: 'right', // 高亮时显示标签的位置
                            show: true         // 高亮时显示标签
                        }
                    },
                    // 连线样式
                    edgeLabel: {
                        normal: {
                            show: true,
                            textStyle: {
                                color: layoutThemeColor,
                                fontSize: 14,
                            },
                            formatter: function(param: any) { // 标签内容
                                return webkitDep.nodes.getItemById(param.data.target)?.category;
                            }
                        }
                    }
                },
            ],
        };

        myChart.value.hideLoading(); // 隐藏加载动画
        myChart.value.setOption(option); // 设置图表选项

        // 窗口调整时图表自适应
        window.addEventListener('resize', () => {
            myChart.value?.resize();
        });

        // 取消浏览器默认右键菜单
        document.addEventListener('contextmenu', event => event.preventDefault());

        // 在组件销毁时移除事件监听器
        onBeforeUnmount(() => {
            window.removeEventListener('resize', () => {
                myChart.value?.resize();
            });
        });

        // 添加事件监听, 控制拖拽行为 (不管用, 摊牌了)
        // myChart.value.on('mousedown', {dataType: 'node'}, function (params: any) {
        //     // 判断是否是节点, 并且只允许左键
        //     if (params.event.event.button !== 0) {
        //         // 如果按下的不是左键, 取消拖动事件
        //         ElMessage.info("No!");
        //         params.preventDefault();
        //     }
        // });

        // 鼠标中键 绑定事件
        myChart.value.on('mousedown', { dataType: 'node' }, function (event: any) {
            // 判断是否是节点, 并且只允许左键
            if (event.event.event.button === 1) { // 鼠标中键
                if (startNodeId && startNodeId === event.data.id) {
                    // 再次选择自己, 就是取消选择
                    startNodeId = null;
                } else {
                    if (endNodeId) {
                        ElMessage.warning("请不要在动画时候选择结点");
                        return;
                    }
                    startNodeId = event.data.id;
                }
                createForceNodeData().then(nodeData => {
                    if (myChart.value) {
                        myChart.value.setOption({
                            series: [{
                                data: nodeData,
                            }],
                        });
                    }
                });
            }
        });

        // 鼠标右键 绑定事件
        myChart.value.on('contextmenu', { dataType: 'node' }, function (event: any) {
            if (startNodeId) {
                // 如果已经选择了起始结点, 则设置终点, 连接为边
                if (startNodeId === event.data.id) {
                    // 如果这个为自己, 则不行
                    ElMessage.error("不能以同一个结点为起点和终点");
                    return;
                }
                endNodeId = event.data.id;

                // 防止重边
                for (const it of webkitDep.links.getMapList()) {
                    if (it.source == startNodeId && it.target == endNodeId) {
                        ElMessage.error("边 [" + startNodeId + "-->" + endNodeId + "] 已存在");
                        endNodeId = null;
                        return;
                    }
                }

                const linkNode = {
                    id: webkitDep.links.getMapList().length + 1,
                    source: startNodeId + '',
                    target: endNodeId + '',
                };

                webkitDep.links.push(linkNode);

                // 播放动画: 变为静态图 -> 动画 -> 变回力导向图
                createStaticNodeDataFromForce().then(nodeData => {
                    function switchToForceLayout(zoomLevel: number) {
                        // 重置
                        startNodeId = null;
                        endNodeId = null;
                        createForceNodeData().then(newData => {
                            myChart.value?.setOption({
                                series: [{
                                    type: 'graph',
                                    layout: 'force',
                                    data: newData,
                                    edges: webkitDep.links.getMapList(),
                                    zoom: zoomLevel,
                                }]
                            });
                        });
                    };

                    const zoomLevel: number = myChart.value?.getOption().series[0].zoom;

                    myChart.value?.setOption({
                        series: [{
                            type: 'graph',
                            layout: 'none', // 转换为静态布局
                            data: nodeData, // 使用固定位置的节点
                            zoom: zoomLevel * 0.24, // TODO 此处的 k 可能和力导向公式有关...
                            edges: webkitDep.links.getMapList(),
                        }]
                    });

                    // 播放连边的动画效果 (只能是非力导向图)
                    myChart.value?.dispatchAction({
                        type: 'graphAddLink',
                        link: linkNode,
                    });

                    // 手动延迟 1100ms, 因为动画播放固定位 1s
                    setTimeout(() => switchToForceLayout(zoomLevel), 1100);
                });
            } else {
                // 没有选择起点, 就是修改当前结点信息
                openNodeUpDataDialog(event.data.id);
            }
        });
    }
});

// 添加新节点
const addNode = async (node: Node) => {
    webkitDep.nodes.push(node);
    createForceNodeData().then(nodeData => {
        if (myChart.value) {
            myChart.value.setOption({
                series: [{
                    data: nodeData,
                }],
            });
        }
    });
};

// 重置视图到中心, 并且重置缩放
const resetPosition = () => {
    if (myChart.value) {
        myChart.value.dispatchAction({
            type: 'dataZoom',
            start: 0,
            end: 100,
        });

        myChart.value.setOption({
            series: [{
                zoom: 1,
                center: 1,
                // 其他参数保持不变
            }]
        });
    }
};

// 打开设置的逻辑
const openSettings = () => {
    console.log("打开设置")
};

// === Begin === 添加结点界面逻辑 === Begin ===
const addNodeDialogVisible = ref(false);

const nodeForm = ref({
    // 结点名称
    name: "",
    // 图例
    category: "",
    // 图片Url
    image: "",       // 本地图片预览
    imageUrl: "",    // 网络图片地址
    // 描述
    describe: "",    // 描述
});

// 选择结点图例颜色
const legendColor = ref(layoutThemeColor);
const predefineColors = ref([
    '#ff4500',
    '#ff8c00',
    '#ffd700',
    '#90ee90',
    '#00ced1',
    '#1e90ff',
    '#c71585',
    'rgba(255, 69, 0, 0.68)',
    'rgb(255, 120, 0)',
    'hsv(51, 100, 98)',
    'hsva(120, 40, 94, 0.5)',
    'hsl(181, 100%, 37%)',
    'hsla(209, 100%, 56%, 0.73)',
    '#c7158577',
]);

// 图例类型选项
let categoriesNameList = webkitDep.categories.getMapList().map(it => it.name);

// 刷新图例类型
const refreshCategoriesNameList = () => {
    categoriesNameList = webkitDep.categories.getMapList().map(it => it.name);
};

// 选择结点图例时候触发: 从图例名称选择颜色
const categoriesSelectChange = (categoriesName: string) => {
    const it = webkitDep.categories.getMapList().find(dict => dict.name === categoriesName);
    if (it) {
        legendColor.value = it.color;
    }
};

// 打开弹窗
const openAddNodeDialog = () => {
    refreshCategoriesNameList();
    addNodeDialogVisible.value = true;
};

// 重置表单
const resetAddNodeForm = () => {
    nodeForm.value = { name: "", category: "", image: "", imageUrl: "", describe: "" };
};

// 关闭弹窗
const closeAddNodeDialog = () => {
    addNodeDialogVisible.value = false;
};

// 测试添加结点
const _addNodeTest = () => {
    const node: Node = {
        id: webkitDep.nodes.getMapList().length + 1,
        name: nodeForm.value.name,
        categoryId: (_ => _ ? _ : -1)(webkitDep.categories.getMapList().find(it => it.name === nodeForm.value.category)?.id),
        category: nodeForm.value.category,
        img: nodeForm.value.imageUrl,
        describe: nodeForm.value.describe
    };
    ElMessage.info("添加结点id:" + node.id);
    addNode(node);
};

/**
 * 修改图例
 * @param categoryName 图例名称
 * @param categoryColor 图例颜色
 */
const upDataCategory = (categoryName: string, categoryColor: string) => {
    // 如果找到这个图例, 则不用添加
    const it = webkitDep.categories.getMapList().find(it => it.name === categoryName);
    if (it) {
        it.color = categoryColor;
    } else {
        webkitDep.categories.push({
            // TODO: 之后id应该是从数据库中获得
            id: webkitDep.categories.getMapList().length + 1,
            name: categoryName,
            color: categoryColor
        })
    }
    createForceNodeData().then(nodeData => {
        if (myChart.value) {
            myChart.value.setOption({
                color: webkitDep.categories.getMapList().map(it => it.color), // 全局的图例颜色
                legend: {
                    data: webkitDep.categories.getMapList().map(it => it.name), // 图例数据
                },
                series: [{
                    data: nodeData,
                    categories: webkitDep.categories.getMapList(),
                }]
            });
        }
    });
};

// 确认添加结点
const confirmAddNode = () => {
    if (!nodeForm.value.name) {
        ElMessage.error("结点名称不能为空");
        return;
    } else if (!nodeForm.value.category) {
        ElMessage.error("结点所属图例不能为空");
        return;
    }
    // 在这里可以将数据提交到后端或在图表中添加节点
    console.log("添加结点数据：", nodeForm.value);
    // 处理图例
    upDataCategory(nodeForm.value.category, legendColor.value);
    // 添加结点
    _addNodeTest();
    // http 需要获得结点的唯一id, 如果有图片, 则需要获取到url
    closeAddNodeDialog();
    resetAddNodeForm();
};

// 输入框输入的图片URL
const inputUrl = ref('');
// 显示的图片URL
const imageUrl = ref<string | ArrayBuffer | null>(null);
// 缓存的图片文件
const cachedFile = ref<File | null>(null);

// 更新图片URL
const updateImageUrl = () => {
    imageUrl.value = inputUrl.value || null;
};

// 超过了最大数量
const updateImageExceed = (files: File[], uploadFiles: UploadUserFile[]) => {
    /**
     * 第一个参数是 files (超出的文件列表)
     * 第二个参数是 fileList (当前已经上传的文件列表)
     */
    cachedFile.value = files[files.length - 1];
    previewImage(cachedFile.value);
};

// 通过拖拽或者点击上传的文件
const updateImage = (rawFile: UploadRawFile) => {
    // 你抽什么风? 明明没有错
    cachedFile.value = rawFile.raw;
    previewImage(cachedFile.value);
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

// 从粘贴板中获取图片
const handlePaste = (event: ClipboardEvent) => {
    const items = event.clipboardData?.items;
    if (items) {
        for (let i = 0; i < items.length; ++i) {
            const item = items[i];
            if (item.type.startsWith('image/')) {
                const file = item.getAsFile();
                if (file) {
                    previewImage(file);
                    cachedFile.value = file;
                }
            }
        }
    }
};

// 预览图片
const previewImage = (file: File) => {
    const reader = new FileReader();
    reader.onload = () => {
        imageUrl.value = reader.result;
    };
    reader.onerror = () => {
        ElMessage.error('图片读取失败');
    };
    reader.readAsDataURL(file);
};

// === End === 添加结点逻辑 === End ===

// === Begin === 修改结点逻辑 === Begin ===
/**
 * 此处只是备份, 以便直接复用
 */
const nodeUpDataDialogVisible = ref(false);
const tmpNodeForm = ref({ name: "", category: "", image: "", imageUrl: "", describe: "", });
const tmpLegendColor = ref(layoutThemeColor);
const tmpInputUrl = ref('');
const tmpImageUrl = ref<string | ArrayBuffer | null>(null);
const tmpCachedFile = ref<File | null>(null);

// 当前修改结点的索引
const nowUpDataNodeId = ref(-1);

// 打开弹窗
const openNodeUpDataDialog = (nodeId: number) => {
    refreshCategoriesNameList();
    nodeUpDataDialogVisible.value = true;
    // 切换
    tmpNodeForm.value = cloneDeep(nodeForm.value);
    tmpLegendColor.value = cloneDeep(legendColor.value);
    tmpInputUrl.value = cloneDeep(inputUrl.value);
    tmpImageUrl.value = cloneDeep(imageUrl.value);
    tmpCachedFile.value = cloneDeep(cachedFile.value);
    // 赋值
    const it = webkitDep.nodes.getItemById(nodeId);
    if (it) {
        nodeForm.value.name = it.name;
        nodeForm.value.category = it.category;
        inputUrl.value = it.img;
        nodeForm.value.describe = it.describe;

        // 保存当前结点的索引信息
        nowUpDataNodeId.value = nodeId;
        categoriesSelectChange(it.categoryId);
    } else {
        ElMessage.error("内部错误: 查询不到结点");
    }
};

// 关闭弹窗
const closeNodeUpDataDialog = () => {
    nodeUpDataDialogVisible.value = false;
    // 切换
    nodeForm.value = cloneDeep(tmpNodeForm.value);
    legendColor.value = cloneDeep(tmpLegendColor.value);
    inputUrl.value = cloneDeep(tmpInputUrl.value);
    imageUrl.value = cloneDeep(tmpImageUrl.value);
    cachedFile.value = cloneDeep(tmpCachedFile.value);
};

// 修改结点
const confirmUpDataNode = () => {
    // TODO 同步数据到后端...
    // 如果后端修改成功, 则同步到前端
    const it = webkitDep.nodes.getItemById(nowUpDataNodeId.value);
    if (it) {
        it.name = cloneDeep(nodeForm.value.name);
        it.category = cloneDeep(nodeForm.value.category);
        it.img = cloneDeep(inputUrl.value);
        it.describe = cloneDeep(nodeForm.value.describe);
        // 同步图例颜色修改
        upDataCategory(nodeForm.value.category, legendColor.value);
    }

    closeNodeUpDataDialog();
};

// === End === 修改结点逻辑 === End ===

// 支持可导出: https://echarts.apache.org/zh/api.html#echartsInstance.renderToSVGString
</script>

<style lang="scss">
.dialog-add-node {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .image-uploader {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 10px;
    }

    .image-preview {
        width: 400px;
        height: 150px;
        display: flex;
        flex-direction: column; // 垂直排列
        align-items: center;
        justify-content: center;
    }
}
</style>
