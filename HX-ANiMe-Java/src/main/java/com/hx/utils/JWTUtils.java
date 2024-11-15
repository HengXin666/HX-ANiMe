package com.hx.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class JWTUtils {
    public static String SECRET_KEY = "!@#$^HengXin%Loli0721_-"; // 密钥
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 过期时间: 1小时

    // 生成JWT，传入id和name
    public static String generateToken(Long id, String name) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withClaim("id", id)  // 添加用户的id
                .withClaim("name", name)  // 添加用户的name
                .withIssuedAt(new Date())  // 创建时间
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // 过期时间
                .sign(algorithm);
    }

    // 验证JWT是否合法，并检查是否过期
    public static boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // 检查token是否过期
            if (isTokenExpired(decodedJWT)) {
                return false;
            }

            // 提取Token中的id和name
            Long id = decodedJWT.getClaim("id").asLong();
            String name = decodedJWT.getClaim("name").asString();

            // 可选：在这里可以查询数据库进行进一步验证，确保用户的 `id` 和 `name` 仍然是有效的
            // 例如：可以验证数据库中是否有该用户 ID，或验证其他业务逻辑相关信息

            return id != null && name != null; // 确保 id 和 name 存在，并且符合预期
        } catch (Exception e) {
            return false;
        }
    }

    // 检查token是否过期
    private static boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }

    // 提取id和name
    public static Long extractId(String token) {
        return JWT.decode(token).getClaim("id").asLong();
    }

    public static String extractName(String token) {
        return JWT.decode(token).getClaim("name").asString();
    }
}
