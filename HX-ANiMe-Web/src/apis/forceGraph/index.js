import Request from "../request";

// 定义一个功能模块基础url, 方便替换
const currBaseUrl = "/force-graph/";

/**
 * 获取图例
 * @param userTableId 当前表id
 * @param success 成功回调
 * @param fail 失败回调
 */
export const getCategory = (userTableId, success, fail) => {
	console.log("post: " + currBaseUrl + "get-legend: " + userTableId);
	Request.request(Request.POST, currBaseUrl + "get-legend?userTableId=" + userTableId)
		.then((data) => {
			console.log(data);
			if (data) {
				// 执行成功回调
				success(data.data);
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
 * 添加图例
 * @param userTableId 当前表id
 * @param categoryData 图例数据
 * @param success 成功回调
 * @param fail 失败回调
 */
export const addCategory = (userTableId, categoryData, success, fail) => {
	console.log("post: " + currBaseUrl + "add-legend: " + userTableId);
	Request.requestJson(Request.POST, currBaseUrl + "add-legend?userTableId=" + userTableId, categoryData)
		.then((data) => {
			if (data) {
				// 执行成功回调
				success(data.data);
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
 * 添加图例
 * @param userTableId 当前表id
 * @param nodeData 图例数据
 * @param success 成功回调
 * @param fail 失败回调
 */
export const addNode = (userTableId, nodeData, success, fail) => {
	console.log("post: " + currBaseUrl + "add-node: " + userTableId);
	Request.requestJson(Request.POST, currBaseUrl + "add-node?userTableId=" + userTableId, nodeData)
		.then((data) => {
			if (data) {
				// 执行成功回调
				success(data.data);
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