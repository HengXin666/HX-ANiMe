package com.hx.service;

import com.hx.DAO.forcegraph.LegendDAO;
import com.hx.DAO.forcegraph.NodeDAO;
import com.hx.pojo.DO.forcegraph.LegendDO;
import com.hx.pojo.DO.forcegraph.NodeDO;
import com.hx.pojo.DTO.forcegraph.LegendDTO;
import com.hx.pojo.DTO.forcegraph.NodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.service
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-19  16:53
 * @Description: 力导向图服务
 * @Version: 1.0
 */
@Service
@Slf4j
public class ForceGraphService {
    @Autowired
    private LegendDAO legendDAO;
    @Autowired
    private NodeDAO nodeDAO;
    /**
     * @description: 获取图例
     * @author: Heng_Xin
     * @date: 2024/11/19 17:01
     * @param: userId
     * @param: userTableId
     * @return: List<LegendDTO>
     **/
    public List<LegendDTO> getLegend(Long userId, Long userTableId) {
        List<LegendDO> legendDOList = legendDAO.queryLegend(userId, userTableId);
        if (legendDOList == null) {
            return null;
        }
        return legendDOList.stream().map(legendDO -> {
            LegendDTO legendDTO = new LegendDTO();
            legendDTO.setLegendId(legendDO.getLegendId());
            legendDTO.setLegendName(legendDO.getLegendName());
            legendDTO.setLegendColor(legendDO.getLegendColor());
            return legendDTO;
        }).collect(Collectors.toList());
    }

    /**
     * @description: 添加结点, 返回结点id
     * @author: Heng_Xin
     * @date: 2024/11/22 9:43
     * @param: userId
     * @param: userTableId
     * @param: nodeDTO
     * @return: Long
     **/
    public Long addNode(Long userId, Long userTableId, NodeDTO nodeDTO) {
        // DTO -> DO
        NodeDO nodeDO = new NodeDO();
        nodeDO.setUserId(userId);
        nodeDO.setUserTableId(userTableId);
        nodeDO.setLegendId(nodeDTO.getLegendId());
        nodeDO.setName(nodeDTO.getName());
        nodeDO.setImgUrl(nodeDTO.getImgUrl());
        nodeDO.setDescription(nodeDTO.getDescription());
        return nodeDAO.addNode(nodeDO);
    }

    /**
     * @description: 添加图例, 返回图例id
     * @author: Heng_Xin
     * @date: 2024/11/22 10:30
     * @param: userId
     * @param: userTableId
     * @param: legendDTO
     * @return: Long
     **/
    public Long addLegend(Long userId, Long userTableId, LegendDTO legendDTO) {
        LegendDO legendDO = new LegendDO();
        legendDO.setUserId(userId);
        legendDO.setUserTableId(userTableId);
        legendDO.setLegendName(legendDTO.getLegendName());
        legendDO.setLegendColor(legendDTO.getLegendColor());
        return legendDAO.addLegend(legendDO);
    }
}
