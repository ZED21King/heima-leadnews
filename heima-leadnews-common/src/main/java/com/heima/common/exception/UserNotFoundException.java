package com.heima.common.exception;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/12 1:06
 */
public class UserNotFoundException extends BaseException{
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(int code, String message) {
        super(code, message);
    }
}
