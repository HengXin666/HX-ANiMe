package com.hx;

import com.hx.pojo.login.DO.BaseUserDO;
import com.hx.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@SpringBootApplication
public class App {
    @Autowired
    private static JdbcTemplate jdbcTemplate;
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        log.info("key: {}", JWTUtils.SECRET_KEY);
    }
}
