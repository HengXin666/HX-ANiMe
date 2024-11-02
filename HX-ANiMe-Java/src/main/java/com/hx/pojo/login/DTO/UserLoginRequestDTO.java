package com.hx.pojo.login.DTO;

import lombok.Data;

/**
 * 用户登录 DTO
 */
@Data
public class UserLoginRequestDTO {
    private String userName;
    private String password;
}
