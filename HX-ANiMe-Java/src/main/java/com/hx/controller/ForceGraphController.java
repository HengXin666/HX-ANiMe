package com.hx.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping("/get-nodes")
    public String getNodes() {
        return "{\"nodes\":[{\"id\":\"node1\"},{\"id\":\"node2\"},{\"id\":\"node3\"}],\"links\":[{\"source\":\"node1\",\"target\":\"node2\"},{\"source\":\"node2\",\"target\":\"node3\"}]}";
    }
}
