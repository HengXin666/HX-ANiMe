<template>
    <div id="app">
      <div ref="chartContainer" style="width: 100%; height: 600px;"></div>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, onMounted, ref, onBeforeUnmount } from 'vue';
  import * as echarts from 'echarts';
  
  export default defineComponent({
    name: 'App',
    setup() {
      const chartContainer = ref<HTMLElement | null>(null);
      let chart: echarts.ECharts | null = null;
      let startNode: any = null;
      let endNode: any = null;
      let currentArrowLink: any = null; // 用来存储当前箭头的连线
  
      const nodes = [
        { id: 'A', name: 'A', x: 100, y: 100 },
        { id: 'B', name: 'B', x: 300, y: 300 },
        { id: 'C', name: 'C', x: 500, y: 500 },
      ];
  
      // 初始化图表
      const initChart = () => {
        if (chartContainer.value) {
          chart = echarts.init(chartContainer.value);
          const option = {
            series: [
              {
                type: 'graph',
                layout: 'none',
                symbolSize: 50,
                roam: true,
                label: {
                  show: true,
                },
                edgeSymbol: ['circle', 'arrow'],
                edgeSymbolSize: [4, 10],
                data: nodes,
                links: [],
                lineStyle: {
                  width: 3,
                  curveness: 0.2,
                },
              },
            ],
          };
          chart.setOption(option);
        }
      };
  
      // 更新箭头连线到鼠标位置
      const updateMouseFollowLink = (event: MouseEvent) => {
        if (startNode) {
          const x = event.offsetX;
          const y = event.offsetY;
  
          // 如果已经有箭头连线，更新它的位置
          if (currentArrowLink) {
            currentArrowLink.target = { x, y };
          } else {
            currentArrowLink = { source: startNode.id, target: { x, y } };
          }
  
          // 动态更新箭头显示
          chart?.setOption({
            series: [
              {
                type: 'graph',
                data: [...nodes],
                links: [currentArrowLink], // 更新箭头连线
              },
            ],
          });
        }
      };
  
      // 左键点击设置起点
      const handleLeftClick = (params: any) => {
        if (params.componentType === 'series' && params.seriesType === 'graph') {
          if (!startNode) {
            startNode = params.data;
            startNode.itemStyle = { color: 'red', borderWidth: 3, borderColor: 'yellow' };
            chart?.setOption({
              series: [{ data: [...nodes] }],
            });
  
            // 监听画布的鼠标移动
            chartContainer.value?.addEventListener('mousemove', updateMouseFollowLink);
          }
        }
      };
  
      // 右键点击设置终点，并绘制连线
      const handleRightClick = (params: any) => {
        if (params.componentType === 'series' && params.seriesType === 'graph' && startNode) {
          endNode = params.data;
  
          // 绘制从起点到终点的边
          const newLink = {
            source: startNode.id,
            target: endNode.id,
          };
  
          chart?.setOption({
            series: [
              {
                type: 'graph',
                data: [...nodes],
                links: [newLink], // 添加边
              },
            ],
          });
  
          // 播放连边的动画效果
          chart?.dispatchAction({
            type: 'graphAddLink',
            link: newLink,
          });
  
          // 清空起点和终点状态，并移除鼠标移动事件监听器
          startNode = null;
          endNode = null;
          currentArrowLink = null;
          chartContainer.value?.removeEventListener('mousemove', updateMouseFollowLink);
        }
      };
  
      // 在 mounted 中初始化图表
      onMounted(() => {
        initChart();
        chart?.on('click', handleLeftClick);
        chart?.on('contextmenu', handleRightClick);
      });
  
      // 清理事件监听器
      onBeforeUnmount(() => {
        chartContainer.value?.removeEventListener('mousemove', updateMouseFollowLink);
        chart?.dispose();
      });
  
      return {
        chartContainer,
      };
    },
  });
  </script>
  
  <style scoped>
  #app {
    text-align: center;
  }
  </style>
  