package com.OjSystem.friend.mapper.exam;

import com.OjSystem.friend.domain.exam.DTO.ExamListDTO;
import com.OjSystem.friend.domain.exam.Exam;
import com.OjSystem.friend.domain.exam.RankInfo;
import com.OjSystem.friend.domain.exam.VO.ExamVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
    List<ExamVO> getExamList(ExamListDTO examListDTO);

    List<RankInfo> selectExamRankList(Long examId);
}
