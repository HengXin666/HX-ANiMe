<template>
    <el-container>
        <!-- 左侧滚动栏 -->
        <el-aside class="chart-list">
            <el-scrollbar>
                <div v-for="chart in charts" :key="chart.id" class="chart-item" @click="handleChartClick(chart.id)">
                    <el-image :src="chart.iconUrl" fit="cover" class="chart-icon" />
                    <div class="chart-info">
                        <h3 class="chart-name">{{ chart.name }}</h3>
                        <p class="chart-description">{{ getFirstSentence(chart.description) }}</p>
                    </div>
                </div>
            </el-scrollbar>
        </el-aside>

        <!-- 主内容区域 -->
        <el-main>
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
                    <div v-if="addNodeAdvancedOptions" class="dialog-add-node">
                        <el-form-item label="Secret API Key">
                            <div style="display: flex; align-items: center; width: 100%;">
                                <el-input v-model="apiKey" readonly style="margin-right: 10px;" />
                                <el-icon @click="copyApiKey" style="right: 35px;">
                                    <DocumentCopy />
                                </el-icon>
                                <el-button @click="fetchApiKey">获取</el-button>
                            </div>
                        </el-form-item>
                    </div>
                    <div v-else class="dialog-add-node">
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
                                    <el-select v-model="nodeForm.category" placeholder="请选择结点所属图例" filterable
                                        allow-create @change="categoriesSelectChange">
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
                            @click="addNodeAdvancedOptions = !addNodeAdvancedOptions">{{ addNodeAdvancedOptions ? "简单添加"
                                :
                                "高级选项" }}</el-button>

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
                                    <el-select v-model="nodeForm.category" placeholder="请选择结点所属图例" filterable
                                        allow-create @change="categoriesSelectChange">
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
                        <!-- 删除结点按钮 -->
                        <el-button style="float: left; color: red; font-weight: bold;"
                            @click="removeNodeLogic">删除结点</el-button>

                        <!-- 取消和确认按钮靠右 -->
                        <el-button @click="closeNodeUpDataDialog">取消</el-button>
                        <el-button type="primary" @click="confirmUpDataNode">确认</el-button>
                    </template>
                </el-dialog>
            </div>
        </el-main>
    </el-container>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onBeforeUnmount, markRaw, h } from 'vue';
import { cloneDeep } from 'lodash';
import * as echarts from 'echarts';
import { ElButton, ElMessage, ElMessageBox, UploadRawFile, UploadUserFile } from 'element-plus';
import { useSettingStore } from '@/stores/useSettingsStore';
import { SyncArrayMap } from '@/types/syncArrayMap';
import * as api from '@/apis/forceGraph';
import { RefreshRight, Plus, Setting, DocumentCopy } from '@element-plus/icons-vue';

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
    describe: string;
};

type Category = {
    id: number;
    name: string;
    color: string;
};

type Link = {
    id: number;
    source: string;
    target: string;
};

// 图表数据
// 数据加载, 分片加载, 自定义http协议, 如果为ing则继续请求(此时带上最大的结点id/边id), 直达为end, 则关闭请求.
const _categoriesData: Category[] = [];
[
    { id: 1, name: 'CV', color: '#990099' },
    { id: 2, name: 'Anime', color: '#334455' },
    { id: 3, name: 'Character', color: '#D6B782' },
];

// TODO: 需要先加载 categoriesData, 然后再加载 nodesData
const _nodesData: Node[] = [];
[
    { id: 1, name: 'CV 1', categoryId: 1, category: 'CV', img: 'src/views/img/logo/logo.png', describe: '这个是CV' },
    { id: 2, name: 'Anime 1', categoryId: 2, category: 'Anime', img: '', describe: '这个是アニメ' },
    { id: 3, name: 'Character 1', categoryId: 3, category: 'Character', img: '', describe: '这个是角色1' },
    { id: 4, name: 'Character 2', categoryId: 3, category: 'Character', img: '', describe: '这个是角色2' },
];

const _linksData: Link[] = [];
[
    { id: 1, source: '1', target: '2' },
    { id: 2, source: '2', target: '3' },
    { id: 3, source: '3', target: '4' },
];

// 当前图的id
const nowGraphId = 1;

// 创建 SyncArrayMap 实例
const webkitDep = {
    // 节点数据
    nodes: new SyncArrayMap(_nodesData),
    // 图例
    categories: new SyncArrayMap(_categoriesData),
    // 边集数组
    links: new SyncArrayMap(_linksData)
};

// === End === 图表数据 === End ===

// === Begin === 左侧边栏数据 === Begin ===

// 左侧图表列表
const charts = ref<Array<{ id: number; iconUrl: string; name: string; description: string }>>([]);

// 从后端加载图表数据
const loadCharts = () => {
    // 模拟从后端获取数据
    charts.value = [
        {
            id: 1,
            iconUrl: "https://via.placeholder.com/50",
            name: "图表1",
            description: "这是第一个图表的简介。1111111111111",
        },
        {
            id: 2,
            iconUrl: "https://via.placeholder.com/50",
            name: "图表2",
            description: "这是第二个图表的简介。",
        },
    ];
};

loadCharts();

// 提取描述的第一句话
const getFirstSentence = (content: string) => {
    try {
        return content.slice(0, 16) + (content.length > 16 ? "..." : "");
    } catch {
        return "无效的内容格式";
    }
};

const handleChartClick = (id: number) => {
    console.log(`点击图表 ID: ${id}`);
    // 这里调用后端接口，根据 ID 获取详细信息
};

// === End === 左侧边栏数据 === End ===


// === Begin === 网络加载数据 (init) === Begin ===

// 异步初始化图表数据
const initCategory = (cb: any) => {
    api.getCategory(nowGraphId, (data: any) => {
        const categoriesData: Category[] = [];
        for (const it of data) {
            categoriesData.push({
                id: it.legendId,
                name: it.legendName,
                color: it.legendColor
            })
        }
        webkitDep.categories = new SyncArrayMap(categoriesData);
        initNodes();
        initLinks();
        cb();
    }, () => {
        ElMessage.error("大错特错");
    });
};

// 异步初始化结点数据
const initNodes = () => {
    api.getNodes(nowGraphId, (data: any) => {
        const nodesData: Node[] = [];
        for (const it of data) {
            nodesData.push({
                id: it.nodeId,
                categoryId: it.legendId,
                name: it.name,
                category: webkitDep.categories.getItemById(it.legendId)?.name,
                img: it.imgUrl,
                describe: it.description,
            })
        }
        webkitDep.nodes = new SyncArrayMap(nodesData);

        // 刷新图
        addNodeToChart(null);
    }, () => {
        ElMessage.error("初始化结点出错");
    });
};

// 异步初始化边数据
const initLinks = () => {
    api.getLinks(nowGraphId, (data: any) => {
        const linksData: Link[] = [];
        for (const it of data) {
            linksData.push({
                id: it.edgeId,
                source: it.fromNodeId + '',
                target: it.toNodeId + ''
            })
        }
        webkitDep.links = new SyncArrayMap(linksData);

        // 刷新图
        addNodeToChart(null);
    }, () => {
        ElMessage.error("初始化边集出错");
    });
};

// === End === 网络加载数据 (init) === End ===

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

        // 网络加载
        initCategory(() => {
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
                                formatter: function (param: any) { // 标签内容
                                    return webkitDep.nodes.getItemById(param.data.target)?.category;
                                }
                            }
                        }
                    },
                ],
            };

            myChart.value?.hideLoading(); // 隐藏加载动画
            myChart.value?.setOption(option); // 设置图表选项
        });

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
        myChart.value.on('mousedown', { dataType: 'node' }, (event: any) => {
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

        // 节点 鼠标右键 绑定事件
        myChart.value.on('contextmenu', { dataType: 'node' }, (event: any) => {
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
                    id: 0,
                    source: startNodeId + '',
                    target: endNodeId + '',
                };

                // 添加边, 后端同步
                api.addLink(nowGraphId, {
                    edgeId: 0,
                    fromNodeId: linkNode.source,
                    toNodeId: linkNode.target
                }, (data: any) => {
                    console.log(data);
                    linkNode.id = data;

                    webkitDep.links.push(linkNode);

                    // 播放动画: 变为静态图 -> 动画 -> 变回力导向图
                    createStaticNodeDataFromForce().then(nodeData => {
                        function switchToForceLayout (zoomLevel: number) {
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
                }, () => {

                });
            } else {
                // 没有选择起点, 就是修改当前结点信息
                openNodeUpDataDialog(event.data.id);
            }
        });

        // 连线的 鼠标右键 绑定事件
        myChart.value.on('contextmenu', { dataType: 'edge' }, (event: any) => {
            const edgeId: number = event.data.id;
            ElMessageBox({
                title: '确实删除边',
                message: h('p', null, [
                    h('span', null, '您真的要删除边: '),
                    h('span', { style: 'color: ' + layoutThemeColor }, webkitDep.nodes.getItemById(event.data.source)?.name),
                    h('span', null, ' --> '),
                    h('span', { style: 'color: ' + layoutThemeColor }, webkitDep.nodes.getItemById(event.data.target)?.name),
                    h('b', { style: 'color: red' }, " ( id = " + edgeId + " )"),
                    h('span', null, ' 吗?'),
                ]),
                showCancelButton: true,
                confirmButtonText: '确认',
                cancelButtonText: '取消',
                type: 'info',
                beforeClose: (action, instance, done) => {
                    if (action === 'confirm') {
                        instance.confirmButtonLoading = true;
                        instance.confirmButtonText = '正在删除...';
                        // 假设这里你调用了某个 API 来删除节点
                        removeEdge(edgeId).then(() => {
                            // 假设删除成功, 关闭弹框
                            done();
                            ElMessage.success('删除成功');
                            closeNodeUpDataDialog();
                            // 播放删除动画
                            createForceNodeData().then(newData => {
                                myChart.value?.setOption({
                                    series: [{
                                        type: 'graph',
                                        data: newData,
                                        edges: webkitDep.links.getMapList(),
                                    }]
                                });
                            });
                        }).catch(() => {
                            // 如果删除失败, 显示错误信息并不关闭弹框
                            done();
                            ElMessage.error('网络错误，删除失败');
                        }).finally(() => {
                            // 不管成功还是失败, 停止加载状态
                            instance.confirmButtonLoading = false;
                        });
                    } else {
                        done();
                    }
                },
            });
        });
    }
});

// 添加新节点并且刷新图, 如果添加为null, 则只刷新图 
const addNodeToChart = async (node: Node | null) => {
    if (node !== null)
        webkitDep.nodes.push(node);
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
                    edges: webkitDep.links.getMapList(),
                }],
            });
        }
    });
};

// 重置视图到中心, 并且重置缩放
const resetPosition = () => {
    if (myChart.value) {
        // myChart.value.dispatchAction({
        //     type: 'dataZoom',
        //     start: 0,
        //     end: 100,
        // });

        myChart.value.setOption({
            series: [{
                zoom: 1,
                // center: 1, // 不要这个, 不然严重的显示bug!!!
                // 其他参数保持不变
            }]
        });
    }
};

// 打开设置的逻辑
const openSettings = () => {
    console.log("打开设置");
};

// === Begin === 添加结点界面逻辑 === Begin ===
// 是否显示添加结点界面
const addNodeDialogVisible = ref(false);

// === Begin === 高级选项界面逻辑 === Begin ===
// 是否处于高级选项界面
const addNodeAdvancedOptions = ref(false);
const apiKey = ref<string>(''); // 定义 API Key，初始为空

// 获取 API Key 的函数
const fetchApiKey = () => {
    // TODO 在这里可以调用接口获取 API Key
    apiKey.value = "your-secret-api-key"; // 模拟赋值
};

// 复制 API Key 到剪贴板
const copyApiKey = () => {
    if (apiKey.value !== "") {
        navigator.clipboard.writeText(apiKey.value).then(() => {
            ElMessage.success("已复制API Key到剪贴板");
        }).catch(() => {
            ElMessage.error("复制失败");
        });
    } else {
        ElMessage.warning("请先获取API Key");
    }
};
// === End === 高级选项界面逻辑 === End ===

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

/* // 测试添加结点
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
    addNodeToChart(node);
}; 
*/

// 上传图片到后端, 获取到url
const uploadImgFromNet = (cb: Function) => {
    api.uploadImg(nowGraphId, cachedFile.value, (url: any) => {
        nodeForm.value.imageUrl = cloneDeep(url);
        cachedFile.value = null;
        cb();
    }, () => {
        ElMessage.error("上传图片出错");
    });
};

// 添加结点, 并且向后端同步
const addNodeFromNet = (categoryId: number) => {
    const node: Node = {
        id: 0,
        name: nodeForm.value.name,
        categoryId: categoryId,
        category: nodeForm.value.category,
        img: nodeForm.value.imageUrl,
        describe: nodeForm.value.describe
    };

    // 添加结点
    api.addNode(nowGraphId, {
        nodeId: 0,
        legendId: node.categoryId,
        name: node.name,
        imgUrl: node.img,
        description: node.describe
    }, (id: number) => {
        node.id = id;
        ElMessage.info("添加结点id:" + node.id);
        addNodeToChart(node);
    }, () => {

    });
};

// 修改结点, 并且同步到后端
const updateNodeFromNet = (categoryId: number) => {
    const node: Node = {
        id: nowUpDataNodeId.value,
        name: nodeForm.value.name,
        categoryId: categoryId,
        category: nodeForm.value.category,
        img: nodeForm.value.imageUrl,
        describe: nodeForm.value.describe
    };

    // 修改结点
    api.updateNode(nowGraphId, {
        nodeId: node.id,
        legendId: node.categoryId,
        name: node.name,
        imgUrl: node.img,
        description: node.describe
    }, () => {
        ElMessage.info("修改结点id:" + node.id);

        const it = webkitDep.nodes.getItemById(node.id);
        if (it) {
            it.name = cloneDeep(nodeForm.value.name);
            it.category = cloneDeep(nodeForm.value.category);
            it.img = cloneDeep(node.img);
            it.describe = cloneDeep(nodeForm.value.describe);
        }

        // 刷新界面
        addNodeToChart(null);

        // 关闭窗口 @TODO 这里不应该这样写, 但是偷懒了: 屎山 +1 
        closeNodeUpDataDialog();
    }, () => {

    });
};

/**
 * 新增图例和结点
 * @param categoryName 图例名称
 * @param categoryColor 图例颜色
 */
const addCategoryAndNode = (categoryName: string, categoryColor: string) => {
    // 如果找到这个图例, 则不用添加
    const it = webkitDep.categories.getMapList().find(it => it.name === categoryName);
    if (it) {
        it.color = categoryColor;
        // 添加结点
        addNodeFromNet(it.id);
    } else {
        // 更新图例, 从后端获取到图例的 id
        const newCategoryIt = {
            // TODO: 之后id应该是从数据库中获得
            id: 0,
            name: categoryName,
            color: categoryColor
        };
        api.addCategory(nowGraphId, {
            legendId: 0,
            legendName: newCategoryIt.name,
            legendColor: newCategoryIt.color
        }, (id: number) => {
            newCategoryIt.id = id;
            webkitDep.categories.push(newCategoryIt);
            // 添加结点
            addNodeFromNet(id);
        }, () => {

        });
    }
};

/**
 * 修改图例和结点
 * @param categoryName 
 * @param categoryColor 
 */
const updateCategoryAndNode = (categoryName: string, categoryColor: string) => {
    // 如果找到这个图例, 则不用添加
    const it = webkitDep.categories.getMapList().find(it => it.name === categoryName);
    if (it) {
        it.color = categoryColor;
        // 添加结点
        updateNodeFromNet(it.id);
    } else {
        // 更新图例, 从后端获取到图例的 id
        const newCategoryIt = {
            // TODO: 之后id应该是从数据库中获得
            id: 0,
            name: categoryName,
            color: categoryColor
        };
        api.addCategory(nowGraphId, {
            legendId: 0,
            legendName: newCategoryIt.name,
            legendColor: newCategoryIt.color
        }, (id: number) => {
            newCategoryIt.id = id;
            webkitDep.categories.push(newCategoryIt);
            // 添加结点
            updateNodeFromNet(id);
        }, () => {

        });
    }
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

    function fun () {
        // 处理图例 & 添加结点
        addCategoryAndNode(nodeForm.value.category, legendColor.value);

        // 关闭窗口
        closeAddNodeDialog();
        resetAddNodeForm();
    };

    // 处理图片: 更新到 nodeForm.value.imageUrl
    if (cachedFile.value && nodeForm.value.imageUrl === "") {
        // 上传图片, 并且更新到 imageUrl, 然后删除 cachedFile 的值
        uploadImgFromNet(fun);
    } else {
        fun();
    }
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
    const isLt5M = file.size / 1024 / 1024 < 5;
    if (!isImage) {
        ElMessage.error("只能上传图片文件");
    }
    if (!isLt5M) {
        ElMessage.error("上传图片大小不能超过 5MB");
    }
    return isImage && isLt5M;
};

// 从粘贴板中获取图片
const handlePaste = (event: ClipboardEvent) => {
    const items = event.clipboardData?.items;
    if (items) {
        for (let i = 0; i < items.length; ++i) {
            const item = items[i];
            if (item.type.startsWith('image/')) {
                const file = item.getAsFile();
                if (file && beforeUpload(file)) {
                    previewImage(file);
                    cachedFile.value = file;
                }
            }
        }
    }
};

// 预览图片
const previewImage = (file: File) => {
    if (!beforeUpload(file))
        return;
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
        categoriesSelectChange(it.category);
    } else {
        ElMessage.error("内部错误: 查询不到结点");
    }

    // 显示图片 (不支持, 因为这样多次加载了 (我不知道怎么改))
    // if (inputUrl.value !== "") {
    // api.getImg(inputUrl.value, (data: any) => {
    //     console.log(data)
    // }, () => {

    // });
    // }
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
    function fun () {
        // 处理图例 & 修改结点
        updateCategoryAndNode(nodeForm.value.category, legendColor.value);
    };

    // 处理图片: 更新到 nodeForm.value.imageUrl
    if (cachedFile.value && nodeForm.value.imageUrl === "") {
        // 上传图片, 并且更新到 imageUrl, 然后删除 cachedFile 的值
        uploadImgFromNet(fun);
    } else {
        fun();
    }
};

// 异步删除结点api
const removeNode = async (nodeId: number) => {
    try {
        // 将回调包装成 Promise
        const apiCall = new Promise((resolve, reject) => {
            api.delNode(
                nowGraphId,
                nodeId,
                (data: any) => {
                    // 成功回调
                    resolve(data);
                },
                (error: any) => {
                    // 失败回调
                    reject(error);
                }
            );
        });

        // 等待后端返回结果
        const result = await apiCall;

        // 删除结点
        webkitDep.nodes.removeItem(nodeId);

        // 删除相关边
        let arr: [number, number][] = [];
        webkitDep.links.getMapList().forEach((it, idx) => {
            if (it.source === nodeId || it.target === nodeId) {
                arr.push([idx, it.id]);
            }
        });
        for (const [index, id] of arr) {
            webkitDep.links.removeItemByIndex(index, id);
        }

        return result; // 可选，返回后端的结果
    } catch (error) {
        console.error("删除结点失败：", error);
        throw error; // 继续抛出异常以便调用方处理
    }
};

// 删除结点逻辑
const removeNodeLogic = () => {
    ElMessageBox({
        title: '确实删除结点',
        message: h('p', null, [
            h('span', null, '您真的要删除结点: '),
            h('span', { style: 'color: ' + layoutThemeColor }, webkitDep.nodes.getItemById(nowUpDataNodeId.value)?.name),
            h('b', { style: 'color: red' }, " ( id = " + webkitDep.nodes.getItemById(nowUpDataNodeId.value)?.id + " )"),
            h('span', null, ' 吗?'),
        ]),
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info',
        beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
                instance.confirmButtonLoading = true;
                instance.confirmButtonText = '正在删除...';
                // 假设这里你调用了某个 API 来删除节点
                removeNode(nowUpDataNodeId.value).then(() => {
                    // 假设删除成功, 关闭弹框
                    done();
                    ElMessage.success('删除成功');
                    closeNodeUpDataDialog();
                    // 播放删除动画
                    createForceNodeData().then(newData => {
                        myChart.value?.setOption({
                            series: [{
                                type: 'graph',
                                data: newData,
                                edges: webkitDep.links.getMapList(),
                            }]
                        });
                    });
                }).catch(() => {
                    // 如果删除失败, 显示错误信息并不关闭弹框
                    done();
                    ElMessage.error('网络错误，删除失败');
                }).finally(() => {
                    // 不管成功还是失败, 停止加载状态
                    instance.confirmButtonLoading = false;
                });
            } else {
                done();
            }
        },
    });
    // .then((action: any) => {});
};

// === End === 修改结点逻辑 === End ===

// === Begin === 删除边逻辑 === Begin ===
// 异步删除边API
const removeEdge = async (id: number) => {
    // 包装异步操作为 Promise
    const apiCall = new Promise((resolve, reject) => {
        api.delLink(
            nowGraphId,
            id,
            (data: any) => {
                // 成功回调
                resolve(data);
            },
            (error: any) => {
                // 失败回调
                reject(error);
            }
        );
    });

    try {
        // 等待回调完成
        const result = await apiCall;

        // 删除前端边
        webkitDep.links.removeItem(id);

    } catch (error) {
        console.error("删除失败：", error);
    }
};
// === end === 删除边逻辑 === end ===

// 支持可导出: https://echarts.apache.org/zh/api.html#echartsInstance.renderToSVGString
</script>

<style lang="scss">
/* 左侧滚动栏样式 */
.chart-list {
    width: 200px;
    padding: 10px;
    background-color: #171717;
    --el-border-color: #0000;
}

.chart-item {
    display: flex;
    align-items: center;
    padding: 10px;
    margin-bottom: 10px;
    border: 1px solid var(--el-menu-active-color);
    border-radius: 5px;
    cursor: pointer;
    transition: all 0.5s ease;
}

.chart-item:hover {
    background-color: #ff066521;
    transform: scale(1.02);
}

.chart-icon {
    width: 50px;
    height: 50px;
    border-radius: 5px;
    margin-right: 10px;
}

.chart-info {
    flex: 1;
}

.chart-name {
    color: var(--el-menu-active-color);
    font-size: 16px;
    font-weight: bold;
    margin: 0 0 5px;
}

.chart-description {
    font-size: 12px;
    color: #909399;
    margin: 0;
}

// 主界面
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
