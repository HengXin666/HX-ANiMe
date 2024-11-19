package com.hx.service;

import com.hx.DAO.forcegraph.LegendDAO;
import com.hx.pojo.DO.forcegraph.LegendDO;
import com.hx.pojo.DTO.forcegraph.LegendDTO;
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
public class ForceGraphService {
    @Autowired
    private LegendDAO legendDAO;
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
}
