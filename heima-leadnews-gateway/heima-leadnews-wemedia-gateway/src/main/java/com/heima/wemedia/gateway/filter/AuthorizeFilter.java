package com.heima.wemedia.gateway.filter;

import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.JwtUtils;
import com.heima.utils.common.Payload;
import com.heima.utils.common.RsaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.PublicKey;
import java.util.concurrent.TimeUnit;

/**
 * 统一鉴权过滤器
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    @Value("${leadnews.jwt.publicKeyPath}")
    private String publicKeyPath;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //针对登录请求进行放行
        System.out.println(request.getURI());
        String uri = request.getURI().getPath();  //  /user/api/v1/login/login_auth
        if (uri.contains("/login")) {
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst("token");
        if (token != null) {
            if (redisTemplate.opsForValue().get(token) != null) {
                redisTemplate.expire(token, 30, TimeUnit.MINUTES);
                request.mutate().header("userId", redisTemplate.opsForValue().get(token));
            }
            else{
                try {
                    PublicKey publicKey = RsaUtils.getPublicKey(this.publicKeyPath);
                    Payload<WmUser> payload = JwtUtils.getInfoFromToken(token, publicKey, WmUser.class);
                    WmUser wmUser = payload.getInfo();
                    request.mutate().header("userId", wmUser.getId().toString());
                } catch (Exception e) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }
            }
            return chain.filter(exchange);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}