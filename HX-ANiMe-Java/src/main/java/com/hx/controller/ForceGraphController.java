package com.hx.controller;

import com.hx.pojo.DTO.forcegraph.UserTablesDTO;
import com.hx.pojo.DTO.forcegraph.EdgeDTO;
import com.hx.pojo.DTO.forcegraph.LegendDTO;
import com.hx.pojo.DTO.forcegraph.NodeDTO;
import com.hx.pojo.vo.JsonVo;
import com.hx.pojo.vo.ResultStatus;
import com.hx.service.ForceGraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/get-edges")
    public JsonVo<List<EdgeDTO>> getEdges(
        HttpServletRequest request,
        @RequestParam Long userTableId
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("User ID: {}, User Name: {}, userTableId: {}", userId, userName, userTableId);
        List<EdgeDTO> edgeDTOList = forceGraphService.getEdges(userId, userTableId);
        if (edgeDTOList == null) { // 查询报错了
            return JsonVo.fail(null);
        }
        return JsonVo.success(edgeDTOList);
    }

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

    /**
     * @description: 删除结点, 以及该结点所对应的所有边
     * @author: Heng_Xin
     * @date: 2024/12/6 10:09
     * @param: request
     * @param: userTableId
     * @param: nodeId
     * @return: JsonVo<String>
     **/
    @PostMapping("/remove-node")
    public JsonVo<String> removeNode(
        HttpServletRequest request,
        @RequestParam Long userTableId,
        @RequestParam Long nodeId
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("删除结点 User ID: {}, User Name: {}, userTableId: {}, nodeId: {}", userId, userName, userTableId, nodeId);
        if (!forceGraphService.removeNode(userId, userTableId, nodeId)) {
            return JsonVo.fail(null);
        }
        return JsonVo.success("ok");
    }

    /**
     * @description: 删除边
     * @author: Heng_Xin
     * @date: 2024/12/6 9:40
     * @param: request
     * @param: userTableId
     * @param: edgeId
     * @return: JsonVo<String>
     **/
    @PostMapping("/remove-edge")
    public JsonVo<String> removeEdge(
        HttpServletRequest request,
        @RequestParam Long userTableId,
        @RequestParam Long edgeId
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("删除边 User ID: {}, User Name: {}, userTableId: {}, edgeId: {}", userId, userName, userTableId, edgeId);
        if (!forceGraphService.removeEdge(userId, userTableId, edgeId)) {
            return JsonVo.fail(null);
        }
        return JsonVo.success("ok");
    }

    // 删除图例

    /**
     * @description: 更新结点
     * @author: Heng_Xin
     * @date: 2024/12/3 17:05
     * @param: request
     * @param: userTableId
     * @param: nodeDTO
     * @return: JsonVo<Long>
     **/
    @PostMapping("/update-node")
    public JsonVo<String> updateNode(
        HttpServletRequest request,
        @RequestParam Long userTableId,
        @RequestBody NodeDTO nodeDTO
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("更新结点 User ID: {}, User Name: {}, userTableId: {}", userId, userName, userTableId);
        if (!forceGraphService.updateNode(userId, userTableId, nodeDTO)) {
            return JsonVo.fail(null);
        }
        return JsonVo.success("ok");
    }

    // 更新边

    // 更新图例

    /**
     * @description: 上传图片, 并且通过文件名判断是否重名, 并返回图片地址
     * @author: Heng_Xin
     * @date: 2024/11/29 10:38
     * @param: request
     * @param: userTableId
     * @param: file
     * @return: JsonVo<String> 重名则报错
     **/
    @PostMapping("/upload-img")
    public JsonVo<String> uploadImg(
        HttpServletRequest request,
        @RequestParam Long userTableId,
        @RequestParam("file") MultipartFile file
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("上传图片 User ID: {}, User Name: {}, userTableId: {}", userId, userName, userTableId);
        // 判断图片大小, 是否小于5M
        if (file.getSize() > 5 * 1024 * 1024) {
            return JsonVo.create(null, ResultStatus.PARAMS_INVALID.getCode(), "图片大小超过5M");
        }
        String imgUrl = forceGraphService.uploadImg(userId, userTableId, file);
        if (imgUrl == null) {
            return JsonVo.create(null, ResultStatus.FAIL.getCode(), "文件重名!");
        }
        return JsonVo.success(imgUrl);
    }

    /**
     * @description: 根据用户 id, 获取图标列表
     * @author: Heng_Xin
     * @date: 2024/12/10 17:51
     * @param: request
     * @return: JsonVo<List<UserTablesDTO>>
     **/
    @PostMapping("/get-table-list")
    public JsonVo<List<UserTablesDTO>> getTableList(
        HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("获取图标列表 User ID: {}, User Name: {}", userId, userName);
        List<UserTablesDTO> res = forceGraphService.getTableList(userId);
        return JsonVo.success(res);
    }

    /**
     * @description: 添加图表, 并返回图表 id
     * @author: Heng_Xin
     * @date: 2024/12/10 17:52
     * @param: request
     * @param: userTablesDTO
     * @return: JsonVo<Long>
     **/
    @PostMapping("/add-table")
    public JsonVo<Long> addTable(
        HttpServletRequest request,
        @RequestBody UserTablesDTO userTablesDTO
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("添加用户图表 User ID: {}, User Name: {}", userId, userName);
        Long res = forceGraphService.addTable(userId, userTablesDTO);
        if (res == null) {
            return JsonVo.fail(null);
        }
        return JsonVo.success(res);
    }

    /**
     * @description: 修改图表
     * @author: Heng_Xin
     * @date: 2024/12/10 23:32
     * @param: request
     * @param: userTablesDTO
     * @return: JsonVo<String>
     **/
    @PostMapping("/update-table")
    public JsonVo<String> updateTable(
        HttpServletRequest request,
        @RequestBody UserTablesDTO userTablesDTO
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("修改用户图表 User ID: {}, User Name: {}", userId, userName);
        if (!forceGraphService.updateTable(userId, userTablesDTO)) {
            return JsonVo.fail(null);
        }
        return JsonVo.success("ok");
    }

    /**
     * @description: 删除图表
     * @author: Heng_Xin
     * @date: 2024/12/12 15:45
     * @param: request
     * @param: userTableId
     * @return: JsonVo<String>
     **/
    @PostMapping("/remove-table")
    public JsonVo<String> removeTable(
        HttpServletRequest request,
        @RequestParam Long userTableId
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("删除用户图表 User ID: {}, User Name: {}, userTableId: {}", userId, userName, userTableId);
        if (!forceGraphService.removeTable(userId, userTableId)) {
            return JsonVo.fail(null);
        }
        return JsonVo.success("ok");
    }

    /**
     * @description: 获取 api-key (每个用户每个图表唯一)
     * @author: Heng_Xin
     * @date: 2024/12/12 16:07
     * @param: request
     * @param: userTableId
     * @return: JsonVo<String>
     **/
    @PostMapping("/get-api-key")
    public JsonVo<String> getApiKey(
        HttpServletRequest request,
        @RequestParam Long userTableId
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String userName = (String) request.getAttribute("userName");
        log.info("获取api-key User ID: {}, User Name: {}, userTableId: {}", userId, userName, userTableId);
        String res = forceGraphService.getApiKey(userId, userTableId);
        if (res == null) {
            return JsonVo.fail(null);
        }
        return JsonVo.success(res);
    }
}
