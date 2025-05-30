package com.OjSystem.friend.service.exam;

import com.OjSystem.friend.domain.exam.DTO.ExamListDTO;
import com.OjSystem.friend.domain.exam.DTO.ExamRankDTO;
import com.OjSystem.friend.domain.exam.RankInfo;
import com.OjSystem.friend.domain.exam.VO.ExamListVO;
import com.OjSystem.friend.domain.exam.VO.ExamVO;

import java.util.List;

public interface IExamService {
    List<ExamVO> getExamList(ExamListDTO examListDTO);

    ExamListVO<ExamVO> getExamListFromRedis(ExamListDTO examListDTO);

    String getQuestionOfFirst(Long examId);

    String nextQuestion(Long examId, Long questionId);

    String preQuestion(Long examId, Long questionId);

    ExamListVO<RankInfo> getExamRankInfo(ExamRankDTO examRankDTO);
}
