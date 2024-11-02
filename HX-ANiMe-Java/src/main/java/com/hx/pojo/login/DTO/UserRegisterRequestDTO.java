package com.hx.pojo.login.DTO;

import lombok.Data;

/**
 * 用户注册信息DTO
 */
@Data
public class UserRegisterRequestDTO {
    private String userName;
    private String password;
    private String email;
}
