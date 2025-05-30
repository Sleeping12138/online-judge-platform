package com.OjSystem.friend.service.message.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.OjSystem.common.core.constants.Constants;
import com.OjSystem.common.core.domain.PageParam;
import com.OjSystem.common.core.utils.ThreadLocalUtil;
import com.OjSystem.friend.domain.message.MessageListVO;
import com.OjSystem.friend.domain.message.MessageVO;
import com.OjSystem.friend.mapper.message.MessageMapper;
import com.OjSystem.friend.service.message.IMessageService;
import com.OjSystem.manager.MessageCacheManager;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageCacheManager messageCacheManager;

    /**
     * 获取用户消息列表
     * @param pageParam
     * @return
     */
    @Override
    public MessageListVO<MessageVO> getUserMessageList(PageParam pageParam) {
        // 获取用户id
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);

        Long total = messageCacheManager.getListSize(userId);

        List<MessageVO> messageTextVOList;
        if (total == null || total <= 0) {
            // 从数据库中查询数据
            PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize());
            messageTextVOList = messageMapper.selectUserMsgList(userId);

            messageCacheManager.refreshCache(userId);

            total = new PageInfo<>(messageTextVOList).getTotal();
        } else {
            // 从redis链上进行获取
            messageTextVOList = messageCacheManager.getMsgTextVOList(pageParam, userId);
        }

        MessageListVO<MessageVO> messageListVO = new MessageListVO<>();

        if (CollectionUtil.isEmpty(messageTextVOList)) {
            return messageListVO;
        }

        messageListVO.setTotal(total);
        messageListVO.setDataList(messageTextVOList);
        return messageListVO;
    }
}
