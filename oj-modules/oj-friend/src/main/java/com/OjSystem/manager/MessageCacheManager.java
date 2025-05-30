package com.OjSystem.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.OjSystem.common.core.domain.PageParam;
import com.OjSystem.common.redis.Constants.RedisConstants;
import com.OjSystem.common.redis.service.RedisService;
import com.OjSystem.friend.domain.message.MessageVO;
import com.OjSystem.friend.mapper.message.MessageMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageCacheManager {

    @Autowired
    private RedisService redisService;

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 获取指定用户消息列表长度
     *
     * @param userId
     * @return
     */
    public Long getListSize(Long userId) {
        String userMsgListKey = getUserMsgListKey(userId);
        return redisService.getListSize(userMsgListKey);
    }


    /**
     * 获取用户消息key
     *
     * @param userId
     * @return
     */
    private String getUserMsgListKey(Long userId) {
        return RedisConstants.USER_MESSAGE_LIST + userId;
    }


    /**
     * 获取消息key
     *
     * @param textId
     * @return
     */
    private String getMsgDetailKey(Long textId) {
        return RedisConstants.MESSAGE_DETAIL + textId;
    }


    /**
     * 刷新消息缓存和用户消息缓存
     *
     * @param userId
     */
    public void refreshCache(Long userId) {
        List<MessageVO> messageTextVOList = messageMapper.selectUserMsgList(userId);
        if (CollectionUtil.isEmpty(messageTextVOList)) {
            return;
        }

        // 刷新用户消息缓存
        List<Long> textIdList = messageTextVOList.stream().map(MessageVO::getTextId).toList();
        String userMsgListKey = getUserMsgListKey(userId);
        redisService.rightPushAll(userMsgListKey, textIdList);

        // 刷新消息缓存
        Map<String, MessageVO> messageTextVOMap = new HashMap<>();
        for (MessageVO messageTextVO : messageTextVOList) {
            messageTextVOMap.put(getMsgDetailKey(messageTextVO.getTextId()), messageTextVO);
        }

        redisService.multiSet(messageTextVOMap);
    }


    /**
     * 从redis链上获取数据
     *
     * @param pageParam
     * @param userId
     * @return
     */
    public List<MessageVO> getMsgTextVOList(PageParam pageParam, Long userId) {
        // 计算开始结束位置
        int start = (pageParam.getPageNum() - 1) * pageParam.getPageSize();
        int end = start + pageParam.getPageSize() - 1; //下标需要 -1

        String userMsgListKey = getUserMsgListKey(userId);
        List<Long> msgTextIdList = redisService.getCacheListByRange(userMsgListKey, start, end, Long.class);

        // 根据id获取对应数据
        List<MessageVO> messageTextVOList = assembleMsgTextVOList(msgTextIdList);

        if (CollectionUtil.isEmpty(messageTextVOList)) {
            //说明redis中数据可能有问题 从数据库中查数据并且重新刷新缓存
            messageTextVOList = getMsgTextVOListByDB(pageParam, userId); //从数据库中获取数据
            refreshCache(userId);
        }

        return messageTextVOList;
    }


    /**
     * 根据id获取对应数据
     *
     * @param msgTextIdList
     * @return
     */
    private List<MessageVO> assembleMsgTextVOList(List<Long> msgTextIdList) {
        if (CollectionUtil.isEmpty(msgTextIdList)) {
            return null;
        }

        // 获取id对应的key
        List<String> detailKeyList = new ArrayList<>();
        for (Long textId : msgTextIdList) {
            detailKeyList.add(getMsgDetailKey(textId));
        }

        List<MessageVO> messageTextVOList = redisService.multiGet(detailKeyList, MessageVO.class);
        CollUtil.removeNull(messageTextVOList);
        if (CollectionUtil.isEmpty(messageTextVOList) || messageTextVOList.size() != msgTextIdList.size()) {
            //说明redis中数据有问题 从数据库中查数据并且重新刷新缓存
            return null;
        }

        return messageTextVOList;
    }


    /**
     * 从数据库中获取消息数据
     *
     * @param pageParam
     * @param userId
     * @return
     */
    private List<MessageVO> getMsgTextVOListByDB(PageParam pageParam, Long userId) {
        PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        return messageMapper.selectUserMsgList(userId);
    }
}
