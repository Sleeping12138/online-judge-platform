package com.OjSystem.common.security.utils;

import com.OjSystem.common.redis.Constants.RedisConstants;
import com.OjSystem.common.security.Constants.JWTConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtil {

    /**
     * 生成密钥
     * @param claim {"id": 12, "name":"张山"}
     * @return
     */
    public static String genJwt(Map<String, Object> claim) {
        //签名算法
        String jwt = Jwts.builder()
                .setClaims(claim)             // 自定义内容(载荷)
                .setIssuedAt(new Date())      // 设置签发时间
                .signWith(JWTConstants.SECRET_KEY)         // 签名算法
                .compact();
        return jwt;
    }

    /**
     * 验证密钥
     */
    public static Claims parseJWT(String jwt) {
        if (!StringUtils.hasLength(jwt)) {
            return null;
        }
        // 创建解析器, 设置签名密钥
        JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder().setSigningKey(JWTConstants.SECRET_KEY);
        Claims claims = null;
        try {
            //解析token
            claims = jwtParserBuilder.build().parseClaimsJws(jwt).getBody();
        } catch (Exception e) {
            // 签名验证失败
            log.error("解析令牌错误,jwt:{}", jwt, e);
        }
        return claims;

    }

    public static String getUserKey(Claims claims) {
        Object value = claims.get(RedisConstants.LOGIN_USER_KEY);
        return getString(value);
    }

    public static String getUserId(Claims claims) {
        Object value = claims.get(RedisConstants.LOGIN_USER_ID);
        return getString(value);
    }

    private static String getString(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
