package com.OjSystem.common.core.result;

import com.OjSystem.common.core.errorcode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> CommonResult<T> success(T data){
        CommonResult<T> commonResult = new CommonResult<>();
        commonResult.setCode(200);
        commonResult.setMsg(null);
        commonResult.setData(data);
        return commonResult;
    }

    public static <T> CommonResult<T> fail(Integer code,String errorMsg) {
        CommonResult<T> commonResult = new CommonResult<>();
        commonResult.setCode(code);
        commonResult.setMsg(errorMsg);
        commonResult.setData(null);
        return commonResult;
    }

    public static <T> CommonResult<T> fail(ErrorCode errorCode){
        CommonResult<T> commonResult = new CommonResult<>();
        commonResult.setCode(errorCode.getCode());
        commonResult.setMsg(errorCode.getErrMsg());
        commonResult.setData(null);
        return commonResult;
    }

}
