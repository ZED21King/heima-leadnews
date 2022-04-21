package com.heima.app.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//可能会报如下错误, 排除掉DataSourceAutoConfiguration即可;
//Consider the following:
//If you want an embedded database (H2, HSQL or Derby), please put it on the classpath.
//If you have database settings to be loaded from a particular profile you may need to activate it (no profiles are currently active).
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
public class AppGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppGatewayApplication.class,args);
    }
}