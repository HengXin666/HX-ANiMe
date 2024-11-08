import Request from "../request";
import { useUserStore } from "@/stores/useUserStore";

// 定义一个功能模块基础url, 方便替换
const currBaseUrl = "/login/";

/**
 * 登录接口
 * @param data 登录数据
 * @param success 登录成功回调
 * @param fail 登录失败回调
 */
export const login = (data, success, fail) => {
	const $store = useUserStore();
	Request.requestJson(Request.POST, currBaseUrl + "query-login", data)
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

/**
 * 注册接口
 * @param data 注册数据
 * @param success 注册成功回调
 * @param fail 注册失败回调
 */
export const register = (data, success, fail) => {
	const $store = useUserStore();
	Request.requestJson(Request.POST, currBaseUrl + "add-user", data)
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