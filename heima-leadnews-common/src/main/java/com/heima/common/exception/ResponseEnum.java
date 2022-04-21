package com.heima.common.exception;

import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.dtos.WmLoginDto;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.BCrypt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum ResponseEnum implements BusinessExceptionAssert {
    /**
     * Bad licence type
     */
    BAD_LICENCE_TYPE(7001, "Bad licence type."),
    /**
     * Licence not found
     */
    LICENCE_NOT_FOUND(7002, "Licence not found."),
    LOGIN_USER_NOT_FOUND(20, "user not found.") {
        @Override
        public void assertNotNull(Object obj1, Object obj2) {
            super.assertNotNull(obj1, obj2);
            LOGIN_PASSWORD_ERROR.assertNotNull(obj1, obj2);
        }
    },
    // 成功段0
    SUCCESS(200, "操作成功"),
    // 登录段1~50
    NEED_LOGIN(1, "需要登录后操作"),
    LOGIN_PASSWORD_ERROR(2, "密码错误") {
        @Override
        public void assertNotNull(Object obj1, Object obj2) {
            if (obj1 instanceof LoginDto && obj2 instanceof ApUser) {
                LoginDto loginDto = (LoginDto) obj1;
                ApUser loginUser = (ApUser) obj2;
                if (BCrypt.checkpw(loginDto.getPassword(), loginUser.getPassword())) {
                    return;
                }
            } else if (obj1 instanceof WmLoginDto && obj2 instanceof WmUser) {
                WmLoginDto loginDto = (WmLoginDto) obj1;
                WmUser loginUser = (WmUser) obj2;
                if (BCrypt.checkpw(loginDto.getPassword(), loginUser.getPassword())) {
                    return;
                }
            }
            throw new LeadNewsException(this.getCode(), this.getMessage());
        }
    },
    // TOKEN50~100
    TOKEN_INVALID(50, "无效的TOKEN"),
    TOKEN_EXPIRE(51, "TOKEN已过期"),
    TOKEN_REQUIRE(52, "TOKEN是必须的"),
    // SIGN验签 100~120
    SIGN_INVALID(100, "无效的SIGN"),
    SIG_TIMEOUT(101, "SIGN已过期"),
    // 参数错误 500~1000
    PARAM_REQUIRE(500, "缺少参数"),
    PARAM_INVALID(501, "无效参数"),
    PARAM_IMAGE_FORMAT_ERROR(502, "图片格式有误"),
    SERVER_ERROR(503, "服务器内部错误"),
    // 数据错误 1000~2000
    DATA_EXIST(1000, "数据已经存在"),
    AP_USER_DATA_NOT_EXIST(1001, "ApUser数据不存在"),
    DATA_NOT_EXIST(1002, "数据不存在"),
    // 数据错误 3000~3500
    NO_OPERATOR_AUTH(3000, "无权限操作"),
    NEED_ADMIND(3001, "需要管理员权限"),
    OBJECT_NONNUL(999, "对象不能为空");

    @Override
    public String toString() {
        return "ResponseEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    /**
     * 返回码
     */
    private final int code;
    /**
     * 返回消息
     */
    private final String message;
}