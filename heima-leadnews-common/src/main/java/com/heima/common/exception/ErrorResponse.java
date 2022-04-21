package com.heima.common.exception;

import com.heima.common.dtos.BaseResponse;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/11 23:15
 */
public class ErrorResponse extends BaseResponse {

    public ErrorResponse(int code, String message) {
        super(code, message);
    }
}
