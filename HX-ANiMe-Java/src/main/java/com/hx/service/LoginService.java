package com.hx.service;

import com.hx.DAO.login.LoginDAO;
import com.hx.pojo.DO.login.BaseUserDO;
import com.hx.pojo.DTO.login.JwtTokenDTO;
import com.hx.pojo.DTO.login.UserLoginRequestDTO;
import com.hx.pojo.DTO.login.UserRegisterRequestDTO;
import com.hx.utils.JWTUtils;
import com.hx.utils.Md5Utils;
import com.hx.utils.RandomStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

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

        return JWTUtils.generateToken(baseUserDO.getUid(), baseUserDO.getUserName());
    }

    /**
     * @description: 添加用户
     * @author: Heng_Xin
     * @date: 2024/11/14 17:17
     * @param: requestDTO
     * @return: String
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
        Long userId = loginDAO.insertUser(baseUserDO);
        if (userId == null) // 注册失败
            return null;
        return JWTUtils.generateToken(userId, baseUserDO.getUserName());
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
        if (!JWTUtils.validateToken(requestDTO.getToken()))
            return null;
        return JWTUtils.generateToken(
            JWTUtils.extractId(requestDTO.getToken()),
            requestDTO.getUserName()
        ); // 生成新token
    }
}
