package com.heima.common.exception;

import com.heima.common.dtos.BindindResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/11 22:08
 */
@Getter
@Slf4j
public class BaseException extends RuntimeException {

    BaseErrorInfoInterface baseErrorInfoInterface;
    Object[] args;
    int code;
    String message;

    private static final long serialVersionUID = 1L;

    public BaseException() {
        super();
    }

    public BaseException(BaseErrorInfoInterface responseEnum) {
        this(responseEnum.getCode(), responseEnum.getMessage());
        this.baseErrorInfoInterface = responseEnum;
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        log.debug("BaseException");
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
//
//    public BaseException(IResponseEnum responseEnum, Object[] args, String message) {
//        this(responseEnum, args, message, new RuntimeException());
//    }
//
//    public BaseException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
//        this(message, cause);
//        this.responseEnum = responseEnum;
//        this.args = args;
//    }
//
    public BindindResult getBindingResult() {
        return null;
    }
}
