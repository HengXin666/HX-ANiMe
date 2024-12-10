package com.hx.DAO.forcegraph;

import com.hx.pojo.DO.forcegraph.UserTablesDO;
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
 * @CreateTime: 2024-12-10  15:33
 * @Description: 用户图表DAO
 * @Version: 1.0
 */
@Repository
public class UserTablesDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * @description: 根据用户ID查询用户图表
     * @author: Heng_Xin
     * @date: 2024/12/10 15:33
     * @param: userId
     * @return: List<UserTablesDO>
     **/
    public List<UserTablesDO> queryUserTables(Long userId) {
        String sql = "SELECT * FROM user_tables WHERE user_id = ?";
        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                UserTablesDO userTables = new UserTablesDO();
                userTables.setId(rs.getLong("user_table_id"));
                userTables.setUserId(rs.getLong("user_id"));
                userTables.setName(rs.getString("table_name"));
                userTables.setImgUrl(rs.getString("icon_url"));
                userTables.setDescription(rs.getString("description"));
                return userTables;
            },
            userId
        );
    }

    /**
     * @description: 添加用户图表, 返回图表ID
     * @author: Heng_Xin
     * @date: 2024/12/10 17:54
     * @param: userTablesDO
     * @return: Long
     **/
    public Long addUserTables(UserTablesDO userTablesDO) {
        String sql = "INSERT INTO user_tables (user_id, table_name, icon_url, description) VALUES (?, ?, ?, ?)";
        // 使用 KeyHolder 获取自增主键
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"}); // "id" 是主键列名
            ps.setLong(1, userTablesDO.getUserId());
            ps.setString(2, userTablesDO.getName());
            ps.setString(3, userTablesDO.getImgUrl());
            ps.setString(4, userTablesDO.getDescription());
            return ps;
        }, keyHolder);

        // 返回主键 ID
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }
}
