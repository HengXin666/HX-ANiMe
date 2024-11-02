package com.hx.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class JWTUtils {
    public static final String SECRET_KEY = "!@#$^HengXin%Loli0721_-"; // 密匙
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 过期时间: 1小时

    // 生成JWT
    public static String generateToken(String subject) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withSubject(subject) // 主题
                .withIssuedAt(new Date()) // 创建时间
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 过期时间
                .sign(algorithm);
    }

    // 验证JWT
    public static boolean validateToken(String token, String subject) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // 检查是否过期
            return !isTokenExpired(decodedJWT) && decodedJWT.getSubject().equals(subject);
        } catch (Exception e) {
            return false;
        }
    }

    // 检查token是否过期
    private static boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }

    // 提取主题
    public static String extractSubject(String token) {
        return JWT.decode(token).getSubject();
    }
}