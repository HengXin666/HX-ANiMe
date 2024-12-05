package com.hx.service;

import com.hx.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.service
 * @Author: Heng_Xin
 * @CreateTime: 2024-12-05  09:32
 * @Description: 图片服务
 * @Version: 1.0
 */
@Service
public class ImagesService {
    @Autowired
    private GlobalConfig globalConfig;
    /**
     * @description: 查询该路径的文件, 如果有则读取并且返回
     * @author: Heng_Xin
     * @date: 2024/12/5 9:33
     * @param: filePath
     * @return: void
     **/
    public File findFile(String filePath) {
        File file = new File(globalConfig.getFilePath() + filePath);
        if (!file.exists())
            return null;
        return file;
    }
}
