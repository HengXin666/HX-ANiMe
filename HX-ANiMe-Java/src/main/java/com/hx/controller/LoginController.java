package com.hx.controller;

import com.hx.pojo.login.DTO.JwtTokenDTO;
import com.hx.pojo.login.DTO.UserLoginRequestDTO;
import com.hx.pojo.login.DTO.UserRegisterRequestDTO;
import com.hx.pojo.vo.JsonVo;
import com.hx.pojo.vo.ResultStatus;
import com.hx.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private LoginService loginService; // 注入服务层

    /**
     * @brief 登录接口
     * @param requestDTO 登录请求 DTO
     * @return 登录凭证
     */
    @PostMapping("/query-login")
    public JsonVo<String> queryLogin(@RequestBody UserLoginRequestDTO requestDTO) {
        log.info("login: {}", requestDTO);

        // 判断参数是否为空
        if (requestDTO.getUserName() == null || requestDTO.getPassword() == null) {
            return JsonVo.create(
                null,
                ResultStatus.PARAMS_INVALID.getCode(),
                "用户名或密码为空"
            );
        }

        // 交给Service层处理
        String token = loginService.queryLogin(requestDTO);
        if (token == null) {
            return JsonVo.create(
                null,
                ResultStatus.PARAMS_INVALID.getCode(),
                "账号密码错误"
            );
        }
        return JsonVo.success(token);
    }

    /**
     * @brief 注册接口
     * @param requestDTO 注册请求 DTO
     * @return 登录凭证
     */
    @PostMapping("/add-user")
    public JsonVo<String> addUser(@RequestBody UserRegisterRequestDTO requestDTO) {
        // 判断参数是否为空
        if (requestDTO.getUserName() == null || requestDTO.getPassword() == null) {
            return JsonVo.create(
                    null,
                    ResultStatus.PARAMS_INVALID.getCode(),
                    "用户名或密码为空"
            );
        }

        String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!requestDTO.getEmail().matches(pattern)) {
            return JsonVo.create(
                    null,
                    ResultStatus.PARAMS_INVALID.getCode(),
                    "邮箱格式不正确"
            );
        }

        // 交给Service层处理
        String token = loginService.addUser(requestDTO);
        if (token == null) {
            return JsonVo.create(
                    null,
                    ResultStatus.PARAMS_INVALID.getCode(),
                    "用户名已存在或邮箱已注册"
            );
        }
        return JsonVo.success(token);
    }

    /**
     * @brief 登出接口, 表示删除会话或 token
     */
    @PostMapping("/remove-logout")
    public String removeLogout() {
        return "logout";
    }

    /**
     * @brief 刷新Token接口
     *
     * @return 登录凭证
     */
    @PostMapping("/update-token")
    public JsonVo<String> updateToken(@RequestBody JwtTokenDTO requestDTO) {
         String token = loginService.updateToken(requestDTO);
         if (token == null)
             return JsonVo.fail(null);
         return JsonVo.success(token);
    }
}
