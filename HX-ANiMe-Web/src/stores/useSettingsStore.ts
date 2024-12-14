import { defineStore } from "pinia";

// 用户设置
export const useSettingStore = defineStore("setting", {
    state: () => ({
        // 主题
        theme: {
            // 主题色
            color: "#990099",
        },
        // 力导向图设置
        forceOrientedDiagram: {
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
            label: {
                show: false,
            },
            emphasis: {
                label: {
                    show: true,
                },
            },
            nodeShape: {
                solidShape: "圆形",
                imageShape: "圆形",
            },
        },
    }),
    getters: {
        getTheme: (state) => JSON.parse(((s) => {return s ? s : ""; })(localStorage.getItem("theme"))) || state.theme,
        getForceOrientedDiagram: (state) => JSON.parse(((s) => {return s ? s : ""; })(localStorage.getItem("forceOrientedDiagram"))) || state.forceOrientedDiagram,
    }
});
