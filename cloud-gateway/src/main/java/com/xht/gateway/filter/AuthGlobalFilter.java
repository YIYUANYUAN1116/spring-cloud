package com.xht.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * @author : YIYUANYUAN
 * @date: 2024/1/6  11:14
 */

@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    ObjectMapper objectMapper;

    // 放行路径，可以编写配置类，配置在YML中
//    private static final String[] SKIP_PATH = {"/api/service01/login", "/api/service02"};

    private static final String[] SKIP_PATH = {"/service01/service01/login", "/service02/service02"};
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求路径
        String requestPath  = exchange.getRequest().getPath().pathWithinApplication().value();
        boolean b = Arrays.stream(SKIP_PATH).map(path -> path.replaceAll("/\\*\\*", ""))
                .anyMatch(path -> path.equalsIgnoreCase(requestPath));
        if (b){ //匹配放行路径就放行
            return chain.filter(exchange);
        }

        // 2. 判断是否包含Oauth2 令牌
        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (!StringUtils.hasLength(authorization)){
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            String result = "";
            try {
                Map<String, Object> map = new HashMap<>(16);
                map.put("code", HttpStatus.UNAUTHORIZED.value());
                map.put("msg", "当前请求未认证，不允许访问");
                map.put("data", null);
                result = objectMapper.writeValueAsString(map);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            DataBuffer buffer = response.bufferFactory().wrap(result.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Flux.just(buffer));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
