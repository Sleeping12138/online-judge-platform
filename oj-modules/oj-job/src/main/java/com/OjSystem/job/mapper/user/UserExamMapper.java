package com.OjSystem.job.mapper.user;

import com.OjSystem.job.domain.user.UserExam;
import com.OjSystem.job.domain.user.UserScore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserExamMapper extends BaseMapper<UserExam> {

    void updateUserRankInfo(List<UserScore> userScores);
}
