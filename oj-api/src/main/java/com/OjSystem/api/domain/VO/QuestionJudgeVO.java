package com.OjSystem.api.domain.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class QuestionJudgeVO {
    private Integer pass; // 0  未通过  1 通过

    private String exeMessage; //异常信息

    private List<QuestionResultVO> userExeResultList;

    @JsonIgnore
    private Integer score;
}
