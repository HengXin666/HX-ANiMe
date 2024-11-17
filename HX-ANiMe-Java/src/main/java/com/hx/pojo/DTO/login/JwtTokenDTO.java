package com.hx.pojo.DTO.login;

import lombok.Data;

@Data
public class JwtTokenDTO {
    private Long userId;
    private String userName;
    private String token;
}
