package com.hx.DAO.forcegraph;

import com.hx.pojo.DO.forcegraph.UserTablesDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
