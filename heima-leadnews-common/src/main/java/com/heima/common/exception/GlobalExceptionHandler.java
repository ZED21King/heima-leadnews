package com.heima.common.exception;

import com.heima.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理
 */
@Slf4j
//@RestControllerAdvice//进行异常拦截  @RestControllerAdvice = @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     */
    @ExceptionHandler(LeadNewsException.class)  // 捕获异常
    public ResponseResult handleLeadNewsException(BaseException e){
        log.error(e.getMessage(), e);
        return ResponseResult.errorResult(e.getCode(),e.getMessage());
    }

    /**
     * 捕获系统异常
     */
    @ExceptionHandler(Exception.class)  // 捕获异常
    public ResponseResult handleException(Exception e){
        log.error(e.getMessage(), e);
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR.getCode(),e.getMessage());
    }

}