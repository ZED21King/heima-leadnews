package com.heima.user.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 扫描全局异常处理类
 */
@Configuration
@ComponentScan("com.heima.common.exception")
public class ExceptionConfig {
}