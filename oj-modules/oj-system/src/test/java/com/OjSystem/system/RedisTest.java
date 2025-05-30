package com.OjSystem.system;

import com.OjSystem.common.redis.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisService redisService;

    @Test
    public void test(){
        redisService.setCacheObject("test","hello world");
    }
}
