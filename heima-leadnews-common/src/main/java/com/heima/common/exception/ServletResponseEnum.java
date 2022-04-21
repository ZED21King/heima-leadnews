package com.heima.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/11 23:06
 */
@Getter
@AllArgsConstructor
public enum ServletResponseEnum implements BaseErrorInfoInterface {
    ;
    /**
     * 返回码
     */
    private final int code;
    /**
     * 返回消息
     */
    private final String message;

    @Override
    public int getCode() {
        return code;
    }
}
