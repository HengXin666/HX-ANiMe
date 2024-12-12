package com.hx.controller;

import com.hx.pojo.DTO.forcegraph.EdgeDTO;
import com.hx.pojo.DTO.forcegraph.LegendDTO;
import com.hx.pojo.DTO.forcegraph.NodeDTO;
import com.hx.pojo.vo.JsonVo;
import com.hx.service.ForceGraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
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
}
