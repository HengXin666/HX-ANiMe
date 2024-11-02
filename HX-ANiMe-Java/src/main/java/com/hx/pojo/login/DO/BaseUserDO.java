package com.hx.pojo.login.DO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @BelongsProject: HXANiMeWeb
 * @BelongsPackage: com.hx.pojo.login.DO
 * @Author: Heng_Xin
 * @CreateTime: 2024-10-31  23:09
 * @Description: 基本用户数据表DO
 * @Version: 1.0
 */
@Data
public class BaseUserDO {
    private Long uid;               // 用户ID
    private Integer type;           // 用户类型
    private String userName;        // 用户名(唯一)
    private String nickname;        // 昵称
    private String email;           // 用户邮箱
    private String password;        // 用户密码
    private String salt;            // 密码加盐
    private String avatar;          // 用户头像
    private LocalDateTime lastLoginTime;  // 最后登录时间
    private LocalDateTime creTime;        // 创建时间
}
