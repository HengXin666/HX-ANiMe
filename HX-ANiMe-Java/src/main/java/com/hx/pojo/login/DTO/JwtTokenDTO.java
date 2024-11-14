package com.hx.pojo.login.DTO;

import lombok.Data;

@Data
public class JwtTokenDTO {
    private Long userId;
    private String userName;
    private String token;
}
