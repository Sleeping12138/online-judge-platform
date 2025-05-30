package com.OjSystem.common.core.errorcode.modules.ojfriend;

import com.OjSystem.common.core.errorcode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum FriendControllerErrorCode implements ErrorCode {

    ;
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
