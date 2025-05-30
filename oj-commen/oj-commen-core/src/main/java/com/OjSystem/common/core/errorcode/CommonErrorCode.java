package com.OjSystem.common.core.errorcode;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode{
    SUCCESS(2000, "成功"),
    PARAM_IS_ERROR(3000,"参数校验失败"),
    TOKEN_HAS_TIME_OUT(3001,"token已经过期"),
    INTERNAL_SERVER_ERROR(5000, "系统异常"),
    UNKNOWN(9999, "未知错误");

    private Integer code;
    private String errorMsg;

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getErrMsg() {
        return this.errorMsg;
    }
}
