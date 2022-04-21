package com.heima.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/11 23:02
 */
@Getter
@AllArgsConstructor
public enum CommonResponseEnum implements BaseErrorInfoInterface {

    SERVER_ERROR(404, "页面不存在");
    /**
     * 返回码
     */
    private final int code;
    /**
     * 返回消息
     */
    private final String message;

}
