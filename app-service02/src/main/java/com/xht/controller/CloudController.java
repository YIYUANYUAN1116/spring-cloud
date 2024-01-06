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
@RequestMapping("/service02")
public class CloudController {


    @GetMapping
    public String get(){
        return "hello this is service02";
    }
}
