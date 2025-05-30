package com.OjSystem.system.mapper.questionmapper;

import com.OjSystem.system.domain.question.DO.QuestionDO;
import com.OjSystem.system.domain.question.DTO.QuestionDTO;
import com.OjSystem.system.domain.question.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {


    List<QuestionDO> getQuestionList(QuestionDTO questionDTO);
}
