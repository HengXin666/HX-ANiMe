package com.hx.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.controller
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-14  09:50
 * @Description: 力导向图控制器
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/force-graph")
public class ForceGraphController {
    // 从数据库获取节点数据
    /**
     * @description:
     * @author: Heng_Xin 
     * @date: 2024/11/14 9:53 
     * @return: String
     **/
    @PostMapping("/get-nodes")
    public String getNodes(HttpServletRequest request) {
        // 从请求属性中获取 id 和 name
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");

        log.info("User ID: {}, User Name: {}", userId, userName);

        if (userId != null && userName != null) {
            return "{UserID: " + userId + ", UserName: " + userName + "}";
        } else {
            // 不可能事件
            return "{Error: User ID or User Name is null}";
        }
    }

    @PostMapping("/get-category")
    public String getCategory(HttpServletRequest request) {
        // 写DTO、DO艹
    }
}
