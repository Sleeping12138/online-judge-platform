package com.OjSystem.friend.controller.message;

import com.OjSystem.common.core.domain.PageParam;
import com.OjSystem.friend.domain.message.MessageListVO;
import com.OjSystem.friend.domain.message.MessageVO;
import com.OjSystem.friend.service.message.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/message")
@RestController
public class MessageController {

    @Autowired
    private IMessageService messageService;


    /**
     * 获取用户消息列表
     * @param pageParam
     * @return
     */
    @RequestMapping("/getUserMessageList")
    public MessageListVO<MessageVO> getUserMessageList(PageParam pageParam){
        return messageService.getUserMessageList(pageParam);
    }
}
