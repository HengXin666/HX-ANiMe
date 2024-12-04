package com.hx.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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
}
