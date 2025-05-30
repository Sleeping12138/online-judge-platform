package com.OjSystem.system.manager;

import com.OjSystem.common.redis.Constants.RedisConstants;
import com.OjSystem.common.redis.service.RedisService;
import com.OjSystem.system.domain.user.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserCacheManager {

    @Autowired
    private RedisService redisService;

    /**
     * 刷新redis中的用户状态
     * @param userId
     * @param status
     */
    public void changeUserStatus(Long userId, Integer status) {
        String userKey = getUserKey(userId);
        User user = redisService.getCacheObject(userKey, User.class);
        if (user == null) {
            return;
        }

        user.setStatus(status);
        redisService.setCacheObject(userKey,user);
    }


    /**
     * 获取userKey
     * @param userId
     * @return
     */
    public String getUserKey(Long userId) {
        return RedisConstants.USER_DETAIL + userId;
    }


}
