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
                <el-button circle title="添加" size="large" :icon="Plus" @click="openDialog"
                    style="margin-bottom: 10px;" />
            </el-tooltip>
            <el-tooltip content="设置" placement="top">
                <el-button circle title="设置" size="large" :icon="Setting" @click="openSettings"
                    style="margin-bottom: 10px;" />
            </el-tooltip>
        </div>

        <!-- 可拖动的弹出窗口 -->
        <el-dialog v-model="dialogVisible" title="添加结点" :draggable="true" width="500px">
            <div class="dialog-add-node">
                <!-- 结点名称输入框 -->
                <el-form :model="nodeForm" label-width="80">
                    <el-form-item label="结点名称">
                        <el-input v-model="nodeForm.name" placeholder="请输入结点名称"></el-input>
                    </el-form-item>

                    <!-- 结点类型下拉选择框或输入框以及其颜色 -->
                    <el-form-item label="结点类型">
                        <div style="display: flex; align-items: center; width: 350px;">
                            <el-color-picker style="margin-right: 10px;" v-model="legendColor" show-alpha :predefine="predefineColors" />
                            <el-select v-model="nodeForm.categories" placeholder="请选择结点类型" filterable allow-create @change="categoriesSelectChange">
                                <el-option v-for="item in nodeLegendList" :key="item" :label="item" :value="item"></el-option>
                            </el-select>
                        </div>
                    </el-form-item>

                    <!-- 结点图片上传 -->
                    <el-upload 
                        drag 
                        class="image-uploader" 
                        :limit="1" 
                        :auto-upload="false" 
                        :show-file-list="false"
                        :on-change="updateImage"
                        :on-exceed="updateImageExceed"
                        :accept="'image/*'"
                    >
                        <!-- 图片显示框 -->
                        <el-image :src="imageUrl" fit="contain" class="image-preview" v-if="imageUrl" />
                        <div v-else class="image-preview empty">
                            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                            <div class="el-upload__text">
                                拖拽图片到此处或 <em>点击上传</em>
                            </div>
                        </div>
                    </el-upload>

                    <!-- 输入框 -->
                    <div class="input-group">
                        <el-input v-model="inputUrl" placeholder="输入图片URL或粘贴图片到此处" @change="updateImageUrl"
                            @paste="handlePaste" />
                    </div>
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
import { ref, onMounted, watch, onBeforeUnmount, markRaw } from 'vue';
import * as echarts from 'echarts';
import { ElButton, ElMessage, UploadRawFile, UploadUserFile } from 'element-plus';
import { useSettingStore } from '@/stores/useSettingsStore';
import { RefreshRight, Plus, Setting } from '@element-plus/icons-vue';

// 断言结点类型
interface Node {
    id: number;
    name: string;
    category: string;
    img: string;
    describe: string;
};

const settingStore = useSettingStore();
const layoutThemeColor = settingStore.theme.color;  // 默认主题色

// 引用图表元素
const chart = ref<HTMLElement | null>(null);

// ECharts 实例
const myChart = ref<echarts.ECharts | null>(null);

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
        { id: 2, name: 'Anime', color: '#334455' },
        { id: 3, name: 'Character', color: '#D6B782' },
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
};

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


// === Begin === 添加结点逻辑 === Begin ===
const dialogVisible = ref(true);

const nodeForm = ref({
    // 结点名称
    name: "",
    // 图例
    categories: "",
    // 图片Url
    image: "",       // 本地图片预览
    imageUrl: "",    // 网络图片地址
});

// 选择结点图例颜色
const legendColor = ref('red');
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

// 结点类型选项
const nodeLegendList = webkitDep.categories.map(it => it.name);

// 选择结点图例时候触发
const categoriesSelectChange = (name: string) => {
    const it = webkitDep.categories.find(dict => dict.name === name);
    if (it) {
        legendColor.value = it.color;
    }
};

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
    resetForm();
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
            const item = items[i]
            if (item.type.startsWith('image/')) {
                const file = item.getAsFile()
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

// 确认上传
const confirmUpload = () => {
    if (cachedFile.value) {
        uploadImage(cachedFile.value);
    } else if (inputUrl.value) {
        uploadImage(inputUrl.value);
    } else {
        ElMessage.warning('请先选择图片或输入图片URL');
    }
};

// 上传图片到后端函数
const uploadImage = (image: File | string) => {
    // 这里可以实现上传逻辑
    if (image instanceof File) {
        // 处理文件上传逻辑
        console.log('上传文件', image);
    } else {
        // 处理 URL 上传逻辑
        console.log('上传图片URL', image);
    }
    ElMessage.success('图片上传成功');
};

// === End === 添加结点逻辑 === End ===

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

    .input-group {
        display: flex;
        gap: 10px;
        max-width: 300px;
    }
}
</style>
