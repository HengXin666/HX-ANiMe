package com.hx.pojo.DTO.login;

import lombok.Data;

/**
 * 用户登录 DTO
 */
@Data
public class UserLoginRequestDTO {
    private String userName;
    private String password;
}
