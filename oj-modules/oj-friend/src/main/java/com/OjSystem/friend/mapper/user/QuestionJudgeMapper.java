package com.OjSystem.friend.mapper.user;

import com.OjSystem.friend.domain.user.UserSubmit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionJudgeMapper extends BaseMapper<UserSubmit> {
    UserSubmit selectCurrentUserSubmit(Long userId, Long examId, Long questionId, String currentTime);
}
