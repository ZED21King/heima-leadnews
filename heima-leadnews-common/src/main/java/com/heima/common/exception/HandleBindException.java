package com.heima.common.exception;

import com.heima.common.dtos.BindindResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/11 23:24
 * 捕获参数绑定校验异常
 */
@Slf4j
//@RestControllerAdvice
public class HandleBindException {

//    @ExceptionHandler(value = BindException.class)
    public ErrorResponse handleBindException(BaseException e) {
        log.error("参数绑定校验异常");
        return wrapperBindingResult(e.getBindingResult());
    }

    private ErrorResponse wrapperBindingResult(BindindResult bindingResult) {
        StringBuilder msg = new StringBuilder();

        for (ObjectError error : bindingResult.getAllErrors()) {
            msg.append(", ");
            if (error instanceof FieldError) {
                msg.append(((FieldError) error).getField()).append(":");
            }
            msg.append(error.getDefaultMessage() == null ? "" : error.getDefaultMessage());

        }

        return null;
    }
}
