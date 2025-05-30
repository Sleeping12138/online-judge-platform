package com.OjSystem.system.mapper.exammapper;

import com.OjSystem.system.domain.exam.DTO.ExamGetListDTO;
import com.OjSystem.system.domain.exam.Exam;
import com.OjSystem.system.domain.exam.VO.ExamVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
    List<ExamVO> getExamList(ExamGetListDTO examGetListDTO);
}
