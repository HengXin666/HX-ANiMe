package com.hx.DAO.forcegraph;

import com.hx.pojo.DO.forcegraph.EdgeDO;
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
 * @CreateTime: 2024-11-29  09:12
 * @Description: 边的数据库操作
 * @Version: 1.0
 */
@Slf4j
@Repository
public class EdgeDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * @description: 添加一条边, 返回主键
     * @author: Heng_Xin
     * @date: 2024/11/29 9:23
     * @param: edgeDO
     * @return: Long
     **/
    public Long addEdge(EdgeDO edgeDO) {
        String sql = "INSERT INTO edges (user_id, user_table_id, from_node_id, to_node_id) VALUES (?, ?, ?, ?)";
        // 使用 KeyHolder 获取自增主键
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"}); // "id" 是主键列名
            ps.setLong(1, edgeDO.getUserId());
            ps.setLong(2, edgeDO.getUserTableId());
            ps.setLong(3, edgeDO.getFromNodeId());
            ps.setLong(4, edgeDO.getToNodeId());
            return ps;
        }, keyHolder);

        // 返回主键 ID
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    /**
     * @description: 查询所有边
     * @author: Heng_Xin
     * @date: 2024/11/29 9:38
     * @param: userId
     * @param: userTableId
     * @return: List<EdgeDO>
     **/
    public List<EdgeDO> queryEdges(Long userId, Long userTableId) {
        String sql = "SELECT * FROM edges WHERE user_id = ? AND user_table_id = ?";
        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                EdgeDO edge = new EdgeDO();
                edge.setEdgeId(rs.getLong("edge_id"));
                edge.setUserId(rs.getLong("user_id"));
                edge.setUserTableId(rs.getLong("user_table_id"));
                edge.setFromNodeId(rs.getLong("from_node_id"));
                edge.setToNodeId(rs.getLong("to_node_id"));
                return edge;
            },
            userId,
            userTableId
        );
    }

    /**
     * @description: 删除边, 返回删除行数
     * @author: Heng_Xin
     * @date: 2024/12/6 9:41
     * @param: userId
     * @param: userTableId
     * @param: edgeId
     * @return: int
     **/
    public int removeEdge(Long userId, Long userTableId, Long edgeId) {
        String sql = "DELETE FROM edges WHERE user_id = ? AND user_table_id = ? AND edge_id = ?";
        return jdbcTemplate.update(sql, userId, userTableId, edgeId);
    }

    /**
     * @description: 删除所有与指定节点相连的边, 返回删除行数
     * @author: Heng_Xin
     * @date: 2024/12/6 10:06
     * @param: userId
     * @param: userTableId
     * @param: nodeId
     * @return: int
     **/
    public int removeAllEdgeByNodeId(Long userId, Long userTableId, Long nodeId) {
        String sql = "DELETE FROM edges WHERE user_id = ? AND user_table_id = ? AND (from_node_id = ? OR to_node_id = ?)";
        return jdbcTemplate.update(sql, userId, userTableId, nodeId, nodeId);
    }

    /**
     * @description: 删除指定用户的图表的所有边, 返回删除行数
     * @author: Heng_Xin
     * @date: 2024/12/11 11:22
     * @param: userId
     * @param: userTableId
     * @return: int
     **/
    public int removeAllEdge(Long userId, Long userTableId) {
        String sql = "DELETE FROM edges WHERE user_id = ? AND user_table_id = ?";
        return jdbcTemplate.update(sql, userId, userTableId);
    }
}
