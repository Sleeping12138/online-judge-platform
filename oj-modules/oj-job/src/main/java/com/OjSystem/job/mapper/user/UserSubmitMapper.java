package com.OjSystem.job.mapper.user;

import com.OjSystem.job.domain.user.UserScore;
import com.OjSystem.job.domain.user.UserSubmit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserSubmitMapper extends BaseMapper<UserSubmit> {

    List<UserScore> selectUserScore(Set<Long> examIds);

}
