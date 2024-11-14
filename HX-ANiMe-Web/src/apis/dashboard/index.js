import Request from "../request";
import { useUserStore } from "@/stores/useUserStore";

// 定义一个功能模块基础url, 方便替换
const currBaseUrl = "/test/";

/**
 * 测试token接口, 看看后端是否可以收到token的请求
 * @param data 登录数据
 * @param success 登录成功回调
 * @param fail 登录失败回调
 */
export const ifToken = (data, success, fail) => {
	const $store = useUserStore();
	Request.requestJson(Request.POST, currBaseUrl + "if-token", data)
		.then((data) => {
			// 记录Token到本地
			if (data.data) {
				$store.setToken(data.data);
				// 执行成功回调
				success();
				return;
			}
			// 执行失败回调
			fail();
		})
		.catch((err) => {
			// 打印错误信息
			console.warn(err);
			// 执行失败回调
			fail();
		});
};