package com.OjSystem.common.core.exception.friend;

import com.OjSystem.common.core.errorcode.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FriendServiceException extends RuntimeException{
    private Integer code;
    private String errMsg;

    public FriendServiceException() {
    }

    public FriendServiceException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.errMsg = errorCode.getErrMsg();
    }
}
