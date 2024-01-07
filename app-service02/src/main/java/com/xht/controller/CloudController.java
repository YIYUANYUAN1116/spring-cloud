package com.xht.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

/**
 * @author : YIYUANYUAN
 * @description :
 * @date: 2024/1/6  9:14
 */

@RestController
@RequestMapping("/service02")
@Slf4j
public class CloudController {


    @GetMapping
    public String get(){
        return "hello this is service02";
    }

    @GetMapping("/feign")
    public String openFeign(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null){
            String header = requestAttributes.getRequest().getHeader("token");
            log.info("token: "+ header);
        }
        return "this is service02 openFeign";
    }
}
