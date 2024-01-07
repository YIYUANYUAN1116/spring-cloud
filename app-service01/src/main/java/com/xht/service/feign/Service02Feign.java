package com.xht.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author : YIYUANYUAN
 * @date: 2024/1/7  13:28
 */

@Component
@FeignClient("service02")
public interface Service02Feign {

    @GetMapping("/service02/feign")
    String openFeign();
}
