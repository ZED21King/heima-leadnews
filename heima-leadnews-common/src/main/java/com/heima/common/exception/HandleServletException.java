package com.heima.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/11 22:57
 */
@Slf4j
//@RestControllerAdvice
//@Component
public class HandleServletException {

    private static final String ENV_PROD = "ENV_PROD";

    //@Value("$spring.profiles.active")
    private String profile;

    @Autowired
    private Environment environment;

    public HandleServletException() {
        this.profile = environment.getActiveProfiles()[0];
    }

    public ErrorResponse handleServletException(Exception e) {
        log.error(e.getMessage(), e);
        int code = CommonResponseEnum.SERVER_ERROR.getCode();
        try {
            ServletResponseEnum servletResponseEnum = ServletResponseEnum.valueOf(e.getClass().getSimpleName());
            code = servletResponseEnum.getCode();
        } catch (IllegalStateException e1) {
            log.error("class [{}] not defined in enum {}", e.getClass().getName(), ServletResponseEnum.class.getName());
        }
        if (ENV_PROD.equals(profile)) {
            // 因为生产环境, 不适合把具体的异常信息展示给用户, 比如404.
            code = CommonResponseEnum.SERVER_ERROR.getCode();
            BaseException baseException = new BaseException(CommonResponseEnum.SERVER_ERROR);
            String message = this.getMessage(baseException);
            return new ErrorResponse(code, message);
        }

        return new ErrorResponse(code, e.getMessage());
    }

    private String getMessage(BaseException baseException) {
        return baseException.getMessage();
    }
}
