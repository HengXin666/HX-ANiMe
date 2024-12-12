package com.hx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @BelongsProject: HX-ANiMe-Java
 * @BelongsPackage: com.hx.config
 * @Author: Heng_Xin
 * @CreateTime: 2024-11-14  09:57
 * @Description: 拦截器
 * @Version: 1.0
 */
@Configuration
public class WebRequestInterceptor implements WebMvcConfigurer {
    /**
     * @description: 自定义拦截器
     * @author: Heng_Xin
     * @date: 2024/12/12 15:40
     * @param: registry
     * @return: void
     **/
    public void addInterceptors(InterceptorRegistry registry) {
        // RequestInterceptor为具体拦截逻辑的执行类 实现了HandlerInterceptor接口
        // addPathPatterns("/test/**")  意义是访问路径下/test 下所有的访问路径都需要被RequestInterceptor拦截
        // excludePathPatterns 这个访问路径/test/exception则不在被RequestInterceptor拦截的范围
        // /user/** user下所有路径都包含在内 例：/user/api 、/user/api/zz
        // /user/* 只有user下一层路径包含在内 例：/user/api(包含) 、/user/api/zz(不包含)
        // /test/queryUser接口则是token验证后，把token为xx的玩家信息放入Request中，方便接口拿取
        registry.addInterceptor(new RequestInterceptor())
            .addPathPatterns("/**") // 所有请求
            .excludePathPatterns("/login/**")  // 登录接口 不需要拦截
            .excludePathPatterns("/images/**") // 图片接口 不需要拦截
            .excludePathPatterns("/force-graph-api/**"); // 力导向图api接口 不需要拦截
        // 注意, api是指非浏览器访问的接口; 不要把某些架构搞混了!
    }

    /**
     * @description: 跨域支持 比如说vue 的axios访问
     * @author: Heng_Xin
     * @date: 2024/12/12 15:41
     * @param: registry
     * @return: void
     **/
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有请求
                .allowedOriginPatterns("*") // 允许跨域的域名, 可以用*表示允许任何域名使用
                .allowCredentials(true) // 允许携带cookie
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600 * 24);
    }
}
