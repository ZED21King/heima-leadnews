package com.heima.common.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO客户端初始配置类
 */
@Configuration(
    proxyBeanMethods = false
)
@EnableConfigurationProperties({MiniOProperties.class}) // 开启属性配置
public class MiniOAutoConfiguration {
    @Autowired
    private MiniOProperties miniOProperties;

    @Bean
    @ConditionalOnMissingBean(
            name = {"minioClient"}
    )
    public MinioClient createMinioClient(){
        return MinioClient.builder()
                .credentials(miniOProperties.getAccessKey(), miniOProperties.getSecretKey())
                .endpoint(miniOProperties.getEndpoint()).build();
    }
}