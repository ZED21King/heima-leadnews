package com.heima.wemedia.config;

import com.heima.utils.common.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/17 20:01
 */
@Configuration
public class WmTokenInterceptor implements WebMvcConfigurer {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

                System.out.println(request.getRequestURI());

                String userId = "userId";
                String value = redisTemplate.opsForValue().get(userId);

                if (value == null) {
                    value = request.getHeader(userId);
                }
                if (value != null) {
//                    threadLocal.set(Integer.valueOf(value));
                    ThreadLocalUtils.set(Integer.valueOf(value));
                    return true;
                }

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
        }).excludePathPatterns("/login/in");
    }
}
