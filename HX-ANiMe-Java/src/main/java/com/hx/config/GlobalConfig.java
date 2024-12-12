package com.hx.config;

import com.hx.utils.JWTUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.config
 * @Author: Heng_Xin
 * @CreateTime: 2024-12-04  11:40
 * @Description: 全局配置类
 * @Version: 1.0
 */

@Data
@Configuration
public class GlobalConfig {
    // 服务器URL
    @Value("${server.url}")
    private String serverUrl;

    // 文件路径
    @Value("${file.path}")
    private String filePath;

    // JWT密钥
    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    // JWT过期时间
    @Value("${jwt.expiration-time}")
    private Long jwtExpirationTime;

    @PostConstruct
    public void init() {
        // JWT Utils初始化
        JWTUtils.SECRET_KEY = jwtSecretKey;
        JWTUtils.EXPIRATION_TIME = jwtExpirationTime;
    }
}
