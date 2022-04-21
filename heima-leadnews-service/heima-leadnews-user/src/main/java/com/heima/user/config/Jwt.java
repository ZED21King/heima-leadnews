package com.heima.user.config;

import lombok.Data;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/9 22:04
 */
@Data
public class Jwt {
    private String privateKeyPath;
    private Integer expire;
}
