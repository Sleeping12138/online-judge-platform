package com.OjSystem.manager;

import com.OjSystem.common.redis.Constants.RedisConstants;
import com.OjSystem.common.redis.service.RedisService;
import com.OjSystem.friend.domain.user.User;
import com.OjSystem.friend.domain.user.VO.UserVO;
import com.OjSystem.friend.mapper.user.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserCacheManager {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据userId获取用户信息
     * @param userId
     * @return
     */
    public UserVO getUserById(Long userId) {
        String userKey = getUserKey(userId);
        UserVO userVO = redisService.getCacheObject(userKey, UserVO.class);
        if (userVO != null) {
            return userVO;
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(
                User::getUserId,
                User::getNickName,
                User::getHeadImage,
                User::getSex,
                User::getEmail,
                User::getPhone,
                User::getWechat,
                User::getIntroduce,
                User::getSchoolName,
                User::getMajorName,
                User::getStatus).eq(User::getUserId,userId);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return null;
        }

        refreshUser(user);

        UserVO newUserVO = new UserVO();
        BeanUtils.copyProperties(user,newUserVO);
        return newUserVO;
    }


    /**
     * 更新缓存
     * @param user
     */
    public void refreshUser(User user) {
        String userKey = getUserKey(user.getUserId());
        redisService.setCacheObject(userKey, user);
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
