package com.hx.DAO.forcegraph;

import com.hx.pojo.DO.forcegraph.LegendDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
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
        String sql = "SELECT * FROM Legends WHERE user_id = ? AND user_table_id = ?";
        return jdbcTemplate.query(
            sql,
            new Object[]{userId, userTableId},
            (rs, rowNum) -> {
                LegendDO legend = new LegendDO();
                legend.setLegendId(rs.getLong("legend_id"));
                legend.setUserId(rs.getLong("user_id"));
                legend.setUserTableId(rs.getLong("user_table_id"));
                legend.setLegendName(rs.getString("legend_name"));
                legend.setLegendColor(rs.getString("legend_color"));
                return legend;
            }
        );
    }

    /**
     * @description: 新增图例, 并且返回图例id
     * @author: Heng_Xin
     * @date: 2024/11/22 10:30
     * @param: legendDO
     * @return: Long
     **/
    public Long addLegend(LegendDO legendDO) {
        String sql = "INSERT INTO Legends (user_id, user_table_id, legend_name, legend_color) VALUES (?, ?, ?, ?)";

        // 使用 KeyHolder 获取自增主键
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"}); // "id" 是主键列名
            ps.setLong(1, legendDO.getUserId());
            ps.setLong(2, legendDO.getUserTableId());
            ps.setString(3, legendDO.getLegendName());
            ps.setString(4, legendDO.getLegendColor());
            return ps;
        }, keyHolder);

        // 返回主键 ID
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    /**
     * @description: 删除指定用户id和图表id的所有图例, 返回删除的行数
     * @author: Heng_Xin
     * @date: 2024/12/11 11:22
     * @param: userId
     * @param: userTableId
     * @return: int
     **/
    public int removeAllLegend(Long userId, Long userTableId) {
        String sql = "DELETE FROM Legends WHERE user_id = ? AND user_table_id = ?";
        return jdbcTemplate.update(sql, userId, userTableId);
    }
}
