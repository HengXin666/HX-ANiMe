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
	console.log("post: " + currBaseUrl + "get-category: " + userTableId);
	Request.request(Request.POST, currBaseUrl + "get-category?userTableId=" + userTableId)
		.then((data) => {
			if (data) {
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