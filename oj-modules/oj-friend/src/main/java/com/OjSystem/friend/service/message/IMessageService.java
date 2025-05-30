package com.OjSystem.friend.service.message;

import com.OjSystem.common.core.domain.PageParam;
import com.OjSystem.friend.domain.message.MessageListVO;
import com.OjSystem.friend.domain.message.MessageVO;

public interface IMessageService {
    MessageListVO<MessageVO> getUserMessageList(PageParam pageParam);
}
