package com.heima.common.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/11 23:16
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    /**
     * 返回码
     */
    public int code;
    /**
     * 返回消息
     */
    public String message;
}
