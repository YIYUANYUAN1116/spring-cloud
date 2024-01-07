package com.xht;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author : YIYUANYUAN
 * @description :
 * @date: 2024/1/6  9:11
 */
@SpringBootApplication
@EnableFeignClients
public class AppService02Application {
    public static void main(String[] args) {
        SpringApplication.run(AppService02Application.class,args);
    }
}
