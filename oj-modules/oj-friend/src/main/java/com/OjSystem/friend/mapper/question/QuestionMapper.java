package com.OjSystem.friend.mapper.question;

import com.OjSystem.friend.domain.question.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {


    List<Question> selectByQuestionIds(List<Long> questionList);

}
