package com.hx.service;

import com.hx.DAO.login.LoginDAO;
import com.hx.pojo.login.DO.BaseUserDO;
import com.hx.pojo.login.DTO.JwtTokenDTO;
import com.hx.pojo.login.DTO.UserLoginRequestDTO;
import com.hx.pojo.login.DTO.UserRegisterRequestDTO;
import com.hx.utils.JWTUtils;
import com.hx.utils.Md5Utils;
import com.hx.utils.RandomStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class LoginService {
    @Autowired
    private LoginDAO loginDAO;
    /**
     * @description:
     * @author: Heng_Xin
     * @date: 2024/10/31 19:21
     * @param: requestDTO
     * @return: String token
     **/
    public String queryLogin(@RequestBody UserLoginRequestDTO requestDTO) {
        BaseUserDO baseUserDO = new BaseUserDO();
        baseUserDO.setUserName(requestDTO.getUserName());

        // 根据用户名查询用户信息
        baseUserDO = loginDAO.queryLoginByUserName(baseUserDO);
        log.info("查询到的用户信息: {}", baseUserDO);
        if (baseUserDO == null) // 用户不存在
            return null;

        // 使用md5+盐
        if (!Md5Utils.md5(requestDTO.getPassword(), baseUserDO.getSalt()).equals(baseUserDO.getPassword())) // 密码错误
            return null;

        return JWTUtils.generateToken(baseUserDO.getUserName());
    }

    /**
     * @description: 注册用户
     * @author: Heng_Xin 
     * @date: 2024/11/1 10:33 
     * @param: username
     * @param: password
     * @return: String token
     **/
    public String addUser(@RequestBody UserRegisterRequestDTO requestDTO) {
        BaseUserDO baseUserDO = new BaseUserDO();
        baseUserDO.setSalt(RandomStringUtils.generateRandomString()); // 密码加盐
        baseUserDO.setUserName(requestDTO.getUserName());
        baseUserDO.setPassword(Md5Utils.md5(requestDTO.getPassword(), baseUserDO.getSalt()));
        baseUserDO.setEmail(requestDTO.getEmail());
        baseUserDO.setNickname(requestDTO.getUserName());
        // 创建时间
        baseUserDO.setCreTime(LocalDateTime.now());
        if (!loginDAO.insertUser(baseUserDO)) // 注册失败
            return null;
        return JWTUtils.generateToken(baseUserDO.getUserName());
    }

    public String logout() {
        return "logout success";
    }

    /**
     * @description: 刷新token
     * @author: Heng_Xin
     * @date: 2024/10/31 19:22
     * @param: requestDTO JwtTokenDTO
     * @return: String 新token
     **/
    public String updateToken(@RequestBody JwtTokenDTO requestDTO) {
        // 验证token是否合法
        if (!JWTUtils.validateToken(requestDTO.getToken(), requestDTO.getUserName()))
            return null;
        return JWTUtils.generateToken(requestDTO.getUserName()); // 生成新token
    }
}
