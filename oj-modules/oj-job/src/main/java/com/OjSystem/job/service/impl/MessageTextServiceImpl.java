package com.OjSystem.job.service.impl;

import com.OjSystem.job.domain.message.MessageText;
import com.OjSystem.job.mapper.message.MessageTextMapper;
import com.OjSystem.job.service.IMessageTextService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MessageTextServiceImpl extends ServiceImpl<MessageTextMapper, MessageText> implements IMessageTextService {

    @Override
    public boolean batchInsert(List<MessageText> messageTextList) {
        return saveBatch(messageTextList);
    }
}
