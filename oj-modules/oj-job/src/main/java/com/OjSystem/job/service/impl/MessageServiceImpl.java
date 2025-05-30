package com.OjSystem.job.service.impl;

import com.OjSystem.job.domain.message.Message;
import com.OjSystem.job.mapper.message.MessageMapper;
import com.OjSystem.job.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Override
    public boolean batchInsert(List<Message> messageList) {
        return saveBatch(messageList);
    }
}
