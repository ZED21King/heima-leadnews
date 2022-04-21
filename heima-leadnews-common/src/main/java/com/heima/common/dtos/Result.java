package com.heima.common.dtos;



import java.io.Serializable;

public class Result<T> implements Serializable {
    private String message;
    private Integer code;
    private T data;

    //成功 并不返回数据
    public static <T> Result<T> ok() {
        return new Result<T>(StatusCode.SUCCESS.message(), StatusCode.SUCCESS.code(), null);
    }

    //成功 并返回数据
    public static <T> Result<T> ok(T data) {
        return new Result<T>(StatusCode.SUCCESS.message(), StatusCode.SUCCESS.code(), data);
    }

    //系统错误 不返回数据
    public static <T> Result<T> error() {
        return new Result<T>(StatusCode.FAILURE.message(), StatusCode.FAILURE.code(), null);
    }

    //系统错误 并返回逻辑数据
    public static <T> Result<T> error(T data) {
        return new Result<T>(StatusCode.FAILURE.message(), StatusCode.FAILURE.code(), data);
    }

    //错误并返回指定错误信息和状态码以及逻辑数据
    public static <T> Result<T> error(StatusCode statusCode, T data) {
        return new Result<T>(statusCode.message(), statusCode.code(), data);
    }

    // 错误并返回指定错误信息和状态码 不返回数据
    public static <T> Result<T> error(StatusCode statusCode) {
        return new Result<T>(statusCode.message(), statusCode.code(), null);
    }

    //自定义错误和状态返回
    public static <T> Result<T> errorMessage(String message, Integer code, T data) {
        return new Result<T>(message, code, data);
    }
    //自定义错误信息 状态码固定
    public static <T> Result<T> errorMessage(String message) {
        return new Result<T>(message, StatusCode.CUSTOM_FAILURE.code(), null);
    }

    public Result() {

    }

    public Result(String message, Integer code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
