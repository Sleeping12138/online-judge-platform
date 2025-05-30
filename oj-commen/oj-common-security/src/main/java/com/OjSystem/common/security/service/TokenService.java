package com.OjSystem.common.security.service;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.OjSystem.common.core.constants.HttpConstants;
import com.OjSystem.common.redis.Constants.RedisConstants;
import com.OjSystem.common.redis.service.RedisService;
import com.OjSystem.common.security.Constants.JWTConstants;
import com.OjSystem.common.security.domain.LoginUser;
import com.OjSystem.common.security.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    @Autowired
    private RedisService redisService;


    /**
     * 获取token
     *
     * @param userId
     * @param identity
     * @return
     */
    public String createToken(Long userId, Integer identity, String nickName, String headImage) {
        String userKey = UUID.fastUUID().toString();

        Map<String, Object> claim = new HashMap<>();
        claim.put(RedisConstants.LOGIN_USER_ID, userId);
        claim.put(RedisConstants.LOGIN_USER_KEY, userKey);
        String token = JwtUtil.genJwt(claim);

        // 将数据缓存到redis中
        LoginUser loginUser = new LoginUser();
        loginUser.setNickName(nickName);
        loginUser.setIdentity(identity);
        loginUser.setHeadImage(headImage);
        redisService.setCacheObject(RedisConstants.KEY_PREFIX + userKey, loginUser, RedisConstants.KEY_TIME_OUT, TimeUnit.MINUTES);

        return token;
    }

    /**
     * 延长token时间
     *
     * @param token
     */
    public void extendToken(String token) {
        // 获取LOGIN_USER_KEY + userKey
        String tokenKey = getTokenKey(token);
        if (tokenKey == null) {
            return;
        }

        Long expire = redisService.getExpire(tokenKey, TimeUnit.MINUTES);
        if (expire != null && expire < RedisConstants.REMAIN_TIME) {
            redisService.expire(tokenKey, RedisConstants.KEY_TIME_OUT, TimeUnit.MINUTES);
        }
    }

    /**
     * 从redis中获取指定token中存储的对象
     *
     * @param token
     * @return
     */
    public LoginUser getLoginUserInfoFromToken(String token) {
        // 获取LOGIN_USER_KEY + userKey
        String tokenKey = getTokenKey(token);

        if (tokenKey == null) return null;
        return redisService.getCacheObject(tokenKey, LoginUser.class);
    }

    /**
     * 获取LOGIN_USER_KEY + userKey
     *
     * @param token
     * @return
     */
    private static String getTokenKey(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        Claims claims;
        try {
            claims = JwtUtil.parseJWT(token);
            if (claims == null) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        String userKey = JwtUtil.getUserKey(claims);
        if (userKey == null) {
            return null;
        }
        return RedisConstants.KEY_PREFIX + userKey;
    }

    /**
     * 删除登录者的token
     *
     * @param token
     * @return
     */
    public boolean deleteLoginUserToken(String token) {
        // 获取LOGIN_USER_KEY + userKey
        String tokenKey = getTokenKey(token);

        if (tokenKey == null) return false;
        return redisService.deleteObject(tokenKey);
    }


    /**
     * 从token中获取userId
     *
     * @param token
     * @return
     */
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return Long.valueOf(JwtUtil.getUserId(claims));
    }


    /**
     * 从token中获取userKey
     *
     * @param token
     * @return
     */
    public String getUserKey(String token) {
        Claims claims = getClaims(token);
        return JwtUtil.getUserKey(claims);
    }


    /**
     * 根据token获取claims
     *
     * @param token
     * @return
     */
    public Claims getClaims(String token) {
        Claims claims;
        try {
            claims = JwtUtil.parseJWT(token);
            if (claims == null) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return claims;
    }


    /**
     * 更新loginUser缓存
     * @param nickName
     * @param headImage
     * @param userKey
     */
    public void refreshLoginUser(String nickName, String headImage, String userKey) {
        LoginUser loginUser = redisService.getCacheObject(RedisConstants.KEY_PREFIX + userKey, LoginUser.class);
        loginUser.setHeadImage(headImage);
        loginUser.setNickName(nickName);
        redisService.setCacheObject(RedisConstants.KEY_PREFIX + userKey, loginUser);
    }
}
