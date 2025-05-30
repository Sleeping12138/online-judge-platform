package com.OjSystem.common.core.exception.sysuser;

import com.OjSystem.common.core.errorcode.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemControllerException extends RuntimeException {
    private Integer code;
    private String errMsg;

    public SystemControllerException() {
    }

    public SystemControllerException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.errMsg = errorCode.getErrMsg();
    }
}
