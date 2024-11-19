package com.hx.DAO.forcegraph;

import com.hx.pojo.DO.forcegraph.LegendDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.DAO.forcegraph
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-19  17:25
 * @Description:
 * @Version: 1.0
 */
@Slf4j
@Repository
public class LegendDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    // 查询指定用户id和图表id的所有图例
    public List<LegendDO> queryLegend(Long userId, Long userTableId) {
        String sql = "SELECT * FROM Legends WHERE user_id = "
                + userId + " AND user_table_id = " + userTableId;
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> new LegendDO());
        } catch (Exception e) {
            log.error("queryLegend error: {}", e.getMessage());
            return null;
        }
    }
}
