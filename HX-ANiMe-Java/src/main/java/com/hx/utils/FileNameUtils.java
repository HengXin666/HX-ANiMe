package com.hx.utils;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.utils
 * @Author: Heng_Xin
 * @CreateTime: 2024-12-05  09:38
 * @Description: 文件名称工具类
 * @Version: 1.0
 */
public class FileNameUtils {
    /**
     * @description: 获取文件后缀, 不含点
     * @author: Heng_Xin
     * @date: 2024/12/5 9:39
     * @param: fileName
     * @return: String
     **/
    public static String getSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
