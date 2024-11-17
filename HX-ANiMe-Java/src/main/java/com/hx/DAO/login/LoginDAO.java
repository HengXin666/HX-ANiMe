package com.hx.DAO.login;

import com.hx.pojo.DO.login.BaseUserDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;

/**
 * @BelongsProject: HXANiMeWeb
 * @BelongsPackage: com.hx.DAO.login
 * @Author: Heng_Xin
 * @CreateTime: 2024-10-31  23:31
 * @Description: 用户登录数据访问对象
 * @Version: 1.0
 */
@Slf4j
@Repository
public class LoginDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * @description: 通过用户名查询用户数据
     * @author: Heng_Xin
     * @date: 2024/10/31 23:40
     * @param: baseUserDO
     * @return: BaseUserDO 用户信息
     **/
    public BaseUserDO queryLoginByUserName(BaseUserDO baseUserDO) {
        log.info("查询用户信息: {}", baseUserDO);
        String sql = "SELECT uid, type, user_name, nickname, email, password, salt, avatar FROM base_user WHERE user_name = ?";
        try {
            BaseUserDO user = jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{baseUserDO.getUserName()},
                    new RowMapper<BaseUserDO>() {
                        @Override
                        public BaseUserDO mapRow(ResultSet rs, int rowNum) throws SQLException {
                            BaseUserDO user = new BaseUserDO();
                            user.setUid(rs.getLong("uid"));
                            user.setType(rs.getInt("type"));
                            user.setUserName(rs.getString("user_name"));
                            user.setNickname(rs.getString("nickname"));
                            user.setEmail(rs.getString("email"));
                            user.setPassword(rs.getString("password"));
                            user.setSalt(rs.getString("salt"));
                            user.setAvatar(rs.getString("avatar"));
                            return user;
                        }
                    }
            );
            return user;
        } catch (EmptyResultDataAccessException e) {
            log.info("未找到用户: {}", baseUserDO.getUserName());
        } catch (DataAccessException e) {
            log.error("数据访问错误: {}", e.getMessage());
        }
        return null; // 查询不到用户时返回null
    }

    /**
     * @description: 添加用户
     * @author: Heng_Xin
     * @date: 2024/11/1 10:46
     * @param: baseUserDO
     * @return: Long 成功则返回用户ID, 失败则返回null
     **/
    public Long insertUser(BaseUserDO baseUserDO) {
        // 插入 SQL 语句
        String sql = "INSERT INTO base_user (user_name, nickname, email, password, salt, avatar, last_login_time, cre_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // 创建 KeyHolder 来接收生成的主键
            KeyHolder keyHolder = new GeneratedKeyHolder();

            // 使用 PreparedStatementCreator 来执行插入操作并返回主键
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"}); // "id" 是主键的列名
                    ps.setString(1, baseUserDO.getUserName());
                    ps.setString(2, baseUserDO.getNickname());
                    ps.setString(3, baseUserDO.getEmail());
                    ps.setString(4, baseUserDO.getPassword());
                    ps.setString(5, baseUserDO.getSalt());
                    ps.setString(6, baseUserDO.getAvatar());

                    // 检查并处理时间字段
                    if (baseUserDO.getLastLoginTime() != null) {
                        ps.setTimestamp(7, Timestamp.valueOf(baseUserDO.getLastLoginTime()));
                    } else {
                        ps.setTimestamp(7, null); // 或使用 new Timestamp(System.currentTimeMillis())
                    }

                    if (baseUserDO.getCreTime() != null) {
                        ps.setTimestamp(8, Timestamp.valueOf(baseUserDO.getCreTime()));
                    } else {
                        ps.setTimestamp(8, new Timestamp(System.currentTimeMillis())); // 设置当前时间
                    }

                    return ps;
                }
            }, keyHolder);

            // 获取生成的主键 ID
            return keyHolder.getKey().longValue();  // 返回生成的用户ID
        } catch (DataAccessException e) {
            log.info("Data access error: " + e.getMessage());
            return null;  // 发生异常时返回 null
        }
    }
}
