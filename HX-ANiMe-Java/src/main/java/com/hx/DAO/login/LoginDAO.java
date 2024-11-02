package com.hx.DAO.login;

import com.hx.pojo.login.DO.BaseUserDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        String sql = "SELECT type, user_name, nickname, email, password, salt, avatar FROM base_user WHERE user_name = ?";
        try {
            BaseUserDO user = jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{baseUserDO.getUserName()},
                    new RowMapper<BaseUserDO>() {
                        @Override
                        public BaseUserDO mapRow(ResultSet rs, int rowNum) throws SQLException {
                            BaseUserDO user = new BaseUserDO();
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
     * @return: Boolean 是否添加成功
     **/
    public Boolean insertUser(BaseUserDO baseUserDO) {
        String sql = "INSERT INTO base_user (user_name, nickname, email, password, salt, avatar, last_login_time, cre_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            int rowsAffected = jdbcTemplate.update(
                    sql,
                    baseUserDO.getUserName(),
                    baseUserDO.getNickname(),
                    baseUserDO.getEmail(),
                    baseUserDO.getPassword(),
                    baseUserDO.getSalt(),
                    baseUserDO.getAvatar(),
                    baseUserDO.getLastLoginTime(),
                    baseUserDO.getCreTime()
            );
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            // 处理数据访问异常
            log.info("Data access error: " + e.getMessage());
            return false; // 返回false表示添加失败
        }
    }
}
