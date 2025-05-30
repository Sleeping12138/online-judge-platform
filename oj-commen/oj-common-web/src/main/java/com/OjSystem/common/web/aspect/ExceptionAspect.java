package com.OjSystem.common.web.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.OjSystem.common.core.errorcode.CommonErrorCode;
import com.OjSystem.common.core.errorcode.modules.ojsystem.SystemServiceErrorCode;
import com.OjSystem.common.core.exception.friend.FriendControllerException;
import com.OjSystem.common.core.exception.friend.FriendServiceException;
import com.OjSystem.common.core.exception.sysuser.SystemControllerException;
import com.OjSystem.common.core.exception.sysuser.SystemServiceException;
import com.OjSystem.common.core.result.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class ExceptionAspect {
    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonResult<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                               HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return CommonResult.fail(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 拦截运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public CommonResult<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生运行时异常.", requestURI, e);
        return CommonResult.fail(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public CommonResult<?> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生异常.", requestURI, e);
        return CommonResult.fail(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 参数校验失败异常
     *
     * @param methodArgumentNotValidException
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> handler(MethodArgumentNotValidException methodArgumentNotValidException) {
        log.error("参数传递有误，" + methodArgumentNotValidException.getMessage());
        return CommonResult.fail(CommonErrorCode.PARAM_IS_ERROR.getCode(), CommonErrorCode.PARAM_IS_ERROR.getErrMsg());
    }

    @ExceptionHandler(BindException.class)
    public CommonResult<Void> handleBindException(BindException e) {
        log.error(e.getMessage());
        String message = join(e.getAllErrors(), DefaultMessageSourceResolvable::getDefaultMessage, ", ");
        return CommonResult.fail(CommonErrorCode.PARAM_IS_ERROR.getCode(), message);
    }

    private <E> String join(Collection<E> collection, Function<E, String>
            function, CharSequence delimiter) {
        if (CollUtil.isEmpty(collection)) {
            return StrUtil.EMPTY;
        }
        return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.joining(delimiter));
    }


    /**
     * System模块异常
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(SystemServiceException.class)
    public CommonResult<?> handler(SystemServiceException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生运行时异常.", requestURI, e);
        return CommonResult.fail(e.getCode(), e.getErrMsg());
    }

    @ExceptionHandler(SystemControllerException.class)
    public CommonResult<?> handler(SystemControllerException e,HttpServletRequest request){
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生运行时异常.", requestURI, e);
        return CommonResult.fail(e.getCode(), e.getErrMsg());
    }


    @ExceptionHandler(FriendControllerException.class)
    public CommonResult<?> handler(FriendControllerException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生运行时异常.", requestURI, e);
        return CommonResult.fail(e.getCode(), e.getErrMsg());
    }

    @ExceptionHandler(FriendServiceException.class)
    public CommonResult<?> handler(FriendServiceException e,HttpServletRequest request){
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生运行时异常.", requestURI, e);
        return CommonResult.fail(e.getCode(), e.getErrMsg());
    }
}