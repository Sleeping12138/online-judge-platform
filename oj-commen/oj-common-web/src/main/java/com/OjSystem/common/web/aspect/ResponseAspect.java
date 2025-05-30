package com.OjSystem.common.web.aspect;

import com.OjSystem.common.core.result.CommonResult;
import com.OjSystem.common.web.utils.JacksonUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ResponseAspect implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof CommonResult<?>) {
            return body;
        } else if (body instanceof String) {
            return JacksonUtil.writeValueAsString(CommonResult.success(body));
        } else {
            return CommonResult.success(body);
        }
    }
}
