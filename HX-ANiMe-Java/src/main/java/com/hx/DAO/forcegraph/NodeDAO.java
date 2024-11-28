package com.hx.DAO.forcegraph;

import com.hx.pojo.DO.forcegraph.NodeDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.DAO.forcegraph
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-22  09:45
 * @Description: 结点表DAO
 * @Version: 1.0
 */

@Slf4j
@Repository
public class NodeDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * @description: 添加新结点, 返回结点ID
     * @author: Heng_Xin
     * @date: 2024/11/22 9:51
     * @param: nodeDO
     * @return: Long
     **/
    public Long addNode(NodeDO nodeDO) {
        String sql = "INSERT INTO Nodes (user_id, user_table_id, legend_id, name, img_url, description) VALUES (?, ?, ?, ?, ?, ?)";

        // 使用 KeyHolder 获取自增主键
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"}); // "id" 是主键列名
            ps.setLong(1, nodeDO.getUserId());
            ps.setLong(2, nodeDO.getUserTableId());
            ps.setLong(3, nodeDO.getLegendId());
            ps.setString(4, nodeDO.getName());
            ps.setString(5, nodeDO.getImgUrl());
            ps.setString(6, nodeDO.getDescription());
            return ps;
        }, keyHolder);

        // 返回主键 ID
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    public List<NodeDO> queryLegend(Long userId, Long userTableId) {
        String sql = "SELECT * FROM Nodes WHERE user_id = ? AND user_table_id = ?";
        return jdbcTemplate.query(
            sql,
            new Object[]{userId, userTableId},
            (rs, rowNum) -> {
                NodeDO node = new NodeDO();
                node.setNodeId(rs.getLong("node_id"));
                node.setUserId(rs.getLong("user_id"));
                node.setUserTableId(rs.getLong("user_table_id"));
                node.setLegendId(rs.getLong("legend_id"));
                node.setName(rs.getString("name"));
                node.setImgUrl(rs.getString("img_url"));
                node.setDescription(rs.getString("description"));
                return node;
            }
        );
    }
}
