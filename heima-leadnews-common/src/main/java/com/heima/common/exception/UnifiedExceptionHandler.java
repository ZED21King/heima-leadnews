package com.heima.common.exception;

import com.heima.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/12 1:35
 * 统一异常处理
 */
@Slf4j
@RestControllerAdvice//进行异常拦截  @RestControllerAdvice = @ControllerAdvice + @ResponseBody
public class UnifiedExceptionHandler {
    /**
     * 捕获业务异常
     */
    @ExceptionHandler(LeadNewsException.class)  // 捕获异常
    public ResponseResult leadNewsException(BaseException e){
        log.error("发生业务异常！原因是：{}", e.getMessage());
        return ResponseResult.errorResult(e.getCode(), e.getMessage());
    }

    /**
     * 处理其他异常: 捕获系统异常
     */
    @ExceptionHandler(Exception.class)  // 捕获异常
    public ResponseResult exceptionHandler(Exception e){
        log.error("未知原因！原因是", e);
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR.getCode(), e.getMessage());
    }
}
