import { defineStore } from "pinia";

const getFromLocalStorage = (key: string, defaultValue: any) => {
    const storedValue = localStorage.getItem(key);
    return storedValue ? JSON.parse(storedValue) : defaultValue;
};

// 用户设置
export const useSettingsStore = defineStore("setting", {
    state: () => ({
        // 主题
        theme: {
            // 主题色
            color: "#990099",
        },
        // 力导向图设置
        forceOrientedDiagram: {
            layout: "force",
            force: {
                edgeLength: [50, 200],
                repulsion: 100,
                gravity: 0.025,
            },
            lineStyle: {
                color: "#990099",
                width: 2,
                curveness: 0,
                opacity: 0.75,
            },
            // 是否显示结点名称
            label: {
                show: false,
            },
            // 连线样式
            edgeLabel: {
                normal: {
                    // 是否显示连线文本
                    show: false,
                },
            },
            nodeShape: {
                solidShape: "circle",
                imageShape: "roundRect",
            },
        },
    }),
    getters: {
        getTheme: (state) => getFromLocalStorage("theme", state.theme),
        getForceOrientedDiagram: (state) => getFromLocalStorage("forceOrientedDiagram", state.forceOrientedDiagram),
    }
});
