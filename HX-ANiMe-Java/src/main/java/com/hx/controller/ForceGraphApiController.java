package com.hx.controller;

import com.hx.pojo.DTO.forcegraph.EdgeDTO;
import com.hx.pojo.DTO.forcegraph.LegendDTO;
import com.hx.pojo.DTO.forcegraph.NodeDTO;
import com.hx.pojo.vo.JsonVo;
import com.hx.service.ForceGraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.controller
 * @Author: Heng_Xin
 * @CreateTime: 2024-12-12  15:42
 * @Description: 力导向图密匙请求控制器
 * @Version: 1.0
 */

@Slf4j
@RestController
@RequestMapping("/force-graph-api")
public class ForceGraphApiController {
    @Autowired
    private ForceGraphService forceGraphService;

    /**
     * @description: 获取apiKey对应的图例列表
     * @author: Heng_Xin
     * @date: 2024/12/12 22:20
     * @param: apiKey
     * @return: JsonVo<List<LegendDTO>>
     **/
    @RequestMapping("/get-legend")
    public JsonVo<List<LegendDTO>> getLegend(@RequestHeader("apiKey") String apiKey) {
        // 校验 apiKey, 返回对应的userId, userTableId
        Pair<Long, Long> pair = forceGraphService.getUserIdAndUserTableIdByApiKey(apiKey);
        if (pair == null) {
            return JsonVo.fail(null);
        }
        return JsonVo.success(forceGraphService.getLegend(pair.getFirst(), pair.getSecond()));
    }

    /**
     * @description: 获取apiKey对应的节点列表
     * @author: Heng_Xin
     * @date: 2024/12/12 22:30
     * @param: apiKey
     * @return: JsonVo<List<NodeDTO>>
     **/
    @RequestMapping("/get-nodes")
    public JsonVo<List<NodeDTO>> getNodes(@RequestHeader("apiKey") String apiKey) {
        // 校验 apiKey, 返回对应的userId, userTableId
        Pair<Long, Long> pair = forceGraphService.getUserIdAndUserTableIdByApiKey(apiKey);
        if (pair == null) {
            return JsonVo.fail(null);
        }
        return JsonVo.success(forceGraphService.getNodes(pair.getFirst(), pair.getSecond()));
    }

    /**
     * @description: 获取apiKey对应的边列表
     * @author: Heng_Xin
     * @date: 2024/12/12 23:03
     * @param: apiKey
     * @return: JsonVo<List<EdgeDTO>>
     **/
    @RequestMapping("/get-edges")
    public JsonVo<List<EdgeDTO>> getEdges(@RequestHeader("apiKey") String apiKey) {
        // 校验 apiKey, 返回对应的userId, userTableId
        Pair<Long, Long> pair = forceGraphService.getUserIdAndUserTableIdByApiKey(apiKey);
        if (pair == null) {
            return JsonVo.fail(null);
        }
        return JsonVo.success(forceGraphService.getEdges(pair.getFirst(), pair.getSecond()));
    }

    /**
     * @description: 添加图例, 返回图例id
     * @author: Heng_Xin
     * @date: 2024/12/12 23:27
     * @param: apiKey
     * @param: legendDTO
     * @return: JsonVo<Long>
     **/
    @RequestMapping("/add-legend")
    public JsonVo<Long> addLegend(
        @RequestHeader("apiKey") String apiKey,
        @RequestBody LegendDTO legendDTO
    ) {
        // 校验 apiKey, 返回对应的userId, userTableId
        Pair<Long, Long> pair = forceGraphService.getUserIdAndUserTableIdByApiKey(apiKey);
        if (pair == null) {
            return JsonVo.fail(null);
        }
        log.info("添加图例 Legend Name: {}", legendDTO.getLegendName());
        return JsonVo.success(forceGraphService.addLegend(pair.getFirst(), pair.getSecond(), legendDTO));
    }

    /**
     * @description: 添加节点, 返回节点id
     * @author: Heng_Xin
     * @date: 2024/12/13 9:03
     * @param: apiKey
     * @param: nodeDTO
     * @return: JsonVo<Long>
     **/
    @RequestMapping("/add-node")
    public JsonVo<Long> addNode(
        @RequestHeader("apiKey") String apiKey,
        @RequestBody NodeDTO nodeDTO
    ) {
        // 校验 apiKey, 返回对应的userId, userTableId
        Pair<Long, Long> pair = forceGraphService.getUserIdAndUserTableIdByApiKey(apiKey);
        if (pair == null) {
            return JsonVo.fail(null);
        }
        return JsonVo.success(
            forceGraphService.addNode(pair.getFirst(), pair.getSecond(), nodeDTO)
        );
    }

    /**
     * @description: 添加边, 返回边id
     * @author: Heng_Xin
     * @date: 2024/12/13 9:03
     * @param: apiKey
     * @param: edgeDTO
     * @return: JsonVo<Long>
     **/
    @RequestMapping("/add-edge")
    public JsonVo<Long> addEdge(
        @RequestHeader("apiKey") String apiKey,
        @RequestBody EdgeDTO edgeDTO
    ) {
        // 校验 apiKey, 返回对应的userId, userTableId
        Pair<Long, Long> pair = forceGraphService.getUserIdAndUserTableIdByApiKey(apiKey);
        if (pair == null) {
            return JsonVo.fail(null);
        }
        return JsonVo.success(
            forceGraphService.addEdge(pair.getFirst(), pair.getSecond(), edgeDTO)
        );
    }
}
