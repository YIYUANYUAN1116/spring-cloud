package com.xht.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * @author : YIYUANYUAN
 * @date: 2024/1/7  14:20
 * feign远程调用相当与发起一个新的请求，不会携带当前线程的请求头，请求体等信息，例如用户登陆信息
 * 需要在请求拦截器中拦截添加
 */

@Configuration
@Slf4j
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){

        return (requestTemplate)->{
            //1.获取请求数据
            ServletRequestAttributes  requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null){
                //老的请求
                HttpServletRequest request = requestAttributes.getRequest();
                //2.同步请求头数据
                String token = request.getHeader("token");
                //这里手动添加测试
                if (token==null){
                    token = UUID.randomUUID().toString();
                }
                requestTemplate.header("token",token);
            }
        };
    }
}
