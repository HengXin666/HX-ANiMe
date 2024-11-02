package com.hx.utils;

import org.springframework.util.DigestUtils;

/**
 * @BelongsProject: HXANiMeWeb
 * @BelongsPackage: com.hx.utils
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-01  00:10
 * @Description: TODO
 * @Version: 1.0
 */
public class Md5Utils {
    /**
     * @description: 以uid作为盐进行md5加密
     * @author: Heng_Xin
     * @date: 2024/11/1 0:11
     * @param: password 密码明文
     * @param: salt 盐
     * @return: String md5加密后的密码
     **/
    public static String md5(String password, String salt) {
        return DigestUtils.md5DigestAsHex((salt + password).getBytes());
    }
}
