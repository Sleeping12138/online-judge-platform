package com.OjSystem.system.service.examservice;

import com.OjSystem.system.domain.exam.DTO.AddExamQuestionDTO;
import com.OjSystem.system.domain.exam.DTO.EditExamDTO;
import com.OjSystem.system.domain.exam.DTO.ExamAddDTO;
import com.OjSystem.system.domain.exam.DTO.ExamGetListDTO;
import com.OjSystem.system.domain.exam.VO.ExamDetailVO;
import com.OjSystem.system.domain.exam.VO.ExamVO;

import java.util.List;

public interface IExamService {
    List<ExamVO> getExamList(ExamGetListDTO examGetListDTO);

    String addExam(ExamAddDTO examAddDTO);

    boolean addExamQuestion(AddExamQuestionDTO addExamQuestionDTO);

    ExamDetailVO getExamDetail(Long examId);

    boolean editExamDetail(EditExamDTO editExamDTO);

    boolean deleteQuestion(Long examId, Long questionId);

    boolean deleteExam(Long examId);

    boolean publishExam(Long examId);

    boolean cancelPublishExam(Long examId);
}
