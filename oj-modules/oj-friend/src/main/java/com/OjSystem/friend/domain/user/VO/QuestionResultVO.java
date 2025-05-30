package com.OjSystem.friend.domain.user.VO;

import lombok.Data;

@Data
public class QuestionResultVO {
    private String input;

    private String output;   //期望输出

    private String exeOutput; //实际输出
}
