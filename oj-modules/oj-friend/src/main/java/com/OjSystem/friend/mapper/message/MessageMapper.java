package com.OjSystem.friend.mapper.message;

import com.OjSystem.friend.domain.message.MessageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<MessageVO> {
    List<MessageVO> selectUserMsgList(Long userId);
}
