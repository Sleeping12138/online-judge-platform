package com.OjSystem.friend.service.quesiton;

import com.OjSystem.friend.domain.exam.DTO.QuestionListDTO;
import com.OjSystem.friend.domain.exam.VO.QuestionListVO;
import com.OjSystem.friend.domain.exam.VO.QuestionVO;
import com.OjSystem.friend.domain.question.DTO.QuestionDetailVO;

public interface IQuestionService {
    QuestionListVO<QuestionVO> getQuestionList(QuestionListDTO questionListDTO);

    QuestionDetailVO getQuestionInfo(Long questionId);

    String preQuestion(Long questionId);

    String nextQuestion(Long questionId);
}
