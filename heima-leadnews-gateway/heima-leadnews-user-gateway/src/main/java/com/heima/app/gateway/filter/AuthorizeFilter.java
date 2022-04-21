package com.heima.app.gateway.filter;

import com.heima.model.user.pojos.ApUser;
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
import org.springframework.util.StringUtils;
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

        //1）获取请求头的token值
        String token = request.getHeaders().getFirst("token");

        //2）判断token是否为空，为空则拒绝访问
        if (!StringUtils.isEmpty(token)) {
            if (redisTemplate.opsForValue().get(token) != null) {
                redisTemplate.expire(token, 30, TimeUnit.MINUTES);
                request.mutate().header("userId", redisTemplate.opsForValue().get(token));
                return chain.filter(exchange);
            }

            else {
                try {
                    System.out.println(publicKeyPath);
                    PublicKey publicKey = RsaUtils.getPublicKey(publicKeyPath);
                    Payload<ApUser> payload = JwtUtils.getInfoFromToken(token, publicKey, ApUser.class);
                    ApUser user = payload.getInfo();
                    if (user != null) {
                        System.out.println(user);
                        Integer userId = user.getId();
                        //把userId存入请求头
                        request.mutate().header("userId", userId.toString());
                    } else {
                        request.mutate().header("userId", "4");
                    }
                    //6）放行请求
                    return chain.filter(exchange);
                } catch (Exception e) {
                    return this.responseUnauthorized(response);
                }
            }
        }
        return responseUnauthorized(response);
    }

    private Mono<Void> responseUnauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}