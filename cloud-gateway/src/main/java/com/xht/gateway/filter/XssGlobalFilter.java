package com.xht.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

/**
 * @author : YIYUANYUAN
 * @date: 2024/1/6  11:32
 * @describe: 防范XSS 攻击
 */
@Slf4j
@Component
public class XssGlobalFilter implements GlobalFilter, Ordered {
    @Autowired
    ObjectMapper objectMapper;

    private final static Pattern[] scriptPatterns = {
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();
        URI uri = request.getURI();
        if (method == HttpMethod.GET && uri.getRawQuery()!=null){
            String paramsString = uri.getRawQuery().replaceAll("[\u0000\n\r]", "");
            if (!StringUtils.hasLength(paramsString)){
                return chain.filter(exchange);
            }

            String lowerValue = URLDecoder.decode(paramsString, StandardCharsets.UTF_8).toLowerCase();
            for (Pattern pattern : scriptPatterns) {
                if (pattern.matcher(lowerValue).find()) {
                    return xssResponse(exchange);
                }
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public Mono<Void> xssResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String result = "";
        try {
            Map<String, Object> map = new HashMap<>(16);
            map.put("code", HttpStatus.BAD_REQUEST.value());
            map.put("msg", "当前请求可能存在恶意脚本，拒绝访问");
            result = objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        DataBuffer buffer = response.bufferFactory().wrap(result.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Flux.just(buffer));
    }
}
