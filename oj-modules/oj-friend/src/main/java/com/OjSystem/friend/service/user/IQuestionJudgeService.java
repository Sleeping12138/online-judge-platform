package com.OjSystem.friend.service.user;

import com.OjSystem.api.domain.VO.QuestionJudgeVO;
import com.OjSystem.friend.domain.user.DTO.QuestionJudgeDTO;

public interface IQuestionJudgeService {
    QuestionJudgeVO submit(QuestionJudgeDTO questionJudgeDTO);

    boolean submitRabbitMQ(QuestionJudgeDTO questionJudgeDTO);

    QuestionJudgeVO getExeResult(Long examId, Long questionId, String currentTime);
}
