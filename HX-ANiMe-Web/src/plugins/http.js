// @ts-check
import http from "axios";
import qs from "qs";
import { ElMessage } from "element-plus";
import { useUserStore } from "../stores/useUserStore";

// 配置axios基本属性
// @ts-ignore
http.defaults.baseURL = import.meta.env.VITE_API_URL;
http.defaults.timeout = 10000;

// 数据上传数据类型
// @ts-ignore
http.upType = {
	// 表单类型
	form: 0,
	// json类型
	json: 1,
	// 文件类型
	file: 3,
	// 文件流类型
	stream: 4,
};

// HTTP状态码
// @ts-ignore
http.httpcode = {
	// 暂未登录或TOKEN已经过期
	UNAUTHORIZED: 401,
	// 没有相关权限
	FORBIDDEN: 403,
	// 访问页面未找到
	NOT_FOUND: 404,
	// 服务器错误
	SERVER_ERROR: 9994,
	// 上传参数异常
	PARAMS_INVALID: 9995,
	// ContentType错误
	CONTENT_TYPE_ERR: 9996,
	// 功能尚未实现
	API_UN_IMPL: 9997,
	// 服务器繁忙
	SERVER_BUSY: 9998,
	// 操作失败
	FAIL: 9999,
	// 操作成功
	SUCCESS: 10000,
};

// 使用qs序列化参数params参数
http.defaults.paramsSerializer = function (params) {
	return qs.stringify(params);
};

// 在发送前再次对参数进行qs序列化处理
// http.defaults.transformRequest = [function (data) {
//     return qs.stringify(data, {arrayFormat: 'repeat'});
// }];

export default (router) => {
	// 请求拦截处理
	http.interceptors.request.use(
		(config) => {
			// 提交的时候携带登录凭证
			let store = useUserStore();
			let token = store.getToken;
			console.log("token: " + token);
			if (!config.headers.common) {
				config.headers.common = {}; // 确保 common 被初始化
			}
			if (token) {
				config.headers['Authorization'] = `Bearer ${token}`;
				// config.headers.common.Authorization = `Bearer ${token}`;
			}
			// 处理提交方式参数序列化操作
			// @ts-ignore
			if (config.upType === http.upType.json) {
				config.headers["Content-Type"] = "application/json;charset=UTF-8";
			// @ts-ignore
			} else if (config.upType === http.upType.file) {
				config.headers["Content-Type"] = "multipart/form-data";
			// @ts-ignore
			} else if (config.upType === http.upType.stream) {
				config.headers["Content-Type"] = "application/octet-stream";
			} else {
				config.headers["Content-Type"] = "application/x-www-form-urlencoded;charset=UTF-8";
				if (config.data) {
					config.data = qs.stringify(config.data, { arrayFormat: "repeat" });
				}
			}
			return config;
		},
		(error) => {
			// @ts-ignore
			return Promise.error(error);
		},
	);

	// 响应拦截处理
	http.interceptors.response.use(
		async (response) => {
			// HTTP响应状态码正常
			if (response.status === 200) {
				if ("code" in response.data) {
					let store = useUserStore();
					const data = response.data;
					switch (data.code) {
						// @ts-ignore
						case http.httpcode.SUCCESS:
							// 将数据继续传递下去
							return Promise.resolve(data);
						// @ts-ignore
						case http.httpcode.FORBIDDEN:
							// 权限不足
							ElMessage.error("权限不够，请联系管理员");
							// 将数据继续传递下去
							return Promise.reject(data);
						// @ts-ignore
						case http.httpcode.UNAUTHORIZED:
							// 判断是否是超时
							if (typeof data.data === "string" && data.data.includes("Jwt expired at")) {
								// 刷新凭证
								await store.reloadToken();
								// 设置为未加载
								store.setLoaded(false);
								// 跳转到主页
								router.push("/home");
							} else {
								// 没有登录或登录已失效
								router.push("/");
								// 重置数据
								store.resetSaveData();
							}
							// 将数据继续传递下去
							return Promise.reject(data);
						// @ts-ignore
						case http.httpcode.NOT_FOUND:
							ElMessage.warning(data.message || "404访问页面不存在");
							return Promise.reject(data);
						// @ts-ignore
						case http.httpcode.CONTENT_TYPE_ERR:
						// @ts-ignore
						case http.httpcode.PARAMS_INVALID:
							ElMessage.warning(data.message || "请求参数或请求头错误");
							return Promise.reject(response);
						default:
							// 显示操作失败提示
							// ElMessage.error(data.message || '操作失败!!!')
							return Promise.reject(response);
					}
				}
				return Promise.resolve(response);
			} else if (response.status === 404) {
				ElMessage.warning("404访问页面不存在");
				return Promise.resolve(response);
			} else {
				return Promise.reject(response);
			}
		},
		(error) => {
			if (error.code === "ECONNABORTED" || error.code === "ERR_NETWORK") ElMessage.error("连接服务器失败!!!");
			return Promise.reject(error);
		},
	);
};
