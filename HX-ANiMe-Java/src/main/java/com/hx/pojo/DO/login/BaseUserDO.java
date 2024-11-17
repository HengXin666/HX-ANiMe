package com.hx.pojo.DO.login;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "base_user", uniqueConstraints = @UniqueConstraint(columnNames = "email"), indexes = @Index(name = "idx_email", columnList = "email"))
public class BaseUserDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid; // 用户ID

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 2 COMMENT '用户类型: 1:管理员; 2:普通用户; 3:只读用户;'")
    private Integer type; // 用户类型

    @Column(name = "user_name", nullable = false, length = 100, unique = true, columnDefinition = "VARCHAR(100) COMMENT '用户名(唯一)'")
    private String userName; // 用户名(唯一)

    @Column(nullable = false, length = 100, columnDefinition = "VARCHAR(100) COMMENT '昵称'")
    private String nickname; // 昵称

    @Column(nullable = false, length = 255, columnDefinition = "VARCHAR(255) COMMENT '用户邮箱'")
    private String email; // 用户邮箱

    @Column(nullable = false, length = 200, columnDefinition = "VARCHAR(200) COMMENT '用户密码'")
    private String password; // 用户密码

    @Column(nullable = false, length = 200, columnDefinition = "VARCHAR(200) COMMENT '密码加盐'")
    private String salt; // 密码加盐

    @Column(length = 255, columnDefinition = "VARCHAR(255) COMMENT '用户头像'")
    private String avatar; // 用户头像

    @Column(name = "last_login_time", columnDefinition = "DATETIME COMMENT '最后登录时间'")
    private LocalDateTime lastLoginTime; // 最后登录时间

    @Column(name = "cre_time", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'")
    private LocalDateTime creTime; // 创建时间
}
