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
        ForceOrientedDiagram: {

        },
    })
});