package com.xht.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : YIYUANYUAN
 * @description :
 * @date: 2024/1/6  9:14
 */

@RestController
@RequestMapping("/service01")
public class CloudController {


    @GetMapping
    public String get(){
        return "hello this is service01";
    }

    @GetMapping("/login")
    public String login(){
        return "service01 login";
    }


    @GetMapping("/needLogin")
    public String needLogin(){
        return "service01 needLogin";
    }
}
