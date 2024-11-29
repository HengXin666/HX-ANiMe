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
	Request.request(Request.POST, currBaseUrl + "get-legend?userTableId=" + userTableId)
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
 * 获取所有结点
 * @param userTableId 当前表id
 * @param success 成功回调
 * @param fail 失败回调
 */
export const getNodes = (userTableId, success, fail) => {
	Request.request(Request.POST, currBaseUrl + "get-nodes?userTableId=" + userTableId)
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
 * 获取所有边
 * @param userTableId 当前表id
 * @param success 成功回调
 * @param fail 失败回调
 */
export const getLinks = (userTableId, success, fail) => {
	Request.request(Request.POST, currBaseUrl + "get-edges?userTableId=" + userTableId)
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
 * @param categoryData 图例数据
 * @param success 成功回调
 * @param fail 失败回调
 */
export const addCategory = (userTableId, categoryData, success, fail) => {
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
 * 添加结点
 * @param userTableId 当前表id
 * @param nodeData 图例数据
 * @param success 成功回调
 * @param fail 失败回调
 */
export const addNode = (userTableId, nodeData, success, fail) => {
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

/**
 * 添加边
 * @param userTableId 当前表id
 * @param linkData 图例数据
 * @param success 成功回调
 * @param fail 失败回调
 */
export const addLink = (userTableId, linkData, success, fail) => {
	Request.requestJson(Request.POST, currBaseUrl + "add-edge?userTableId=" + userTableId, linkData)
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
 * 上传图片
 * @param userTableId 当前表id
 * @param fileData 文件数据
 * @param success 成功回调
 * @param fail 失败回调
 */
export const uploadImg = (userTableId, fileData, success, fail) => {
	Request.postFileStream(
		currBaseUrl + "upload-img?userTableId=" + userTableId,
		fileData,
		(data) => {
			if (data) {
				// 执行成功回调
				success(data.data);
				return;
			}
			// 执行失败回调
			fail();
		}, (err) => {
			// 打印错误信息
			console.warn(err);
			// 执行失败回调
			fail();
		}
	);
};