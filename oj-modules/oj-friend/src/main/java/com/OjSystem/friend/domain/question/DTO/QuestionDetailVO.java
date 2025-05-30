package com.OjSystem.friend.domain.question.DTO;

import com.OjSystem.friend.domain.question.VO.QuestionVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionDetailVO extends QuestionVO {
    private Long timeLimit;

    private Long spaceLimit;

    private String content;

    private String defaultCode;
}
