package com.xht.controller;

import com.xht.service.feign.Service02Feign;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author : YIYUANYUAN
 * @description :
 * @date: 2024/1/6  9:14
 */

@Slf4j
@RestController
@RequestMapping("/service01")
public class CloudController {

    @Value("${user.service}")
    private String service;


    @Autowired
    Service02Feign service02Feign;

    @GetMapping
    public String get(){
        return "hello this is "+service;
    }

    @GetMapping("/login")
    public String login(){
        return "service01 login";
    }


    @GetMapping("/needLogin")
    public String needLogin(){
        return "service01 needLogin";
    }


    @GetMapping("/feign")
    public String openFeign(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes!=null){
            HttpSession session = requestAttributes.getRequest().getSession();
            session.setAttribute("test",123);
        }
        String s = service02Feign.openFeign();
        log.info(s);
        return "this is service01 openFeign";
    }


    @GetMapping("/feign/async")
    public String openFeignAsync(){
        //feign请求拦截器使用 RequestContextHolder.getRequestAttributes() 获取请求数据，该数据使用ThreadLocal 保存
        //异步的情况下，是新开一个线程，没有这些数据

        //异步调用解决线程丢失上下文
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            String s = service02Feign.openFeign();
            log.info(s);
        });
        Thread thread = new Thread();
        ThreadLocal<Object> objectThreadLocal = new ThreadLocal<>();
        objectThreadLocal.set("123");
        objectThreadLocal.get();

        try {
            future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "this is service01 openFeign";
    }

}
