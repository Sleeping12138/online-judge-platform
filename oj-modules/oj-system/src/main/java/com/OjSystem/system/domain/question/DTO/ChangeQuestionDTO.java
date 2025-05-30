package com.OjSystem.system.domain.question.DTO;

import lombok.Data;

@Data
public class ChangeQuestionDTO {
    private Long questionId;

    private String title;

    private Integer difficulty;

    private Long timeLimit;

    private Long spaceLimit;

    private String content;

    private String questionCase;

    private String defaultCode;

    private String mainFuc;
}
