package com.hx.controller;

import com.hx.service.ImagesService;
import com.hx.service.LoginService;
import com.hx.utils.FileNameUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.controller
 * @Author: Heng_Xin
 * @CreateTime: 2024-12-05  09:25
 * @Description: 图片控制器, 不进行登录验证
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/images")
public class ImagesController {
    @Autowired
    private ImagesService imagesService;
    @RequestMapping("/force-graph/{userId}/{userTableId}/{fileName}")
    public ResponseEntity<Resource> getImg(
        @PathVariable Long userId,
        @PathVariable Long userTableId,
        @PathVariable String fileName
    ) {
        log.info("图片看看");
        log.info("User ID: {}, User Table ID: {}, File Name: {}", userId, userTableId, fileName);
        // 构造图片文件路径
        String filePath = "/images/force-graph/" + userId + "/" + userTableId + "/" + fileName;
        File file = imagesService.findFile(filePath);
        if (file == null)
            return ResponseEntity.notFound().build();
        // 返回文件资源
        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/" + FileNameUtils.getSuffix(fileName)); // 根据实际类型调整
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
