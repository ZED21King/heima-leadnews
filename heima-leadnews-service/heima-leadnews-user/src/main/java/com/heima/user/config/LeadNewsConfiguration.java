package com.heima.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/9 22:03
 */

@Configuration
public class LeadNewsConfiguration {

    @Bean
    @ConfigurationProperties("leadnews.jwt")
    public Jwt jwt() {
        return new Jwt();
    }
}
