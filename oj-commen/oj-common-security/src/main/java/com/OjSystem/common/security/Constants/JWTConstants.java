package com.OjSystem.common.security.Constants;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JWTConstants {
    public static final String SECRET = "iWbPd8jWrfctNjiAdwnjs+/h1LI/b4qJTRU2Lc8Y5+Y=";

    // 生成的SECRET_KEY
    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
}
