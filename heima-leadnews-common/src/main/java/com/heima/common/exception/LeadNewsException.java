package com.heima.common.exception;

import lombok.Getter;

/**
 * 自定义业务异常类
 */
@Getter
public class LeadNewsException extends BaseException{

    /**
     * 业务状态码
     */
    private final int code;
    private final String message;

    public LeadNewsException(int code,String message){
        super(code, message);
        this.code = code;
        this.message = message;
    }

    public LeadNewsException(AppHttpCodeEnum appHttpCodeEnum){
        this(appHttpCodeEnum.getCode(), appHttpCodeEnum.getMessage());
    }
}