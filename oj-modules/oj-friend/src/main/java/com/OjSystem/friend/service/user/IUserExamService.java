package com.OjSystem.friend.service.user;


import com.OjSystem.friend.domain.exam.DTO.ExamListDTO;
import com.OjSystem.friend.domain.exam.VO.ExamListVO;
import com.OjSystem.friend.domain.exam.VO.ExamVO;

import java.util.List;

public interface IUserExamService {
    boolean enter(String token, Long examId);

    ExamListVO<ExamVO> getUserExamList(ExamListDTO examListDTO);
}
