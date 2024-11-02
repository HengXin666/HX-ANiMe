package com.hx.pojo.login.DTO;

import lombok.Data;

@Data
public class JwtTokenDTO {
    private String userName;
    public String token;
}
