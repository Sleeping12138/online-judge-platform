package com.OjSystem.system.manager;

import com.OjSystem.common.core.errorcode.modules.ojfriend.FriendServiceErrorCode;
import com.OjSystem.common.core.exception.friend.FriendServiceException;
import com.OjSystem.common.redis.Constants.RedisConstants;
import com.OjSystem.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionCacheManager {

    @Autowired
    private RedisService redisService;

    /**
     * 向redis链中添加数据
     * @param questionId
     */
    public void addElementToList(Long questionId){
        redisService.leftPushForList(RedisConstants.QUESTION_LIST,questionId);
    }


    /**
     * 删除redis链中的指定id数据
     * @param questionId
     */
    public void removeElementFromList(Long questionId) {
        redisService.removeForList(RedisConstants.QUESTION_LIST,questionId);
    }
}
