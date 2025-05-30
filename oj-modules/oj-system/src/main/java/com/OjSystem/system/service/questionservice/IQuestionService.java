package com.OjSystem.system.service.questionservice;

import com.OjSystem.system.domain.question.DO.QuestionDO;
import com.OjSystem.system.domain.question.DTO.AddQuestionDTO;
import com.OjSystem.system.domain.question.DTO.ChangeQuestionDTO;
import com.OjSystem.system.domain.question.DTO.QuestionDTO;
import com.OjSystem.system.domain.question.VO.QuestionDetailVO;
import com.OjSystem.system.domain.question.VO.QuestionVO;

import java.util.List;

public interface IQuestionService {
    List<QuestionDO> getQuestionList(QuestionDTO questionDTO);

    boolean addQuestion(AddQuestionDTO addQuestionDTO);

    QuestionDetailVO getDetailById(Long questionId);

    boolean changeQuestionInfo(ChangeQuestionDTO changeQuestionDTO);

    boolean deleteQuestion(Long questionId);
}
