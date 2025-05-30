package com.OjSystem.friend.domain.exam.DTO;

import com.OjSystem.common.core.domain.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionListDTO extends PageParam {
    private String keyword;

    private Integer difficulty;
}
