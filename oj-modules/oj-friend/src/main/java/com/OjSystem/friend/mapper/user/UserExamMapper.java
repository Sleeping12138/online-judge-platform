package com.OjSystem.friend.mapper.user;

import com.OjSystem.friend.domain.exam.VO.ExamVO;
import com.OjSystem.friend.domain.user.UserExam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserExamMapper extends BaseMapper<UserExam> {
    List<ExamVO> getExamList(Long userId);
}
