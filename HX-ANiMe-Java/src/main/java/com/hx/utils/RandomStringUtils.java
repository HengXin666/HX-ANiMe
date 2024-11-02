package com.hx.utils;

import java.security.SecureRandom;

/**
 * @BelongsProject: HXANiMeWeb
 * @BelongsPackage: com.hx.utils
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-01  10:40
 * @Description: 生成随机字符串
 * @Version: 1.0
 */
public class RandomStringUtils {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@%^&*()_+-=|<>!~#$?";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int LENGTH = 16;

    /**
     * @description: 生成随机字符串
     * @author: Heng_Xin
     * @date: 2024/11/1 10:42
     * @return: String
     **/
    public static String generateRandomString() {
        StringBuilder result = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; ++i) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            result.append(CHARACTERS.charAt(index));
        }
        return result.toString();
    }
}
