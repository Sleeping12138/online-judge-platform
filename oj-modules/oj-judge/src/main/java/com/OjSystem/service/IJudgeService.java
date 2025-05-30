package com.OjSystem.service;

import com.OjSystem.api.domain.DTO.JudgeSubmitDTO;
import com.OjSystem.api.domain.VO.QuestionJudgeVO;

public interface IJudgeService {
    QuestionJudgeVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO);
}
