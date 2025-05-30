package com.OjSystem.common.core.exception.friend;

import com.OjSystem.common.core.errorcode.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FriendControllerException extends RuntimeException{
    private Integer code;
    private String errMsg;

    public FriendControllerException() {
    }

    public FriendControllerException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.errMsg = errorCode.getErrMsg();
    }
}
