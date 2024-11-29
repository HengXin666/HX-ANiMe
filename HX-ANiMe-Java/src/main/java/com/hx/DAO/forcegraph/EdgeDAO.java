package com.hx.DAO.forcegraph;

import com.hx.pojo.DO.forcegraph.EdgeDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

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
        String sql = "INSERT INTO Edges (user_id, user_table_id, from_node_id, to_node_id) VALUES (?, ?, ?, ?)";
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
}
