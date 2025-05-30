package com.OjSystem.manager;

import com.OjSystem.common.core.errorcode.modules.ojfriend.FriendServiceErrorCode;
import com.OjSystem.common.core.exception.friend.FriendServiceException;
import com.OjSystem.common.redis.Constants.RedisConstants;
import com.OjSystem.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class QuestionCacheManager {

    @Autowired
    private RedisService redisService;



    /**
     * 获取列表长度
     *
     * @return
     */
    public Long getListSize() {
        return redisService.getListSize(RedisConstants.QUESTION_LIST);
    }


    /**
     * 获取前一题的questionId
     *
     * @param questionId
     * @return
     */
    public Long preQuestion(Long questionId) {
        Long index = redisService.indexOf(RedisConstants.QUESTION_LIST, questionId);
        System.out.println(index);
        if (index == 0) {
            throw new FriendServiceException(FriendServiceErrorCode.THIS_IS_FIRST_QUESTION);
        }

        return redisService.index(RedisConstants.QUESTION_LIST, index - 1, Long.class);
    }


    /**
     * 获取后一题的questionId
     *
     * @param questionId
     * @return
     */
    public Object nextQuestion(Long questionId) {
        Long index = redisService.indexOf(RedisConstants.QUESTION_LIST, questionId);
        System.out.println(index);
        Long listSize = getListSize();
        if (index == listSize - 1) {
            throw new FriendServiceException(FriendServiceErrorCode.THIS_IS_LAST_QUESTION);
        }

        return redisService.index(RedisConstants.QUESTION_LIST, index + 1, Long.class);
    }

}
