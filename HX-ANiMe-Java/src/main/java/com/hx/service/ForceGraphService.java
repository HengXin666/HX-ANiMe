package com.hx.service;

import com.hx.DAO.forcegraph.EdgeDAO;
import com.hx.DAO.forcegraph.LegendDAO;
import com.hx.DAO.forcegraph.NodeDAO;
import com.hx.pojo.DO.forcegraph.EdgeDO;
import com.hx.pojo.DO.forcegraph.LegendDO;
import com.hx.pojo.DO.forcegraph.NodeDO;
import com.hx.pojo.DTO.forcegraph.EdgeDTO;
import com.hx.pojo.DTO.forcegraph.LegendDTO;
import com.hx.pojo.DTO.forcegraph.NodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    @Autowired
    private EdgeDAO edgeDAO;

    // 保存文件的根路径, 通过读取, application.yaml配置文件
    @Value("${file.path}")
    private String FILE_PATH;

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

    /**
     * @description: 获取所有节点
     * @author: Heng_Xin
     * @date: 2024/11/28 9:03
     * @param: userId
     * @param: userTableId
     * @return: List<NodeDTO>
     **/
    public List<NodeDTO> getNodes(Long userId, Long userTableId) {
        List<NodeDO> nodeDOList = nodeDAO.queryLegend(userId, userTableId);
        if (nodeDOList == null) {
            return null;
        }
        return nodeDOList.stream().map(nodeDO -> {
            NodeDTO nodeDTO = new NodeDTO();
            nodeDTO.setNodeId(nodeDO.getNodeId());
            nodeDTO.setLegendId(nodeDO.getLegendId());
            nodeDTO.setName(nodeDO.getName());
            nodeDTO.setImgUrl(nodeDO.getImgUrl());
            nodeDTO.setDescription(nodeDO.getDescription());
            return nodeDTO;
        }).collect(Collectors.toList());
    }

    /**
     * @description: 添加边
     * @author: Heng_Xin
     * @date: 2024/11/29 9:22
     * @param: userId
     * @param: userTableId
     * @param: edgeDTO
     * @return: Long
     **/
    public Long addEdge(Long userId, Long userTableId, EdgeDTO edgeDTO) {
        EdgeDO edgeDO = new EdgeDO();
        edgeDO.setUserId(userId);
        edgeDO.setUserTableId(userTableId);
        edgeDO.setFromNodeId(edgeDTO.getFromNodeId());
        edgeDO.setToNodeId(edgeDTO.getToNodeId());
        return edgeDAO.addEdge(edgeDO);
    }

    /**
     * @description: 获取所有边
     * @author: Heng_Xin
     * @date: 2024/11/29 9:38
     * @param: userId
     * @param: userTableId
     * @return: List<EdgeDTO>
     **/
    public List<EdgeDTO> getEdges(Long userId, Long userTableId) {
        List<EdgeDO> edgeDOList = edgeDAO.queryEdges(userId, userTableId);
        if (edgeDOList == null) {
            return null;
        }
        return edgeDOList.stream().map(edgeDO -> {
            EdgeDTO edgeDTO = new EdgeDTO();
            edgeDTO.setEdgeId(edgeDO.getEdgeId());
            edgeDTO.setFromNodeId(edgeDO.getFromNodeId());
            edgeDTO.setToNodeId(edgeDO.getToNodeId());
            return edgeDTO;
        }).collect(Collectors.toList());
    }

    /**
     * @description: 上传图片
     * @author: Heng_Xin
     * @date: 2024/11/29 10:42
     * @param: userId
     * @param: userTableId
     * @param: file
     * @return: String
     **/
    public String uploadImg(Long userId, Long userTableId, MultipartFile file) {
        // 保存图片到服务器, 路径为: ~/images/force-graph/userId/userTableId/fileName
        // ~路径读取application.yaml配置文件!
        String fileName = file.getOriginalFilename();
        String filePath = FILE_PATH + "/images/force-graph/" + userId + "/" + userTableId + "/" + fileName;
        // 判断是否有该文件夹, 没有则创建
        File dir = new File(filePath.substring(0, filePath.lastIndexOf("/")));
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                log.error("创建文件夹失败: {}", e.getMessage());
                return null;
            }
        }
        File dest = new File(filePath);
        // 判断是否重名
        if (dest.exists()) {
            log.error("上传图片失败: 文件已存在");
            return null;
        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("上传图片失败: {}", e.getMessage());
            return null;
        }
        return filePath;
    }

    /**
     * @description: 更新结点
     * @author: Heng_Xin 
     * @date: 2024/12/3 17:01
     * @param: userId
     * @param: userTableId
     * @param: nodeDTO
     * @return: 是否更新成功
     **/
    public Boolean updateNode(Long userId, Long userTableId, NodeDTO nodeDTO) {
        NodeDO nodeDO = new NodeDO();
        nodeDO.setNodeId(nodeDTO.getNodeId());
        nodeDO.setUserId(userId);
        nodeDO.setUserTableId(userTableId);
        nodeDO.setLegendId(nodeDTO.getLegendId());
        nodeDO.setName(nodeDTO.getName());
        nodeDO.setImgUrl(nodeDTO.getImgUrl());
        nodeDO.setDescription(nodeDTO.getDescription());
        return nodeDAO.updateNode(nodeDO) > 0;
    }
}
