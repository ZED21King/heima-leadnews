package com.heima.wemedia;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/17 11:02
 */

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 自媒体微服务
 */
@SpringBootApplication
@MapperScan("com.heima.wemedia.mapper")
@EnableDiscoveryClient
@EnableFeignClients("com.heima.*.feign")
@EnableAsync
public class WemediaApplication {
    public static void main(String[] args) {
        SpringApplication.run(WemediaApplication.class,args);
    }

    /**
     * MyBatis分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
