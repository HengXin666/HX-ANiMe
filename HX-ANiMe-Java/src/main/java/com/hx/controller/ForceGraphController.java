package com.hx.controller;

import com.hx.pojo.DO.forcegraph.LegendDO;
import com.hx.pojo.DTO.forcegraph.EdgeDTO;
import com.hx.pojo.DTO.forcegraph.LegendDTO;
import com.hx.pojo.DTO.forcegraph.NodeDTO;
import com.hx.pojo.vo.JsonVo;
import com.hx.service.ForceGraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    @Autowired
    private ForceGraphService forceGraphService; // 注入服务层

    /**
     * @description: 获取节点分类, 通过`userTableId`
     * @author: Heng_Xin
     * @date: 2024/11/19 16:50
     * @param: request
     * @param: userTableId
     * @return: JsonVo<List<LegendDTO>>
     **/
    @PostMapping("/get-legend")
    public JsonVo<List<LegendDTO>> getCategory(
        HttpServletRequest request,
        @RequestParam Long userTableId
    ) {
        // 从请求属性中获取 id 和 name
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("User ID: {}, User Name: {}, userTableId: {}", userId, userName, userTableId);
        List<LegendDTO> legendDTOList = forceGraphService.getLegend(userId, userTableId);
        if (legendDTOList == null) { // 查询报错了
            log.error("legendDTOList is null");
            return JsonVo.fail(null);
        }
        log.info("legendDTOList: {}", legendDTOList);
        // 可以为空, 因为可能是新的空图表
        return JsonVo.success(legendDTOList);
    }

    /**
     * @description: 获取所有结点, 通过用户id 和 图id
     * @author: Heng_Xin
     * @date: 2024/11/28 9:02
     * @param: request
     * @param: userTableId
     * @return: JsonVo<List<NodeDTO>>
     **/
    @PostMapping("/get-nodes")
    public JsonVo<List<NodeDTO>> getNodes(
        HttpServletRequest request,
        @RequestParam Long userTableId
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("User ID: {}, User Name: {}, userTableId: {}", userId, userName, userTableId);
        List<NodeDTO> nodeDTOList = forceGraphService.getNodes(userId, userTableId);
        if (nodeDTOList == null) { // 查询报错了
            return JsonVo.fail(null);
        }
        return JsonVo.success(nodeDTOList);
    }

    // 获取边

    /**
     * @description: 添加图例, 并返回图例 id
     * @author: Heng_Xin
     * @date: 2024/11/22 10:28
     * @param: request
     * @param: userTableId
     * @param: legendDTO
     * @return: JsonVo<Long>
     **/
    @PostMapping("/add-legend")
    public JsonVo<Long> addLegend(
        HttpServletRequest request,
        @RequestParam Long userTableId,
        @RequestBody LegendDTO legendDTO
    ) {
        // 从请求属性中获取 id 和 name
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("添加图例 User ID: {}, User Name: {}, userTableId: {}", userId, userName, userTableId);
        Long res = forceGraphService.addLegend(userId, userTableId, legendDTO);
        if (res == null) {
            return JsonVo.fail(null);
        }
        return JsonVo.success(res);
    }

    /**
     * @description: 添加结点, 并返回结点 id
     * @author: Heng_Xin
     * @date: 2024/11/22 10:04
     * @param: request
     * @param: userTableId
     * @param: nodeDTO
     * @return: JsonVo<Long>
     **/
    @PostMapping("/add-node")
    public JsonVo<Long> addNode(
        HttpServletRequest request,
        @RequestParam Long userTableId,
        @RequestBody NodeDTO nodeDTO
    ) {
        // 从请求属性中获取 id 和 name
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("添加结点 User ID: {}, User Name: {}, userTableId: {}", userId, userName, userTableId);
        Long res = forceGraphService.addNode(userId, userTableId, nodeDTO);
        if (res == null) {
            return JsonVo.fail(null);
        }
        return JsonVo.success(res);
    }

    /**
     * @description: 添加边, 并返回边 id
     * @author: Heng_Xin
     * @date: 2024/11/29 9:22
     * @param: request
     * @param: userTableId
     * @param: edgeDTO
     * @return: JsonVo<Long>
     **/
    @PostMapping("/add-edge")
    public JsonVo<Long> addEdge(
        HttpServletRequest request,
        @RequestParam Long userTableId,
        @RequestBody EdgeDTO edgeDTO
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("添加边 User ID: {}, User Name: {}, userTableId: {}", userId, userName, userTableId);
        Long res = forceGraphService.addEdge(userId, userTableId, edgeDTO);
        if (res == null) {
            return JsonVo.fail(null);
        }
        return JsonVo.success(res);
    }

    // 删除结点

    // 删除边

    // 删除图例

    // 更新结点

    // 更新边

    // 更新图例
}
