package com.hx.DAO.forcegraph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.DAO.forcegraph
 * @Author: Heng_Xin
 * @CreateTime: 2024-12-12  15:49
 * @Description: 图表api密匙数据库DAO
 * @Version: 1.0
 */

@Repository
public class GraphApiKeyDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * @description: 新增或者更新图表api密匙, 返回新增或者更新成功与否
     * @author: Heng_Xin
     * @date: 2024/12/12 16:04
     * @param: userId
     * @param: userTableId
     * @param: apiMd5
     * @return: boolean
     **/
    public boolean addGraphApiKey(Long userId, Long userTableId, String apiMd5) {
        String sql = "INSERT INTO graph_api_keys (user_id, user_table_id, api_key, create_time, update_time) VALUES (?, ?, ?, NOW(), NOW()) ON DUPLICATE KEY UPDATE api_key = VALUES(api_key), update_time = NOW(), create_time = create_time;";
        return jdbcTemplate.update(sql, userId, userTableId, apiMd5) > 0;
    }
}
