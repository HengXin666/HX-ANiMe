package com.hx.config;

import com.hx.utils.JWTUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Enumeration;
/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.config
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-14  09:59
 * @Description: 请求的token拦截器
 * @Version: 1.0
 */
public class RequestInterceptor implements HandlerInterceptor {
    /**
     * 预处理回调方法，实现处理器的预处理（如检查登陆），第三个参数为响应的处理器，自定义Controller
     * 返回值：
     * true表示继续流程（如调用下一个拦截器或处理器）；
     * false表示流程中断（如登录检查失败），不会继续调用其他的拦截器或处理器，此时我们需要通过response来产生响应；
     */
    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object o
    ) throws Exception {
        // 从请求头中获取 Authorization 字段
        String authorizationHeader = request.getHeader("Authorization");

        // 判断是否存在 Bearer Token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 截取 Bearer 后面的部分，获取 Token
            String token = authorizationHeader.substring(7);
            if (!JWTUtils.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                return false;
            }
            try {
                // 解析 Token，提取 id 和 name
                Long id = JWTUtils.extractId(token);
                String name = JWTUtils.extractName(token);

                // 将 id 和 name 放入请求属性中，供后续处理使用
                request.setAttribute("userId", id);
                request.setAttribute("userName", name);

            } catch (Exception e) {
                // 如果解析 Token 失败，可以返回错误信息，或者设置相应的状态码
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
                return false;
            }
        } else {
            // 如果没有 Authorization 头，返回错误信息或状态码
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 如果 token 解析成功，放行请求
        return true;
    }

    /**
     * 后处理回调方法，实现处理器的后处理（但在渲染视图之前），此时我们可以通过modelAndView（模型和视图对象）对模型数据进行处理或对视图进行处理，modelAndView也可能为null。
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 整个请求处理完毕回调方法，即在视图渲染完毕时回调，如性能监控中我们可以在此记录结束时间并输出消耗时间，还可以进行一些资源清理，类似于try-catch-finally中的finally，但仅调用处理器执行链中
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
