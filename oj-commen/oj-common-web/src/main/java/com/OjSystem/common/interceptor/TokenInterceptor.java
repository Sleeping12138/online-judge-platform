package com.OjSystem.common.interceptor;

import com.OjSystem.common.core.constants.Constants;
import com.OjSystem.common.core.utils.ThreadLocalUtil;
import com.OjSystem.common.security.service.TokenService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        if (token == null) {
            return true;
        }
        Long userId = tokenService.getUserId(token);
        String userKey = tokenService.getUserKey(token);
        ThreadLocalUtil.set(Constants.USER_ID,userId);
        ThreadLocalUtil.set(Constants.USER_KEY,userKey);
        tokenService.extendToken(token);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 防止数据泄漏
        ThreadLocalUtil.remove();
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StringUtils.isNotEmpty(token) && token.startsWith("Bearer ")) {
            token = token.replaceFirst("Bearer ", "");
        }
        return token;
    }
}
